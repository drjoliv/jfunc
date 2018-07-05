package drjoliv.fjava.functions;

import static drjoliv.fjava.adt.Dequeue.dequeue;
import static drjoliv.fjava.functions.F1Composed.isComposedFunc;

/**
 * A function from {@code A} to {@code B}.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
@FunctionalInterface
public interface F1<A,B> {

  /**
   * Returns the result of this function.
   * @param a the argument for this function.
   * @return the result of this function.
   */
  public B call(A a);

  /**
   * Transfroms this function from {@code fn::A -> B} to {@code fn::A -> F0<B>}.
   * @return a {@code fn::A→ F0<B>}.
   */
  public default F1<A,F0<B>> curry() {
    return a -> () -> {
      return call(a);
    };
  }

  /**
   * Creates a composition of this function and the argument such that this function is applied first.
   * {@code g = this}
   * {@code f = fn}
   * {@code h = (f . g) }
   * {@code h(a) == f(g(a)) }
   *
   * @param fn a function to compose.
   * @return a composed function.
   */
  public default <C> F1<A,C> then(F1< ? super B, ? extends C> fn) {
    return (F1<A,C>)fn.before(this);
  }

  /**
   * Creates a composition of this function and the argument such that argument function is applied first.
   * {@code g = fn}
   * {@code f = this}
   * {@code h = (f . g) }
   * {@code h(a) == f(g(a)) }
   *
   * @param fn a function to compose.
   * @return a composed function.
   */
  public default <C> F1<C,B> before(F1<? super C ,? extends A> fn) {
    return isComposedFunc(fn)
      ? (F1<C,B>)fn.then(this)
      : new F1Composed<C,B>(dequeue(fn,this));
  }

  /**
   * Returns the identity function.
   * @return the identity function.
   */
  public static <A> F1<A, A> identity() {
    return a -> a;
  }

  public static <A,B,C> F1<A,C> compose(F1<B,C> f2, F1<A,B> f1) {
    return f2.before(f1);
  }

  public static <A,B,C,D> F1<A,D> compose(F1<C,D> f3, F1<B,C> f2, F1<A,B> f1) {
    return f3.before(f2.before(f1));
  }

}
