package drjoliv.fjava.functions;

import drjoliv.fjava.functor.Functor;
import drjoliv.fjava.hkt.Hkt5;
import drjoliv.fjava.hkt.Witness;
import drjoliv.fjava.hlist.T5;

/**
 * A computation that takes five arguments and returns a result.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
@FunctionalInterface
public interface F5<A,B,C,D,E,F> { 


  public default <G> F5<A,B,C,D,E,G> then(F1<? super F, ? extends G> fn) {
    return F5Composed.doMap(this,fn);
  }

  /**
   *
   * Returns the result of this computation.
   * @param a an argument.
   * @param b an argument.
   * @param c an argument.
   * @param d an argument.
   * @param e an argument.
   * @return the result of this computation.
   */
  public F call(A a, B b, C c, D d, E e);

  /**
   *
   * Partially applies four arguments, returning a function that takes one argument.
   * @param a an argmuent.
   * @param a an argument.
   * @param c an argument.
   * @param d an argument.
   * @return a partially applied function that takes an argument.
   */
  public default F1<E,F> call(A a, B b, C c, D d) {
    return call(a).call(b).call(c).call(d);
  }

  /**
   * Paritally applies the two arguments, returning a function that takes three arguments.
   * @param a an argument.
   * @param b an argument.
   * @return a partially applied function that takes three arguments.
   */
  public default F3<C,D,E,F> call(A a, B b) {
    return call(a).call(b);
  }

  /**
   * Paritally applies an argument, returning a function that takes four arguments.
   * @param a an argument.
   * @return a function that takes four arguments.
   */
  public default F4<B,C,D,E,F> call(A a) {
    return partial(this,a);
  }

  /**
   * Converts this function into one that returns a functions.
   * @return a function that returns a function of arity 4.
   */
  public default F1<A,F4<B,C,D,E,F>> curry() {
    return a -> partial(this,a);
  }

  /**
   * Converts this function into one that takes a single argument of {@code T3<A,B,C,D,E>}.
   * @return a function that take a single arguemnt of {@code T2<A,B,C,D,E>} and returns a result.
   */
  public default F1<T5<A,B,C,D,E>,F> t5() {
    return t -> call(t._1(),t._2(),t._3(), t._4(), t._5());
  }

   /**
   * Partially appiles the value {@code a} to the function {@code fn}, returning a partially applied function of arity 4.
   * @param fn a funcion to be partially applied.
   * @param a a argument to be applied.
   * @return a partially applied function of arity 4.
   */
  public static <A,B,C,D,E,F> F4<B,C,D,E,F> partial(F5<A,B,C,D,E,F> fn4, A a) {
    return (b,c,d,e) -> fn4.call(a,b,c,d,e);
  }
}
