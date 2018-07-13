package drjoliv.jfunc.contorl;

import static drjoliv.jfunc.contorl.Either.left;
import static drjoliv.jfunc.contorl.Either.right;
import static drjoliv.jfunc.contorl.Eval.later;
import static drjoliv.jfunc.contorl.Eval.now;
import static drjoliv.jfunc.contorl.Trampoline.done;
import static drjoliv.jfunc.monad.Monad.For;

import drjoliv.jfunc.applicative.Applicative;
import drjoliv.jfunc.applicative.ApplicativePure;
import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.function.F2;
import drjoliv.jfunc.function.Try0;
import drjoliv.jfunc.hkt.Hkt;
import drjoliv.jfunc.hkt.Hkt2;
import drjoliv.jfunc.monad.Monad;
import drjoliv.jfunc.monad.MonadUnit;

/**
 * Softens exceptions by wrapping thrown exceptions into the a Failure instance
 * of Try and wrapping success into Success.
 *
 * @author Desonte 'drjoliv' Jolivete : drjoliv@gmail.com
 */
public class Try<A> implements Monad<Try.μ,A>, Hkt<Try.μ,A> {

  /**
  * The witness type of {@code Try}.
  */
  public static class μ {private μ(){}}

  private final Trampoline<Either<Exception,A>> trampoline;

  private Try(Trampoline<Either<Exception,A>> trampoline) {
    this.trampoline = trampoline; 
  }

  @Override
  public <B> Try<B> map(F1<? super A, ? extends B> fn) {
    Trampoline<Either<Exception,B>> ret = trampoline.map(e -> {
      return e.bimap(F1.<Exception>identity(), fn);
    });
    return new Try<>(ret);
  }

  @Override
  public <B> Try<B> apply(Applicative<μ, ? extends F1<? super A, ? extends B>> f) {
    Trampoline<Either<Exception ,F1<? super A, ? extends B>>> tramp
      = ((Try<F1<? super A, ? extends B>>)f).trampoline;
    Monad<Trampoline.μ,Either<Exception,B>> ret = 
    Monad.For(tramp
      , ef     -> trampoline
      ,(ef,e) -> ef.match(l -> done(left(l.value()))
                        , r -> done(e.bimap(F1.identity(),r.value()))));
    return new Try<>(Trampoline.monad(ret));
  }

  @Override
  public ApplicativePure<μ> pure() {
    return Try::success;
  }

  @Override
  public <B> Try<B> bind(F1<? super A, ? extends Monad<μ, B>> fn) {
    Trampoline<Either<Exception,B>> mb = trampoline.bind(e -> e.match(
          l -> done(left(l.value()))
        , r -> monad(fn.call(r.value())).trampoline));
    return new Try<>(mb);
  }

  @Override
  public <B> Try<B> semi(Monad<μ, B> mb) {
    return bind(a -> mb);
  }

  @Override
  public MonadUnit<μ> yield() {
    return Try::success;
  }

  /**
   * Runs this Try returning it result.
   * If an exception was thrown and not recovered from the either will contain an exception or the result otherwise.
   * @return an either containing an exception or the result.
   */
  public Either<Exception,A> run() {
    return trampoline.result();
  }

  public A get() throws Exception {
    Either<Exception, A> e = run();
    Maybe<A> ma = e.valueR();
    if(ma.isSome())
      return ma.toNull();
    else
      throw e.valueL().toNull();
  }

  /**
   * Recovers with the given try if this try is a failure.
   * @param t a try to recover with if this try is a failure.
   * @return a try that recovers with the given arugment if this is a failure or this ortherwise.
   */
  public Try<A> recoverWith(Try<A> t) {
    final Trampoline<Either<Exception,A>> ma = trampoline;
    final Trampoline<Either<Exception,A>> mb = t.trampoline;
    Monad<Trampoline.μ,Either<Exception,A>> ret = Monad.For(ma
            , a    -> mb
            ,(a,b) -> a.match(ex -> b.match(ll -> ma, rr -> mb)
                            , r  -> ma));

    return new Try<>(Trampoline.monad(ret));
  }

  /**
   * Recovers with a try created from a function if this try is a failure and if the failure's
   * exception matches the given excpetion class.
   * @param cls the class of the excpetion to match against
   * @param fn the function used to generate a try to recover with.
   * @return a try created from a function if this try is a failure.
   */
  public <E extends Exception> Try<A> recoverWith(Class<E> cls, F1<E, Try<A>> fn) {
    final Trampoline<Either<Exception,A>> ret = 
      trampoline.bind(e -> e.match(l -> {
        Class<?> c  = l.value().getClass();
        boolean bool = c.equals(cls);
        return (bool || cls.isInstance(l.value()))
        ? fn.call((E)l.value()).trampoline
        : done(l);
      }
      , r -> done(r)));
    return new Try<>(Trampoline.monad(ret));
  }

  /**
   * Recovers from a try of failure with the given argument the exception within the failure matches the class given.
   * @param cls the class of the exception to match against.
   * @param a the value injected into a try used to recover with.
   * @return a try injected with {@code a} if this is af failure or returns this otherwise.
   */
  public Try<A> recover(Class<? extends Exception> cls, A a) {
    Trampoline<Either<Exception,A>> ret = 
    trampoline.map(e -> e.match( l -> l.value()
                                       .getClass()
                                       .equals(cls) ? right(a) : l
                               , r -> r));
    return new Try<>(ret);
  }

  
  /**
   * A helper function to convert/narrow a reference from an applicative to its underlying type.
   * @param applicative the try to be casted to its original type.
   * @return a try.
   */
  public static <B> Try<B> applicative(Applicative<μ, B> applicative) {
    return (Try<B>)applicative;
  }

  /**
   * Converts a {@code Try0} into a {@code Try}.
   * @param runTry a try0.
   * @return a try
   */
  public static <A,E extends Exception> Try<A> with(Try0<A,E> runTry) {
    Trampoline<Either<Exception,A>> tramp = Trampoline.done$(() -> {
        try {
          return right(runTry.get());
        } catch(Exception ex) {
          return left(ex);
        }
      });
    return new Try<>(tramp);
  }

  /**
  * A strategy for lifting a value into a try.
  */
  public static final  MonadUnit<Try.μ> MONAD_UNIT = Try::success;

  /**
   * Creates a Try of success containing the given argument.
   * @param b the value to wrapped into a try.
   * @return a try of success.
   */
  public static <B> Try<B> success(B b) {
    return new Try<>(done(right(b)));
  }

  /**
   * Creates a try of failure containing the given argument.
   * @param ex the exception to be wrapped into a try.
   * @return a try of failure. 
   */
  public static <B> Try<B> failure(Exception ex) {
    return new Try<>(done(left(ex)));
  }

  /**
   *
   * A helper function to convert/narrow a reference from an applicative to its underlying type.
   * @param monad the try to be casted to its original type.
   * @return a try.
   */
  public static <A> Try<A> monad(Monad<Try.μ,A> monad) {
    return (Try<A>) monad;
  }

  /**
   * The monad transformer for Try.
   */
  public static class TryT<M,A> implements Monad<Hkt<TryT.μ,M>, A>, Hkt2<TryT.μ,M,A> {

    /**
    * The witness type of TryT.
    */
    public static class μ {private μ(){}}

    private final Eval<Monad<M,Try<A>>> runTryT;
    private final MonadUnit<M> mUnit;

    private TryT(Try<A> t, MonadUnit<M> mUnit) {
      this.runTryT = now(mUnit.unit(t));
      this.mUnit   = mUnit;
    }

    private TryT(Monad<M,Try<A>> t) {
      this.runTryT = now(t);
      this.mUnit   = t.yield();
    }

    private TryT(Eval<Monad<M,Try<A>>> runTryT, MonadUnit<M> mUnit) {
      this.runTryT = runTryT;
      this.mUnit   = mUnit;
    }

    @Override
    public <B> TryT<M, B> apply(
        Applicative<Hkt<drjoliv.jfunc.contorl.Try.TryT.μ, M>, ? extends F1<? super A, ? extends B>> applicative) {
         Eval<Monad<M,Try<B>>> eval_mb = later(() -> {
          Monad<M, Try<F1<? super A, B>>> f = ((TryT<M,F1<? super A, B>>)applicative).runTryT();
          Monad<M, Try<A>> v = runTryT();
          F2< Try<F1<? super A, B>>, Try<A>, Try<B>> go = (tf, t) -> t.apply(tf);
          Monad<M,Try<B>> mb = Monad.liftM2(f, v, go);
          return mb;
         });
         return new TryT<>(eval_mb, mUnit);
    }

    @Override
    public ApplicativePure<Hkt<drjoliv.jfunc.contorl.Try.TryT.μ, M>> pure() {
      return new ApplicativePure<Hkt<drjoliv.jfunc.contorl.Try.TryT.μ, M>>(){
        @Override
        public <B> Applicative<Hkt<drjoliv.jfunc.contorl.Try.TryT.μ, M>, B> pure(B b) {
          return new TryT<M,B>(mUnit.unit(success(b)));
        }
      };
    }

    @Override
    public <B> TryT<M,B> map(F1<? super A, ? extends B> fn) {
      return new TryT<M,B>(runTryT.map(m -> m.map(t -> t.map(fn))), mUnit); 
    }

    @Override
    public <B> TryT<M,B> bind(F1<? super A, ? extends Monad<Hkt<μ, M>, B>> fn) {
      Eval<Monad<M,Try<B>>> eval_mb = runTryT.map(ma -> {
        return Monad.For(ma
          ,ta -> ta.run().match(l -> mUnit.unit(failure(l.value()))
                               ,r -> monad(fn.call(r.value())).runTryT()));
      });
      return new TryT<M,B>(eval_mb, mUnit);
    }

    @Override
    public <B> TryT<M,B> semi(Monad<Hkt<μ, M>, B> mb) {
      return bind(a -> mb);
    }

    @Override
    public MonadUnit<Hkt<μ, M>> yield() {
      return new MonadUnit<Hkt<μ, M>>() {
        @Override
        public <B> Monad<Hkt<μ, M>, B> unit(B b) {
          return new TryT<M,B>(mUnit.unit(success(b)));
        }
      };
    }

    /**
    * Returns the inner monad containing a try.
    * @return the inner monad containing a try.
    */
    public Monad<M,Try<A>> runTryT() {
      return runTryT.value();
    }

    /**
     * Returns a tryt containing a monad injected with {@code b}.
     * @param b a value injected into a a tryT
     * @param mUnit a strategy for lifting a value into a monad.
     * @return a tryt.
     */
    public static <M, B> TryT<M,B> tryT(B b, MonadUnit<M> mUnit) {
      return new TryT<>(success(b), mUnit);
    }

    /**
     * Converts an inner monad containing a try into a tryT.
     * @param t a monad containing a try.
     * @return a tryt.
     */
    public static <M, B> TryT<M,B> tryT(Monad<M,Try<B>> t) {
      return new TryT<>(t);
    }

    /**
     * Lifts a monad into TryT.
     * @param t a monad.
     * @return a tryT.
     */
    public static <M, B> TryT<M,B> lift(Monad<M,B> t) {
      return new TryT<M,B>(t.map(b -> success(b)));
    }

    /**
     * A helper function to convert/narrow a reference from an applicative to its underlying type.
     * @param f the applicative to be casted to its original type.
     * @return a tryT.
     */
    public static <M, B> TryT<M,B> applicative(Applicative<Hkt<drjoliv.jfunc.contorl.Try.TryT.μ, M>, B> f) {
      return (TryT<M,B>) f;
    }

   /**
     * A helper function to convert/narrow a reference from a monad to its underlying type.
    * @param monad the monad to be casted to its original type.
    * @return a tryT.
    */
    public static <M, B> TryT<M, B> monad(Monad<Hkt<μ, M>, B> monad) {
      return (TryT<M, B>) monad;
    }
  }

}
