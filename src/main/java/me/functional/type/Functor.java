package me.functional.type;

import java.util.function.Function;

import me.functional.hkt.Hkt;

/**
 * A Functor is a container of some sort whose value can be mapped over.
 *
 * @author drjoliv@gmail.com
 */
public interface Functor<F,A> {

  /**
   * fmap maps over the contents of the given functor with the given function.
   *
   * @param fn a function that will map over the contents of the given functor.
   * @param functor the functor whose value will be mapped over.
   * @return a functor containing the result of fn, after it has mapped over the contents of the given functor.
   */
  public <B> Hkt<F,B> fmap(Function<A,B> fn);

  /**
   * Replaces the contents of functor with the given value a.
   *
   * @param a the value to be insterted into a functor
   * @param functor the Functor whose value will be replaced
   * @return A Functor where a has been inserted
   */
  public default <B> Hkt<F,B> replace(B b) {
    return fmap(a -> b);
  }

}
