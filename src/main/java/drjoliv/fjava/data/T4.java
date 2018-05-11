package drjoliv.fjava.data;

import drjoliv.fjava.functions.F4;

/**
 * The product of {@code A} x {@code B} x {@code C} x {@code D}.
 *
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public class T4<A,B,C,D> {
  public final A _1;
  public final B _2;
  public final C _3;
  public final D _4;

  private T4(A _1, B _2, C _3, D _4) {
    this._1 = _1;
    this._2 = _2;
    this._3 = _3;
    this._4 = _4;
  }

  /**
   * Creates the product of {@code a} x {@code b} x {@code c} x {@code d}.
   *
   * @param a the first element of the tuple.
   * @param b the second element of this product.
   * @param c the third element of this product.
   * @param d the fourth element of this product.
   * @return the product of {@code a} x {@code b} x {@code c} x {@code d}.
   */
  public static <A,B,C,D> T4<A,B,C,D> t4(A a, B b, C c, D d) {
    return new T4<>(a, b, c, d);
  }

  /**
   * Returns a first class fucntion that creates the 
   * product of {@code A} x {@code A} x {@code C} x {@code D}.
   *
   * @return The first class fucntion that creates the product
   * of {@code A} x {@code A} x {@code C} x {@code D}.
   */
  public static <A,B,C,D> F4<A,B,C,D,T4<A,B,C,D>> t4() {
    return T4::t4;
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

  /**
   * Returns the fourth element of this product.
   * @return the fourth element of this product.
   */
  public D _4() {
    return _4;
  }

}
