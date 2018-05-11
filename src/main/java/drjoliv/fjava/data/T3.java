package drjoliv.fjava.data;

import drjoliv.fjava.functions.F3;

/**
 * The product of {@code A} x {@code B} x {@code C}.
 *
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public class T3<A,B,C> {
  public final A _1;
  public final B _2;
  public final C _3;

  private T3(A _1, B _2, C _3) {
    this._1 = _1;
    this._2 = _2;
    this._3 = _3;
  }

  /**
   * Creates the product of {@code a} x {@code b} x {@code c}.
   *
   * @param a the first element of the tuple.
   * @param b the second element of this product.
   * @param b the second element of this product.
   * @param c the third element of this product.
   * @return the product of {@code a} x {@code b} x {@code c}.
   */
  public static <A,B,C> T3<A,B,C> t3(A a, B b, C c) {
    return new T3<A,B,C>(a, b, c);
  }

  /**
   * Returns a first class fucntion that creates the 
   * product of {@code A} x {@code A} x {@code C}.
   *
   * @return The first class fucntion that creates the product
   * of {@code A} x {@code A} x {@code C}.
   */
  public static <A,B,C> F3<A,B,C,T3<A,B,C>> t3() {
    return T3::t3;
  }

  /**
   * Returns the first element of this product.
   * @return the first element of this product.
   */
  public A _1() {
    return _1;
  }

  /**
   * Returns the second element of this product.
   * @return the second element of this product.
   */
  public B _2() {
    return _2;
  }

  /**
   * Returns the third element of this product.
   * @return the third element of this product.
   */
  public C _3() {
    return _3;
  }
}
