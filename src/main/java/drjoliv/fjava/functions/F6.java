package drjoliv.fjava.functions;

import drjoliv.fjava.functor.Functor;
import drjoliv.fjava.hkt.Hkt6;
import drjoliv.fjava.hkt.Witness;
import drjoliv.fjava.hlist.T6;

/**
 *
 * A computation that takes six arguments and returns a result.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
@FunctionalInterface
public interface F6<A,B,C,D,E,F,G> {

  public default <H> F6<A,B,C,D,E,F,H> then(F1<? super G, ? extends H> fn) {
    return F6Composed.doMap(this,fn);
  }

  /**
   * Returns the result of this computation.
   * @param a an argument.
   * @param b an argument.
   * @param c an argument.
   * @param d an argument.
   * @param e an argument.
   * @param f an argument.
   * @return the result of this computation.
   */
  public G call(A a, B b, C c, D d, E e, F f);

  /**
   * Partially applies five arguments, returning a function that takes one argument.
   * @param a an argument.
   * @param b an argument.
   * @param c an argument.
   * @param d an argument.
   * @param e an argument.
   * @return a partially applied function that takes one argumennt.
   */
  public default F1<F,G> call(A a, B b, C c, D d, E e) {
    return call(a).call(b).call(c).call(d).call(e);
  }

  /**
   *
   * Partially applies four arguments, returning a function that takes two arguments.
   * @param a an argmuent.
   * @param a an argument.
   * @param c an argument.
   * @param d an argument.
   * @return a partially applied function that takes two arguments.
   */
  public default F2<E,F,G> call(A a, B b, C c, D d) {
    return call(a).call(b).call(c).call(d);
  }

  /**
   * Partially applies three arguments, returning a function that takes three arguments.
   * @param a an argument.
   * @param b an argument.
   * @param c an argument.
   * @return a partially applied function that takes three arguments.
   */
  public default F3<D,E,F,G> call(A a, B b, C c) {
    return call(a).call(b).call(c);
  }

   /**
   * Paritally applies the two arguments, returning a function that takes four arguments.
   * @param a an argument.
   * @param b an argument.
   * @return a partially applied function that takes four arguments.
   */
  public default F4<C,D,E,F,G> call(A a, B b) {
    return call(a).call(b);
  }

  /**
   * Paritally applies an argument, returning a function that takes five arguments.
   * @param an argument.
   * @return a function that takes five arguments.
   */
  public default F5<B,C,D,E,F,G> call(A a) {
    return partial(this,a);
  }

  /**
   * Converts this function into one that returns a functions.
   * @return a function that returns a function of arity 5.
   */
  public default F1<A,F5<B,C,D,E,F,G>> curry() {
    return a -> partial(this,a);
  }

  /**
   * Converts this function into one that takes a single argument of {@code T3<A,B,C,D,E,F>}.
   * @return a function that take a single arguemnt of {@code T2<A,B,C,D,E,F>} and returns a result.
   */
  public default F1<T6<A,B,C,D,E,F>,G> t6() {
    return t -> call(t._1(),t._2(),t._3(), t._4(), t._5(), t._6());
  }

   /**
   * Partially appiles the value {@code a} to the function {@code fn}, returning a partially applied function of arity 5.
   * @param fn a funcion to be partially applied.
   * @param a a argument to be applied.
   * @return a partially applied function of arity 5.
   */
  public static <A,B,C,D,E,F,G> F5<B,C,D,E,F,G> partial(F6<A,B,C,D,E,F,G> fn6, A a) {
    return (b,c,d,e,f) -> fn6.call(a,b,c,d,e,f);
  }
}
