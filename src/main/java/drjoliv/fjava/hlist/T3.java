package drjoliv.fjava.data;

import drjoliv.fjava.control.bind.Eval;
import static drjoliv.fjava.control.bind.Eval.*;
import drjoliv.fjava.functions.F1;
import drjoliv.fjava.functions.F3;

/**
 * The product of {@code A} x {@code B} x {@code C}.
 *
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public class T3<A,B,C> {
  public final Eval<A> _1;
  public final Eval<B> _2;
  public final Eval<C> _3;

  private T3(Eval<A> _1, Eval<B> _2, Eval<C> _3) {
    this._1 = _1;
    this._2 = _2;
    this._3 = _3;
  }

  /**
   * Creates the product of {@code a} x {@code b} x {@code c}.
   *
   * @param a the first element of the tuple.
   * @param b the second element of this product.
   * @param c the third element of this product.
   * @return the product of {@code a} x {@code b} x {@code c}.
   */
  public static <A,B,C> T3<A,B,C> t3(Eval<A> a, Eval<B> b, Eval<C> c) {
    return new T3<A,B,C>(a, b, c);
  }

 /**
   * Creates the product of {@code a} x {@code b} x {@code c}.
   *
   * @param a the first element of the tuple.
   * @param b the second element of this product.
   * @param c the third element of this product.
   * @return the product of {@code a} x {@code b} x {@code c}.
   */
  public static <A,B,C> T3<A,B,C> t3(A a, B b, C c) {
    return new T3<A,B,C>(now(a), now(b), now(c));
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

  public <D,E,F> T3<D,E,F> map(F1<? super A,D> fn1, F1<? super B,E> fn2, F1<? super C,F> fn3) {
    return t3(_1.map(fn1), _2.map(fn2), _3.map(fn3));
  }

  public <D> T3<D,B,C> map1(F1<? super A,D> fn) {
   return t3(_1.map(fn), _2, _3);
  }

  public <D> T3<A,D,C> map2(F1<? super B,D> fn) {
   return t3(_1, _2.map(fn), _3);
  }

  public <D> T3<A,B,D> map3(F1<? super C,D> fn) {
   return t3(_1, _2, _3.map(fn));
  }

  /**
   * Returns the first element of this product.
   * @return the first element of this product.
   */
  public A _1() {
    return _1.value();
  }

  /**
   * Returns the second element of this product.
   * @return the second element of this product.
   */
  public B _2() {
    return _2.value();
  }

  /**
   * Returns the third element of this product.
   * @return the third element of this product.
   */
  public C _3() {
    return _3.value();
  }
}
