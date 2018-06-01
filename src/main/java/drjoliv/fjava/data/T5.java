package drjoliv.fjava.data;

import drjoliv.fjava.control.bind.Eval;
import static drjoliv.fjava.control.bind.Eval.*;
import drjoliv.fjava.functions.F1;
import drjoliv.fjava.functions.F5;

/**
 * The product of {@code A} x {@code B} x {@code C} x {@code D} x {@code E}.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public class T5<A,B,C,D,E> {
  public final Eval<A> _1;
  public final Eval<B> _2;
  public final Eval<C> _3;
  public final Eval<D> _4;
  public final Eval<E> _5;

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
    return new T5<>(now(a), now(b), now(c), now(d), now(e));
  }

  public static <A,B,C,D,E> T5<A,B,C,D,E> t5(Eval<A> a, Eval<B> b, Eval<C> c, Eval<D> d, Eval<E> e) {
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


  private T5(Eval<A> _1, Eval<B> _2, Eval<C> _3, Eval<D> _4, Eval<E> _5) {
    this._1 = _1;
    this._2 = _2;
    this._3 = _3;
    this._4 = _4;
    this._5 = _5;
  }

  public <F,G,H,I,J> T5<F,G,H,I,J> map(F1<? super A,F> fn1, F1<? super B,G> fn2, F1<? super C,H> fn3
      , F1<? super D,I> fn4, F1<? super E,J> fn5) {
    return t5(_1.map(fn1), _2.map(fn2), _3.map(fn3), _4.map(fn4), _5.map(fn5));
  }

  public <F> T5<F,B,C,D,E> map1(F1<? super A,F> fn) {
   return t5(_1.map(fn), _2, _3, _4, _5);
  }

  public <F> T5<A,F,C,D,E> map2(F1<? super B,F> fn) {
   return t5(_1, _2.map(fn), _3, _4, _5);
  }

  public <F> T5<A,B,F,D,E> map3(F1<? super C,F> fn) {
   return t5(_1, _2, _3.map(fn), _4, _5);
  }

  public <F> T5<A,B,C,F,E> map4(F1<? super D,F> fn) {
   return t5(_1, _2, _3, _4.map(fn), _5);
  }

  public <F> T5<A,B,C,D,F> map5(F1<? super E,F> fn) {
   return t5(_1, _2, _3, _4, _5.map(fn));
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


  /**
   * Returns the fifth element of this product.
   * @return the fifth element of this product.
   */
  public E _5() {
    return _5.value();
  }

}
