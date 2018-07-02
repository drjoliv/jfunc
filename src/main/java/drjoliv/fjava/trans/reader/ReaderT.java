package drjoliv.fjava.trans.reader;

import static drjoliv.fjava.adt.FList.flist;

import java.util.function.Function;

import drjoliv.fjava.adt.FList;
import drjoliv.fjava.adt.Maybe;
import drjoliv.fjava.adt.Unit;
import drjoliv.fjava.applicative.Applicative;
import drjoliv.fjava.applicative.ApplicativePure;
import drjoliv.fjava.functions.F1;
import drjoliv.fjava.functions.F2;
import drjoliv.fjava.hkt.Hkt2;
import drjoliv.fjava.hkt.Hkt3;
import drjoliv.fjava.hkt.Witness;
import drjoliv.fjava.monad.Monad;
import drjoliv.fjava.monad.MonadUnit;
import drjoliv.fjava.monad.Identity;

/**
 * The ReaderT monad.
 * @author Desonte 'drjoliv' Jolivet
 */
public abstract class ReaderT <M extends Witness,R,A> implements Monad<Hkt2<ReaderT.μ,M,R>,A>, Hkt3<ReaderT.μ,M,R,A> {

  public static class μ implements Witness{private μ(){}}

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

  public static <M extends Witness,R,B> ReaderT<M,R,B> monad(Monad<Hkt2<μ, M, R>, B> monad) {
    return (ReaderT<M,R,B>)monad;
  }

  private static class ReaderTmpl<M extends Witness,R,A> extends ReaderT<M,R,A> {

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
        Monad<M,Monad<Hkt2<μ, M, R>, B>> mab = ma.map(fn);
        Monad<M, ReaderT<M, R, B>> mabb = mab.map(ReaderT::monad);
        Monad<M, Monad<M,B>> mba = mabb.map(rr -> rr.runReader(r));
        Monad<M,B> mb = Monad.join(mba);
        return new ReaderTmpl<M,R,B>(env -> mb, mUnit());
    }

    @Override
    ReaderT<M, R, A> step(R r) {
      return this;
    }
  }

  private static class ReaderTBind<M extends Witness,R,A,B> extends ReaderT<M,R,B>  {

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

  public static <M extends Witness,R,A> ReaderT<M,R,A> reader(F1<R,Monad<M,A>> fn, MonadUnit<M> mUnit) {
    return new ReaderTmpl<>(fn, mUnit);
  };


  public static void main(String [] args) {

    ReaderT<Identity.μ, Integer, Integer> rt = reader(i -> Identity.id(i), null);

    F1<Integer,ReaderT<Identity.μ,Integer, Integer>> add = a ->  reader(i -> {
      //System.out.println("i hope this is lazy");
      return Identity.id(i + a);
    }, null);

    for (int i = 0; i < 1000000; i++) {
      rt = rt.bind(add);
    }

    long now = System.currentTimeMillis();
    Monad<Identity.μ, Integer> m = rt.runReader(1);
    System.out.println(Identity.monad(m).value());
    System.out.println(System.currentTimeMillis() - now);
  }
}
