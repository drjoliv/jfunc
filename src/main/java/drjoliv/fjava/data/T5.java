package drjoliv.fjava.data;

import drjoliv.fjava.functions.F5;

/**
 * The product of {@code A} x {@code B} x {@code C} x {@code D} x {@code E}.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public class T5<A,B,C,D,E> {
  public final A _1;
  public final B _2;
  public final C _3;
  public final D _4;
  public final E _5;

  /**
   *
   *
   * Creates the product of {@code a} x {@code b} x {@code c} x {@code d} x {@code e}.
   *
   * @param a the first element of the tuple.
   * @param b the second element of this product.
   * @param c the third element of this product.
   * @param d the fourth element of this product.
   * @param e the fifth element of this product.
   * @return the product of {@code a} x {@code b} x {@code c} x {@code d} x {@code e}.
   */
  public static <A,B,C,D,E> T5<A,B,C,D,E> t5(A a, B b, C c, D d, E e) {
    return new T5<>(a, b, c, d, e);
  }

  /**
   * Returns a first class fucntion that creates the 
   * product of {@code A} x {@code A} x {@code C} x {@code D} x {@code E}.
   *
   * @return The first class fucntion that creates the product
   * of {@code A} x {@code A} x {@code C} x {@code D} x {@code E}.
   */
  public static <A,B,C,D,E> F5<A,B,C,D,E,T5<A,B,C,D,E>> t5() {
    return T5::t5;
  }


  private T5(A _1, B _2, C _3, D _4, E _5) {
    this._1 = _1;
    this._2 = _2;
    this._3 = _3;
    this._4 = _4;
    this._5 = _5;
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

}
