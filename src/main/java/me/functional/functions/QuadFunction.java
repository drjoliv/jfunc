package me.functional;

/**
 * A function that takes four arugments.
 *
 * @author drjoliv@gmail.com
 */
public interface QuadFunction<A,B,C,D,E>{
  public E apply(A a, B b, C c, D d);
}
