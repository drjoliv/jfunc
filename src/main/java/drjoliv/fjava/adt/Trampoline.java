package drjoliv.fjava.adt;

import static drjoliv.fjava.unsafe.Usafe.cast;

import drjoliv.fjava.applicative.Applicative;
import drjoliv.fjava.applicative.ApplicativePure;
import drjoliv.fjava.functions.F0;
import drjoliv.fjava.functions.F1;
import drjoliv.fjava.functions.F2;
import drjoliv.fjava.monad.Monad;
import drjoliv.fjava.monad.MonadUnit;

/**
 * A container for stackless computation.
 *
 * When writing recursive computations you can use Trampolines to avoid nasty stack overflows.
 *
 * Below is an example of a simple factorial function. Each time fact is called a new frame is put onto the stack, trampolines help us
 * avoid this.
 *
 * <pre>{@code
 * public static Long fact(Long n) {
 *  if(n == 0L)
 *    return 1L;
 *  else if(n == 1L)
 *    return 1L;
 *  else
 *    return n * fact(n - 1);
 * }}</pre>
 * 
 * The below code snippet is factorial rewritten using a trampoline. (Note: The below implementation was written to show the stackless nature of
 * trampoilnes not to write a fast implementation of factorial.) 
 *
  * <pre>{@code
 * public static Trampoline<Long> tFact(Long n) {
 *  if(n == 0L)
 *    return done(1L);
 *  else if(n == 1L)
 *    return done(1L);
 *  else {
 *    return more(() -> tFact(n -1).map(i -> n.multiply(i)));
 *  }
 * }}</pre>
*
 * done(A a) is a function that creates a trampoline that is ready to return a result and more(Trampoline&lt;A&gt;) is a function that creates a
 * trampoline that has more work to do.
 *
 * Mapping over a trampoline is analougouse to creating a trampoine that depends on a previous trampolines result.
 *
 * After constructing a Trampoline use the method Trampoline#result to obtain the final value.
 *
 * <pre>{@code fact(100) == tFact(100).result()}</pre>
 *
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 *
 */
public abstract class Trampoline<A> implements Monad<Trampoline.μ,A> {

  /**
  * Witness type of {@code Trampoline}.
  */
  public static class μ {private μ(){}}

  private Trampoline(){}

  abstract <B> Trampoline<B> doBind(F1<? super A, Trampoline<B>> fn);

  @Override
  public MonadUnit<μ> yield() {
    return Trampoline::UNIT;
  }

  @Override
  public <B> Trampoline<B> map(F1<? super A, ? extends B> fn) {
    return bind(value -> done(fn.call(value)));
  }

  @Override
  public <B> Trampoline<B> bind(F1<? super A, ? extends Monad<μ, B>> fn) {
    return new TrampolineBind<A,B>(this, fn.then(Trampoline::monad));
  }

  @Override
  public <C> Trampoline<C> semi(Monad<μ, C> mb) {
    return bind(a -> mb);
  }

  @Override
  public ApplicativePure<μ> pure() {
    return Trampoline::UNIT;
  }

  @Override
  public <B> Trampoline<B> apply(Applicative<μ, ? extends F1<? super A, ? extends B>> applicative) {
    Trampoline<F1<? super A, B>> mf = (Trampoline<F1<? super A, B>>) applicative;
    return monad(Monad.liftM2(this, mf, (a,f) -> f.call(a)));
  }

  /**
   *
   * Returns the value produced by this trampoline, the value is not cached.
   * @return The resulting vaule of this trampoline.
   */
  public A result() {
    Trampoline<A> trampoline = this;
    while(trampoline.isDone() == false) {
     trampoline = trampoline.step();
    }
    return trampoline.get();
  }

  abstract boolean isDone();

  abstract A get();

  abstract Trampoline<A> step();


  private static class TrampolineBind<A,B> extends Trampoline<B> {

    private final Trampoline<A> t;
    private final F1<? super A, Trampoline<B>> fnNext;

    private TrampolineBind(Trampoline<A> t, F1<? super A, Trampoline<B>> fn) {
      this.t = t;
      this.fnNext = fn;
    }

    @Override
    public boolean isDone() {
      return false;
    }

    @Override
    public Trampoline<B> step() {
      return t.doBind(fnNext);
    }

    @Override
    protected B get() {
      throw new UnsupportedOperationException();
    }

    @Override
    <C> Trampoline<C> doBind(F1<? super B, Trampoline<C>> fn) {
      return t.bind(a -> fnNext.call(a).bind(fn));
    }
  }

  private static class More<A> extends Trampoline<A> {

    private final F0<Trampoline<A>> next;

    private More(F0<Trampoline<A>> next) {
      this.next = next;
    }

    @Override
    public boolean isDone() {
      return false;
    }

    @Override
    public Trampoline<A> step() {
      return next.call();
    }

    @Override
    protected A get() {
      throw new UnsupportedOperationException();
    }

    @Override
    <B> Trampoline<B> doBind(F1<? super A, Trampoline<B>> fn) {
      return next.call().doBind(fn);
    }
  }

  private static class Done<A> extends Trampoline<A> {

    private final F0<A> result;

    private Done(F0<A> result) {
      this.result = result;
    }

    @Override
    public boolean isDone() {
      return true;
    }

    @Override
    public Trampoline<A> step() {
      return this;
    }

    @Override
    protected A get() {
      return result.call();
    }

    @Override
    <B> Trampoline<B> doBind(F1<? super A, Trampoline<B>> fn) {
      return fn.call(result.call());
    }
  }

  /**
  * A strategy for lifting a value into the context of a Trampoline.
  */
  public static final MonadUnit<Trampoline.μ> MONAD_UNIT = new MonadUnit<Trampoline.μ>(){
    @Override
    public <A> Monad<μ, A> unit(A a) {
      return Trampoline.done(a);
    }
  };


  /**
   * A helper function to convert/narrow a reference from a monad to its underlying type.
   * @param monad the trampoline to be casted to its original type.
   * @return a trampoline.
   */
  public static <B> Trampoline<B> monad(Monad<Trampoline.μ,B> monad) {
    return (Trampoline<B>) monad;
  }

  /**
   * A helper function to convert/narrow a refernce from an applicative to its underlying type.
   * @param app the trampoline to be casted to its original type.
   * @return a trampoline.
   */
  public static <A,B> Trampoline<F1<A,B>> applicative(Applicative<Trampoline.μ, ? extends F1<? super A, ? extends B>> app) {
    return cast(app);
  }

  /**
   * Wraps a value within a trampoine.
   * Trampoline.done(a) == Trampoline.UNIT(a)
   * @param a the value to be wrapped into a trampoline.
   * @return a trampoline.
   */
  public static final  <A> Trampoline<A> UNIT(A a) {
    return Trampoline.done(a);
  }

  /**
   * Converts a supplier to a trampoine.
   * The value returned by the supplier is the valued use by the trampoline.
   * Trampoline.done(fn.call()) == Trampoline.done$(fn)
   * The supplier is onlly evaluated when needed.
   * @param fn the supplier to be wrapped into a trampoline.
   * @return a trampoline.
   */
  public static final <A> Trampoline<A> done$(F0<A> fn) {
    return new Done<>(fn);
  }

  /**
   * Creates a Trampoline that has no further computation.
   * @param a the value returned from the created trampoline when Trampoline#result is called.
   * @return a trampoiine containing the given arugment.
   */
  public static final <A> Trampoline<A> done(A a) {
    return done$(() -> a);
  }

  /**
   *
   * Cratets a trampoline whose value depends on the result of two other trampolines.
   * Equivalient to Monad#liftM2.
   *
   * @param fn A function to be applied to the contents of the given trmpoline.
   * @param t1 a trampoline.
   * @param t2 a trmpaoline
   * @return A trampoline created from applying the function fn to the contents of the given
   * trampolines them lifting the value return by fn into a trampoline.
   */
  public static final <A,B,C> Trampoline<C> zipWith(F2<A,B,C> fn, Trampoline<A> t1, Trampoline<B> t2) {
      return monad(Monad.liftM2(t1,t2,fn));
  }

  /**
   * Creates a trampolline that has more work to be done.
   * @param more a supplier that genertates a tramopline. The trampolline generated by the supplier contains
   * subsequent steps for complemeting the computation of the returned trampoline.
   * @return a trampoline that has more to be done.
   */
  public static <A> Trampoline<A> more(F0<Trampoline<A>> more) {
    return new More<A>(more);
  }
}
