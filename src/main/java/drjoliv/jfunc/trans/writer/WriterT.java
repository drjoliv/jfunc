package drjoliv.jfunc.trans.writer;

import static drjoliv.jfunc.hlist.T2.t2;
import static drjoliv.jfunc.monad.Identity.id;

import drjoliv.jfunc.applicative.Applicative;
import drjoliv.jfunc.applicative.ApplicativePure;
import drjoliv.jfunc.contorl.Eval;
import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.hkt.Hkt2;
import drjoliv.jfunc.hkt.Hkt3;
import drjoliv.jfunc.hlist.T2;
import drjoliv.jfunc.monad.Identity;
import drjoliv.jfunc.monad.Monad;
import drjoliv.jfunc.monad.MonadUnit;
import drjoliv.jfunc.monoid.Monoid;


/**
 * The WriterT is a monad that produces a stream of data in addition to a computed value, the stream of subcomputations are collected by combining them via a monoid.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public abstract class WriterT <M,W,A> implements Monad<Hkt2<WriterT.μ,M,W>,A>, Hkt3<WriterT.μ,M,W,A> {

 /**
  * Witness type of WriterT.
  */
  public static class μ {private μ(){}}

  private final Monad<M,T2<A,W>> runWriterT;
  private final Monoid<W> monoid;
  private final MonadUnit<M> mUnit;

  private WriterT(Monad<M,T2<A,W>> runWriterT, Monoid<W> monoid, MonadUnit<M> mUnit) {
    this.runWriterT = runWriterT;
    this.monoid     = monoid;
    this.mUnit      = mUnit;
  }

  @Override
  public abstract <B> WriterT<M,W,B> map(F1<? super A, ? extends B> fn);

  @Override
  public abstract <B> WriterT <M, W, B> apply(Applicative<Hkt2<μ, M, W>, ? extends F1<? super A, ? extends B>> applicative);

  @Override
  public abstract ApplicativePure<Hkt2<μ, M, W>> pure();

  @Override
  public abstract <B> WriterT<M,W,B> bind(F1<? super A, ? extends Monad<Hkt2<μ, M, W>, B>> fn);

  @Override
  public abstract <B> WriterT<M,W,B> semi(Monad<Hkt2<μ, M, W>, B> mb);

  @Override
  public abstract MonadUnit<Hkt2<μ, M, W>> yield();

  /**
   * Returns the monoid used by this WriterT.
   * @return the monoid used by this WriterT.
   */
  public final Monoid<W> monoid() {
    return monoid;
  }

  /**
   * Returns the inner monad containing two values, the main computaion {@code A } and the accumulated subcomputations {@code W}.
   * @return the inner monad containing two values the main computaion {@code A } and the accumulated subcomputations {@code W}.
   */
  public Monad<M,T2<A,W>> runWriterT(){
    return runWriterT;
  }

  /**
   * Returns the inner monad containing the accumulated subcomputations {@code W}.
   * @return the inner monad containing the accumulated subcomputations {@code W}.
   */
  public Monad<M,W> execWriterT(){
    return runWriterT.map(T2::_2);
  }

  <D> Monad<M,D> u(D d) {
    return mUnit.unit(d);
  }

  MonadUnit<M> unit() {
    return mUnit;
  }

  private static class WriterTImpl<M,W,A> extends WriterT<M,W,A>  implements Monad<Hkt2<WriterT.μ,M,W>,A>, Hkt3<WriterT.μ,M,W,A> {

    private WriterTImpl(Monad<M,T2<A,W>> runWriterT, Monoid<W> monoid, MonadUnit<M> mUnit) {
      super(runWriterT, monoid, mUnit);
    }

    @Override
    public MonadUnit<Hkt2<μ, M, W>> yield() {
      MonadUnit<M> u = unit();
      Monoid<W> m = monoid();
      return new MonadUnit<Hkt2<μ, M, W>>(){
        @Override
        public <B> Monad<Hkt2<μ, M, W>, B> unit(B b) {
          return new WriterTImpl<M,W,B>(u.unit(t2(b, m.mempty())), m, u );
        }
      };
    }

    @Override
    public <B> WriterTImpl<M,W,B> map(F1<? super A, ? extends B> fn) {
      Monad<M,T2<B,W>> mb = runWriterT().map(t -> t.map1(fn));
      return new WriterTImpl<M,W,B>(mb, monoid(), unit());
    }

    @Override
    public <B> WriterT <M, W, B> apply(Applicative<Hkt2<μ, M, W>, ? extends F1<? super A, ? extends B>> applicative) {
      Monad<M,T2<F1<? super A, B>,W>> mf = ((WriterT <M, W, F1<? super A, B>>) applicative).runWriterT();
      Monad<M,T2<A,W>>                ma = runWriterT();
      Monad<M,T2<B,W>> mb =
        Monad.For(mf
            , tf      -> ma
            ,(tf,ta)  -> {
              A a = ta._1();
              W w = ta._2();
              return unit().unit(tf.bimap(f -> f.call(a) , ww -> monoid().mappend(w,ww)));
          });
      return new WriterTImpl<>(mb, monoid(), unit());
    }

    @Override
    public ApplicativePure<Hkt2<μ, M, W>> pure() {
      return new ApplicativePure<Hkt2<μ, M, W>>(){
        @Override
        public <B> Applicative<Hkt2<μ, M, W>, B> pure(B b) {
          return new WriterTImpl<M,W,B>(unit().unit(t2(b, monoid().mempty())), monoid(), unit());
        }
      };
    }

    @Override
    public <B> WriterT<M,W,B> bind(F1<? super A, ? extends Monad<Hkt2<μ, M, W>, B>> fn) {
      Monad<M,T2<B,W>> mb
        = Monad.For(runWriterT()
            ,  a    -> monad(fn.call(a._1())).runWriterT()
            , (a,b) -> {
              Monad<Eval.μ, W> obj = Monad.liftM2(a._2, b._2, monoid()::mappend);
              return u(b.set2(Eval.monad(obj)));
            });
      return new WriterTImpl<M,W,B>(mb , monoid(), unit());
    }

    @Override
    public <B> WriterT<M,W,B> semi(Monad<Hkt2<μ, M, W>, B> mb) {
      return bind(a -> mb);
    }
  }

  /**
  * Writer is a WriterT whose inner monad is the identity monad.
  */
  public static final class Writer<W,A> extends WriterT<Identity.μ,W,A> {

    private Writer(Monad<Identity.μ,T2<A,W>> runWriterT, Monoid<W> monoid) {
      super(runWriterT, monoid, Identity::id);
    }

    @Override
    public <B> Writer<W,B> map(F1<? super A, ? extends B> fn) {
      return new Writer<W,B>(runWriterT().map(t -> t.map1(fn)), monoid());
    }

    @Override
    public <B> Writer<W, B> apply(Applicative<Hkt2<μ, drjoliv.jfunc.monad.Identity.μ, W>, ? extends F1<? super A, ? extends B>> applicative) {
        Identity<T2<F1<? super A, B>,W>> wf = isWriter(applicative)
          ? ((Writer<W,F1<? super A, B>>) applicative).runWriterT()
          : Identity.monad(((WriterT<Identity.μ,W,F1<? super A, B>>) applicative).runWriterT());

        Identity<T2<B,W>> mb = wf.bind(tf -> {
          final F1<? super A, B> f = tf._1();
          final W ww = tf._2();

          return runWriterT().map(t ->  t.bimap(a -> f.call(a), w -> monoid().mappend(w,ww))); 
        });

      return new Writer<W,B>(mb, monoid());
    }

    @Override
    public ApplicativePure<Hkt2<μ, drjoliv.jfunc.monad.Identity.μ, W>> pure() {
      return new ApplicativePure<Hkt2<μ, drjoliv.jfunc.monad.Identity.μ, W>>(){
        @Override
        public <B> Applicative<Hkt2<μ, drjoliv.jfunc.monad.Identity.μ, W>, B> pure(B b) {
          return new Writer<W,B>(id(t2(b, monoid().mempty())), monoid());
        }
      };
    }

    @Override
    public <B> Writer<W,B> bind(
        F1<? super A, ? extends Monad<Hkt2<μ, drjoliv.jfunc.monad.Identity.μ, W>, B>> fn) {
      Identity<T2<B,W>> mb =
        Identity.For(runWriterT()
                  , t       ->  monad(fn.call(t._1())).runWriterT()
                  , (ta,tb) ->  {
                    Eval<W> w = Eval.liftM2(ta._2, tb._2, monoid()::mappend);
                    return u(tb.set2(w));
                  });
      return new Writer<W,B>(mb,monoid());
    }

    @Override
    public <B> Writer<W,B> semi(
      final Monad<Hkt2<μ, drjoliv.jfunc.monad.Identity.μ, W>, B> mb) {
      return bind(a -> mb);
    }

    @Override
    public MonadUnit<Hkt2<μ, drjoliv.jfunc.monad.Identity.μ, W>> yield() {
      return new MonadUnit<Hkt2<μ, drjoliv.jfunc.monad.Identity.μ, W>>(){
        @Override
        public <B> Monad<Hkt2<μ, drjoliv.jfunc.monad.Identity.μ, W>, B> unit(B b) {
          return new Writer<W,B>(id(t2(b, monoid().mempty())), monoid());
        }
      };
    }

    @Override
    public Identity<T2<A, W>> runWriterT() {
      return (Identity<T2<A, W>>)super.runWriterT();
    }

    /**
     * Returns the unwrapped writer monad, yielding the return value {@code A} and accumulated subcomputations {@code W}.
     * @return the unwrapped writer monad, yielding the return value {@code A} and accumulated subcomputations {@code W}.
     */
    public T2<A,W> run() {
      return runWriterT().value();
    }

    /**
     * Returns the accumulated subcomputations.
     * @return the accumulated subcomputations.
     */
    public W log() {
      return runWriterT().value()._2();
    }

    /**
     * Returns the return value.
     * @return the return value.
     */
    public A exec() {
      return runWriterT().value()._1();
    }

    private static boolean isWriter(Object obj) {
      return obj instanceof Writer;
    }
  }

  /**
   * Creates a writer.
   * @param a a return value.
   * @param w a subcomuptation.
   * @param m a monoid used to combine subcomputation.
   * @param <W> subcomputation type.
   * @param <A> return value type.
   * @return a wrtier.
   */
  public static <W,A> Writer<W,A> writer(A a, W w, Monoid<W> m) {
    return new Writer<W,A>(id(t2(a,w)), m);
  }

  /**
   * Creates a writerT.
   * @param a the return value
   * @param w the  subcomputation.
   * @param unit a strategy for lift values into the contect of the inner monad.
   * @param mw a monoid used to combine subcomputations.
   * @return a writerT.
   */
  public static <A,W,M> WriterT<M,W,A> writer(A a, W w, MonadUnit<M> unit, Monoid<W> mw) {
    return  new WriterTImpl<>(unit.unit(t2(a,w)), mw, unit);
  }


  /**
   * Creates a strategy for constructing writers.
   * @param m a monoid used to combine subcomputations of the created writer.
   * @return a strategy for creating writers.
   */
  public static final <W> MonadUnit<Hkt2<WriterT.μ,Identity.μ,W>> WRITER_UNIT(Monoid<W> m) {
    return new MonadUnit<Hkt2<WriterT.μ,Identity.μ,W>>(){
      @Override
      public <A> Monad<Hkt2<μ, drjoliv.jfunc.monad.Identity.μ, W>, A> unit(A a) {
        return new Writer<W,A>(id(t2(a, m.mempty())), m);
      }
    };
  }


  /**
   * Creates a strategy for constructing writers.
   * @param m the monoid used to combine subcmoputations of the created writer.
   * @param unit a strategy for lifting values into the inner monad.
   * @return a strategy for creating writers.
   */
  public static final <M, W> MonadUnit<Hkt2<WriterT.μ,M,W>> WRITERT_MONAD_UNIT(Monoid<W> m, MonadUnit<M> unit) {
    return new MonadUnit<Hkt2<WriterT.μ,M,W>>(){
      @Override
      public <A> Monad<Hkt2<μ, M, W>, A> unit(A a) {
        return new WriterTImpl<M,W,A>(unit.unit(t2(a, m.mempty())), m, unit);
      }
    };
  }

  /**
  * Converts a instnace of bind whose witness type is {@code Hkt2<Writer.μ, Identity.μ, W>} to a instance of writer.
  * @param mb the argument to convert to an instance of writer.
  * @return a writer.
  */
  public static <W, A> Writer<W,A> asWriter(Monad<Hkt2<WriterT.μ, Identity.μ, W>, A> mb) {
    if(mb instanceof WriterT)
      return new Writer<W,A>(monad(mb).runWriterT, monad(mb).monoid);
    else
      return (Writer<W,A>) mb;
  }
  
  /**
   * A helper function to convert/narrow a reference from a monad to its underlying type.
   * @param monad the writerT to be casted to its original type.
   * @return a writerT.
   */
  public static <M,W,A> WriterT<M,W,A> monad(Monad<Hkt2<WriterT.μ,M,W>,A> monad) {
    return (WriterT<M,W,A>)monad;
  }
}
