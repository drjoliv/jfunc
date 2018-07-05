package drjoliv.fjava.functions;

import drjoliv.fjava.hlist.T4;

/**
 * A computation that takes four arguments and returns a result.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
@FunctionalInterface
public interface F4<A,B,C,D,E> {


  public default <F> F4<A,B,C,D,F> then(F1<? super E, ? extends F> fn) {
    return F4Composed.doMap(this,fn);
  }

  /**
   *
   * Returns the result of this computation.
   * @param a a argmuent.
   * @param a a argument.
   * @param c a argument.
   * @param d a argument.
   * @return the result of this computation.
   */
  public E call(A a, B b, C c, D d);

  /**
   * Partially applies three arguments, returning a function that takes one argument.
   * @param a a argmuent.
   * @param b a argmuent.
   * @param c a argument.
   * @return a partially applied function that takes an argument.
   */
  public default F1<D,E> call(A a, B b, C c) {
    return call(a).call(b).call(c);
  }

  /**
   * Paritally applies the two arguments, returning a function that takes two arguments.
   * @param a a argument.
   * @param b a argument.
   * @return a partially applied function that takes two arguments.
   */
  public default F2<C,D,E> call(A a, B b) {
    return call(a).call(b);
  }

  /**
   * Paritally applies an argument, returning a function that takes three arguments.
   * @param a argument.
   * @return a partially applied function that takes three arguments.
   */
  public default F3<B,C,D,E> call(A a) {
    return partial(this,a);
  }

   /**
   * Converts this function into one that returns a function.
   * @return a function that returns a function of arity 3.
   */
  public default F1<A,F3<B,C,D,E>> curry() {
    return a -> partial(this,a);
  }

  /**
   * Converts this function into one that takes a single argument of {@code T3<A,B,C,D>}.
   * @return a function that take a single arguemnt of {@code T2<A,B,C,D>} and returns a result.
   */
  public default F1<T4<A,B,C,D>,E> t4() {
    return t -> call(t._1(),t._2(),t._3(),t._4());
  }

  /**
   * Partially appiles the value {@code a} to the function {@code fn}, returning a partially applied function of arity 3.
   * @param fn a funcion to be partially applied.
   * @param a a argument to be applied.
   * @return a partially applied function of arity 3.
   */
  public static <A,B,C,D,E> F3<B,C,D,E> partial(F4<A,B,C,D,E> fn, A a) {
    return (b,c,d) -> fn.call(a,b,c,d);
  }
}

