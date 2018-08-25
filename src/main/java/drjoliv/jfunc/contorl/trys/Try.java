package drjoliv.jfunc.contorl.trys;

import static drjoliv.jfunc.contorl.either.Either.left;
import static drjoliv.jfunc.contorl.either.Either.right;
import static drjoliv.jfunc.contorl.tramp.Trampoline.done;

import drjoliv.jfunc.applicative.Applicative;
import drjoliv.jfunc.applicative.ApplicativeFactory;
import drjoliv.jfunc.contorl.either.Either;
import drjoliv.jfunc.contorl.maybe.Maybe;
import drjoliv.jfunc.contorl.tramp.Trampoline;
import drjoliv.jfunc.contorl.trys.Try.μ;
import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.function.F2;
import drjoliv.jfunc.function.F3;
import drjoliv.jfunc.function.F4;
import drjoliv.jfunc.function.Try0;
import drjoliv.jfunc.hkt.Hkt;
import drjoliv.jfunc.monad.Monad;
import drjoliv.jfunc.monad.MonadFactory;

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
  public ApplicativeFactory<μ> pure() {
    return TryMonadFactory.instance();
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
  public <B, C> Try<C> For(F1<? super A, ? extends Monad<μ, B>> fn,
      F2<? super A, ? super B, ? extends Monad<μ, C>> fn2) {
    return monad(Monad.super.For(fn, fn2));
  }

  @Override
  public <B, C, D> Try<D> For(F1<? super A, ? extends Monad<μ, B>> fn,
      F2<? super A, ? super B, ? extends Monad<μ, C>> fn2,
      F3<? super A, ? super B, ? super C, ? extends Monad<μ, D>> fn3) {
    return monad(Monad.super.For(fn, fn2, fn3));
  }

  @Override
  public <B, C, D, E> Try<E> For(F1<? super A, ? extends Monad<μ, B>> fn,
      F2<? super A, ? super B, ? extends Monad<μ, C>> fn2,
      F3<? super A, ? super B, ? super C, ? extends Monad<μ, D>> fn3,
      F4<? super A, ? super B, ? super C, ? super D, ? extends Monad<μ, E>> fn4) {
    return monad(Monad.super.For(fn, fn2, fn3, fn4));
  }


  @Override
  public MonadFactory<μ> yield() {
    return TryMonadFactory.instance();
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
          if(ex instanceof RuntimeException)
            throw (RuntimeException)ex;
          else
            return left(ex);
        }
      });
    return new Try<>(tramp);
  }

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
}
