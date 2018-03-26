package me.functional;

/**
 * A Function that takes three arguments
 *
 * @author drjoliv@gmail.com
 */
public interface TriFunction<A,B,C,D> {
  public D apply(A a,B b,C c);
}
