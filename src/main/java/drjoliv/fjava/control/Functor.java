package me.functional.type;

import me.functional.functions.F1;
import me.functional.hkt.Witness;

/**
 * Functor is a context in which values are transformed.
 * @author drjoliv@gmail.com
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
  public <B> Functor<F,B>  map(F1<? super A, B> fn);

  /**
   * Replace is equivalent to {@code map(const(b)) // f.replace(b) == f.map(const(b))) } where {@code const(b)} creates a function that always returns {@code b}.
   *
   * @see #map
   * @param b the value to be insterted into a Functor.
   * @param <B> the type of the contents of the new Functor.
   * @return the Functor containing {@code b}.
   */
  public default <B> Functor<F,B> replace(B b) {
    return map(a -> b);
  }

}
