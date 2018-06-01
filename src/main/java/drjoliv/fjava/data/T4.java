package drjoliv.fjava.data;

import drjoliv.fjava.control.bind.Eval;
import static drjoliv.fjava.control.bind.Eval.*;
import drjoliv.fjava.functions.F1;
import drjoliv.fjava.functions.F4;

/**
 * The product of {@code A} x {@code B} x {@code C} x {@code D}.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public class T4<A,B,C,D> {
  public final Eval<A> _1;
  public final Eval<B> _2;
  public final Eval<C> _3;
  public final Eval<D> _4;

  private T4(Eval<A> _1, Eval<B> _2, Eval<C> _3, Eval<D> _4) {
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
    return new T4<>(now(a), now(b), now(c), now(d));
  }

  public static <A,B,C,D> T4<A,B,C,D> t4(Eval<A> a, Eval<B> b, Eval<C> c, Eval<D> d) {
    return new T4<>(a, b, c, d);
  }

  public <E,F,G,H> T4<E,F,G,H> map(F1<? super A,E> fn1, F1<? super B,F> fn2, F1<? super C,G> fn3, F1<? super D,H> fn4) {
    return t4(_1.map(fn1), _2.map(fn2), _3.map(fn3), _4.map(fn4));
  }

  public <E> T4<E,B,C,D> map1(F1<? super A,E> fn) {
   return t4(_1.map(fn), _2, _3,_4);
  }

  public <E> T4<A,E,C,D> map2(F1<? super B,E> fn) {
   return t4(_1, _2.map(fn), _3,_4);
  }

  public <E> T4<A,B,E,D> map3(F1<? super C,E> fn) {
   return t4(_1, _2, _3.map(fn),_4);
  }

  public <E> T4<A,B,C,E> map4(F1<? super D,E> fn) {
   return t4(_1, _2, _3,_4.map(fn));
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

  /**
   * Returns the fourth element of this product.
   * @return the fourth element of this product.
   */
  public D _4() {
    return _4.value();
  }

}
