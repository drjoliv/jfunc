package me.functional.type;

import java.util.function.Function;

import me.functional.hkt.Hkt;


import static me.functional.Util.*;

/**
 * A Functor is a container of some sort whose value can be mapped over.
 *
 * @author drjoliv@gmail.com
 */
public interface Functor<F> {

  /**
   * fmap maps over the contents of the given functor with the given function.
   *
   * @param fn a function that will map over the contents of the given functor.
   * @param functor the functor whose value will be mapped over.
   * @return a functor containing the result of fn, after it has mapped over the contents of the given functor.
   */
  public <A,B> Hkt<F,B> fmap(Function<A,B> fn, Hkt<F,A> functor);

  /**
   * Replaces the contents of functor with the given value a.
   *
   * @param a the value to be insterted into a functor
   * @param functor the Functor whose value will be replaced
   * @return A Functor where a has been inserted
   */
  public default <A,B> Hkt<F,A> replace(A a, Hkt<F,B> functor) {
    return fmap(constant(a), functor);
  }

}
