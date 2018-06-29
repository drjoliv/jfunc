package drjoliv.fjava.functions;

import java.util.function.BiFunction;

import drjoliv.fjava.functor.Functor;
import drjoliv.fjava.hkt.Hkt2;
import drjoliv.fjava.hkt.Witness;
import drjoliv.fjava.hlist.T2;

/**
 * A computaiton that takes two arugments and returns a result.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
@FunctionalInterface
public interface F2<A,B,C> extends BiFunction<A,B,C> {

  public default <D> F2<A,B,D> then(F1<? super C, ? extends D> fn) {
    return F2Composed.doMap(this, fn);
  }


  @Override
  public default C apply(A a, B b) {
    return call(a,b);
  }

  /**
   * Returns the result of this computation.
   * @param a a argument.
   * @param b a argument.
   * @return the result of this computation.
   */
  public C call(A a, B b);


  /**
   *
   * Paritally applies an argument, returning a function that takes one argument and returns a result.
   * @param a
   * @return a function that takes one argument and returns a result.
   */
  public default F1<B,C> call(A a) {
    return partial(this,a);
  }

  /**
   * Converts this function into one that takes a single argument of {@code T2<A,B>}.
   * @return a function that take a single arguemnt of {@code T2<A,B>} and returns a result.
   */
  public default F1<T2<A,B>, C> t2() {
    return t -> call(t._1(),t._2());
  }

  /**
   * Converts that function in one that returns a functions.
   * @return a function that returns a function.
   */
  public default F1<A,F1<B,C>> curry() {
    return a -> partial(this,a);
  }

  /**
   * Partially appiles the value {@code a} to the function {@code fn}, returning a partially applied function.
   * @param fn a funcion to be partially applied.
   * @param a a argument to be applied.
   * @return a partially applied function.
   */
  public static <A,B,C> F1<B,C> partial(F2<A,B,C> fn, A a) {
    return b -> fn.call(a,b);
  }
}
