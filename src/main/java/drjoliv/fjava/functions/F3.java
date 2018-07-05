package drjoliv.fjava.functions;

import drjoliv.fjava.hlist.T3;

/**
 * A computation that takes three argumensts and returns a result.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
@FunctionalInterface
public interface F3<A,B,C,D> {


  public default <E> F3<A,B,C,E> then(F1<? super D, ? extends E> fn) {
    return F3Composed.doMap(this, fn);
  }

  /**
   *
   * Returns the result of this computation.
   * @param a a arugment.
   * @param b a arugment.
   * @param c a argument.
   * @return the result of this computation.
   */
  public D call(A a, B b, C c);

  /**
   *
   * Paritally applies the two arguments, returning a function that takes one argument and returns a result.
   * @param a a argument.
   * @param b a argument.
   * @return a partially applied function that takes one argument and returns a result.
   */
  public default F1<C,D> call(A a, B b) {
    return call(a).call(b);
  }

  /**
   *
   * Paritally applies an argument, returning a function that takes two argument and returns a result.
   * @param a
   * @return a function that takes two argument and returns a result.
   */
  public default F2<B,C,D> call(A a) {
    return partial(this,a);
  }

  /**
   * Converts this function into one that takes a single argument of {@code T3<A,B,C>}.
   * @return a function that take a single arguemnt of {@code T2<A,B,C>} and returns a result.
   */
  public default F1<T3<A,B,C>,D> t3() {
    return t -> call(t._1(),t._2(), t._3());
  }

  /**
   * Converts that function in one that returns a functions.
   * @return a function that returns a function.
   */
  public default F1<A,F2<B,C,D>> curry() {
    return a -> partial(this,a);
  }

  /**
   * Partially appiles the value {@code a} to the function {@code fn}, returning a partially applied function.
   * @param fn a funcion to be partially applied.
   * @param a a argument to be applied.
   * @return a partially applied function.
   */
  public static <A,B,C,D> F2<B,C,D> partial(F3<A,B,C,D> fn, A a) {
    return (b,c) -> fn.call(a,b,c);
  }

}
