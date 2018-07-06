package drjoliv.fjava.functor;

import drjoliv.fjava.functions.F1;
import drjoliv.fjava.functions.F2;
import drjoliv.fjava.functions.F3;
import drjoliv.fjava.functions.F4;
import drjoliv.fjava.functions.F5;
import drjoliv.fjava.hkt.Witness;

/**
 * Functor is a context in which values are transformed.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public interface Functor<F extends Witness,A> {

  /**
   * Map applies the function {@code fn} to the contents of this Functor, returning a new Functor
   * whose contents is the result of that function application.
   *
   * @param fn the function that will map over the contents of this functor.
   * @param <B> the type of the contents of the new Functor.
   * @return a {@code Functor} containing the result of {@code fn}.
   */
  public <B> Functor<F,B>  map(F1<? super A, ? extends B> fn);

  /**
   * Partially applies a function to the contents of this functor returning a new Functor whose contents is a partially applioed function of arity 1.
   * @param fn the function that will map over the contents of this functor.
   * @return a functor whose contents is a partially applioed function of arity 1.
   */
  public default <B,C> Functor<F,F1<B,C>>  map(F2<? super A, B, C> fn) {
    return map(fn.curry());
  }

  /**
   * Partially applies a function to the contents of this functor returning a new functor whose contents is a partially applioed function of arity 2.
   * @param fn the function that will map over the contents of this functor.
   * @return a functor whose contents is a partially applioed function of arity 2.
   */
  public default <B,C,D> Functor<F, F1<B, F1<C, D>>>  map(F3<? super A, B, C, D> fn) {
    return map(fn.curry()).map(f -> f.curry());
  }

  /**
   * Partially applies a function to the contents of this functor returning a new functor whose contents is a partially applioed function of arity 3.
   * @param fn the function that will map over the contents of this functor.
   * @return a functor whose contents is a partially applioed function of arity 3.
   */
  public default <B,C,D,E> Functor<F, F1<B, F1<C, F1<D, E>>>>  map(F4<? super A, B, C, D, E> fn) {
    return map(fn.curry())
      .map(f -> f.curry().map(h -> h.curry()));
  }

  /**
   * Partially applies a function to the contents of this functor returning a new functor whose contents is a partially applioed function of arity 4.
   * @param fn the function that will map over the contents of this functor.
   * @return a functor whose contents is a partially applioed function of arity 4.
   */
  public default <B,C,D,E,G> Functor<F,F1<B, F1<C, F1<D, F1<E, G>>>>>  map(F5<? super A, B, C, D, E, G> fn) {
    return map(fn.curry())
      .map(f -> f.curry().map(h -> h.curry().map(i -> i.curry())));
  }

  /**
   * Replace is equivalent to {@code map(const(b)) // f.replace(b) == f.map(const(b))) } where {@code const(b)} creates a function that always returns {@code b}.
   *
   * @param b the value to be insterted into a Functor.
   * @param <B> the type of the contents of the new Functor.
   * @return the Functor containing {@code b}.
   */
  public default <B> Functor<F,B> replace(B b) {
    F1<A,B> go = a -> b;
    return map(go);
  }
}
