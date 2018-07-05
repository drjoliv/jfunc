package drjoliv.jfunc.trans.reader;

import drjoliv.jfunc.applicative.Applicative;
import drjoliv.jfunc.applicative.ApplicativePure;
import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.hkt.Hkt2;
import drjoliv.jfunc.hkt.Hkt3;
import drjoliv.jfunc.monad.Identity;
import drjoliv.jfunc.monad.Monad;
import drjoliv.jfunc.monad.MonadUnit;

/**
 * The ReaderT monad.
 * @author Desonte 'drjoliv' Jolivet
 */
public abstract class ReaderT <M,R,A> implements Monad<Hkt2<ReaderT.μ,M,R>,A>, Hkt3<ReaderT.μ,M,R,A> {

  public static class μ {private μ(){}}

  private final MonadUnit<M> mUnit;

  ReaderT(MonadUnit<M> mUnit) {
    this.mUnit = mUnit;
  }

  <B> MonadUnit<M> mUnit() {
    return mUnit;
  }

  <C> Monad<M,C>  mUnit(C c) {
    return mUnit.unit(c);
  }

  public <B> ReaderT<M,R,B> apply(Applicative<Hkt2<μ,M,R>, ? extends F1<? super A, ? extends B>> app) {
    return applicative(app).bind(f -> map(f));
  }

  public <B> ReaderT<M,R,B> applicative(Applicative<Hkt2<μ,M,R>, B> app) {
    return (ReaderT<M,R,B>)app;
  }

  @Override
  public <B> ReaderT<M,R,B> bind(F1<? super A, ? extends Monad<Hkt2<μ, M, R>, B>> fn) {
    return new ReaderTBind<M,R,A,B>(this, fn, mUnit());
  }

  @Override
  public <B> ReaderT<M,R,B> map(F1<? super A, ? extends B> fn) {
    return bind(a -> reader(env -> mUnit(fn.call(a)), mUnit()));
  }

  @Override
  public <B> ReaderT<M,R,B> semi(Monad<Hkt2<μ, M, R>, B> mb) {
    return bind(a -> mb);
  }


  @Override
  public MonadUnit<Hkt2<μ, M, R>> yield() {
    return new MonadUnit<Hkt2<μ, M, R>>(){
      @Override
      public <C> Monad<Hkt2<μ, M, R>, C> unit(C c) {
        return new ReaderTmpl<>(env -> mUnit(c), mUnit());
      }
    };
  }

  @Override
  public ApplicativePure<Hkt2<μ, M, R>> pure() {
    return new ApplicativePure<Hkt2<μ, M, R>>(){
      @Override
      public <C> Applicative<Hkt2<μ, M, R>, C> pure(C c) {
        return new ReaderTmpl<>(env -> mUnit(c), mUnit());
      }
    };
  }


  public Monad<M,A> runReader(R r) {
   ReaderT reader = this;
    while(reader instanceof ReaderTBind) {
      reader = reader.step(r);
    }
    return (Monad<M,A>)reader.call(r);
  }

  abstract ReaderT<M,R,A> step(R r);

  abstract Monad<M,A> call(R r);

  abstract <B> ReaderT<M,R,B> doBind(R r, F1<? super A, ? extends Monad<Hkt2<μ, M, R>, B>> fn);

  public static <M,R,B> ReaderT<M,R,B> monad(Monad<Hkt2<μ, M, R>, B> monad) {
    return (ReaderT<M,R,B>)monad;
  }

  private static class ReaderTmpl<M,R,A> extends ReaderT<M,R,A> {

    private final F1<R,Monad<M,A>> fn;

    private ReaderTmpl(F1<R, Monad<M, A>> fn, MonadUnit<M> mUnit) {
      super(mUnit);
      this.fn = fn;
    }

    public Monad<M,A> runReader(R r) {
      return fn.call(r);
    }

    @Override
    Monad<M,A> call(R r) {
      return fn.call(r);
    }

    @Override
    <B> ReaderT<M, R, B> doBind(R r, F1<? super A, ? extends Monad<Hkt2<μ, M, R>, B>> fn) {
      
        Monad<M,A> ma = this.fn.call(r);
        Monad<M,B> mab = Monad.join(ma.map(a -> ((ReaderT<M,R,B>)fn.call(a)).runReader(r) ));
        return new ReaderTmpl<M,R,B>(env -> mab, mUnit());
    }

    @Override
    ReaderT<M, R, A> step(R r) {
      return this;
    }
  }

  private static class ReaderTBind<M,R,A,B> extends ReaderT<M,R,B>  {

    private final ReaderT<M,R,A> reader;
    private final F1<? super A, ? extends Monad<Hkt2<μ,M, R>, B>> fn;//[A -> R -> B]

    private ReaderTBind(ReaderT<M,R,A> reader, F1<? super A, ? extends Monad<Hkt2<μ,M, R>, B>> binder
        , MonadUnit<M> mUnit) {
      super(mUnit);
      this.reader = reader;
      this.fn = binder;
    }

    public ReaderT<M,R,B> step(R r) {
      return reader.doBind(r, fn);
    }

    public <C> ReaderT<M,R,C> doBind(R r, F1<? super B, ? extends Monad<Hkt2<μ,M, R>, C>> binder) {
      return reader.bind(a -> {
        ReaderT<M, R, B> mb = monad(fn.call(a));
        return mb.bind(binder);
      });
    }

    @Override
    public Monad<M,B> call(R r) {
      throw new UnsupportedOperationException();
    }
  }

  public static <M,R,A> ReaderT<M,R,A> reader(F1<R,Monad<M,A>> fn, MonadUnit<M> mUnit) {
    return new ReaderTmpl<>(fn, mUnit);
  };


  //public static void main(String [] args) {

  //  ReaderT<Identity.μ, Integer, Integer> rt = reader(i -> { System.out.println("rt"); return Identity.id(i);}, null);

  //  F1<Integer,ReaderT<Identity.μ,Integer, Integer>> add = a ->  reader(i -> {
  //    return Identity.id(i + a);
  //  }, null);

  //  for (int i = 1; i < 10000; i++) {
  //    rt = rt.bind(add);
  //  }

  //  long now = System.currentTimeMillis();
  //  Monad<Identity.μ, Integer> m = rt.runReader(3);
  //  System.out.println(Identity.monad(m).value());
  //  System.out.println(System.currentTimeMillis() - now);
  //}
}
