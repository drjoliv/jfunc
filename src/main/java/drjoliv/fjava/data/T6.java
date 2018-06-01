package drjoliv.fjava.data;

import drjoliv.fjava.functions.F6;

/**
 * The product of {@code A} x {@code B} x {@code C} x {@code D} x {@code E} x {@code F}.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public class T6<A,B,C,D,E,F> {
  public final A _1;
  public final B _2;
  public final C _3;
  public final D _4;
  public final E _5;
  public final F _6;

  /**
   * Creates the product of {@code a} x {@code b} x {@code c} x {@code d} x {@code e} x {@code f}.
   *
   * @param a the first element of the tuple.
   * @param b the second element of this product.
   * @param c the third element of this product.
   * @param d the fourth element of this product.
   * @param e the fifth element of this product.
   * @param e the fifth element of this product.
   * @param f the sixth element of this product.
   * @return the product of {@code a} x {@code b} x {@code c} x {@code d} x {@code e} x {@code f}.
   */
  public static <A,B,C,D,E,F> T6<A,B,C,D,E,F> t6(A a, B b, C c, D d, E e, F f) {
    return new T6<>(a, b, c, d, e, f);
  }

  /**
   * Returns a first class fucntion that creates the 
   * product of {@code A} x {@code A} x {@code C} x {@code D} x {@code E} x {@code F}.
   *
   * @return The first class fucntion that creates the product
   * of {@code A} x {@code A} x {@code C} x {@code D} x {@code E} x {@code F}.
   */
  public static <A,B,C,D,E,F> F6<A,B,C,D,E,F,T6<A,B,C,D,E,F>> t6() {
    return T6::t6;
  }

  private T6(A _1, B _2, C _3, D _4, E _5, F _6) {
    this._1 = _1;
    this._2 = _2;
    this._3 = _3;
    this._4 = _4;
    this._5 = _5;
    this._6 = _6;
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


  /**
   * Returns the fifth element of this product.
   * @return the fifth element of this product.
   */
  public E _5() {
    return _5;
  }

  /**
   * Returns the sixth element of this product.
   * @return the sixth element of this product.
   */
  public F _6() {
    return _6;
  }
}
