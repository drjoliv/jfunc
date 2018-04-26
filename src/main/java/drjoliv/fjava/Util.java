package me.functional;

import java.util.function.Function;

/**
 * Util functions.
 *
 * @author drjoliv@gmail.com
 */
public class Util {

  public static Util instnace = new Util();

  private Util(){}

  /**
   * Creates a function that always returns the a.
   *
   * @param a the value that will always be returned by the created function.
   * @return A function that will always return a.
   */
  public static <A> Function<?,A> constant(A a) {
    return b -> a;
  }
}
