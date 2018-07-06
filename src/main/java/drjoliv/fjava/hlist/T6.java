package drjoliv.fjava.hlist;

import static drjoliv.fjava.adt.Eval.*;

import drjoliv.fjava.adt.Eval;
import drjoliv.fjava.functions.F1;
import drjoliv.fjava.functions.F6;

/**
 * The product of {@code A} x {@code B} x {@code C} x {@code D} x {@code E} x {@code F}.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public class T6<A,B,C,D,E,F> {
  public final Eval<A> _1;
  public final Eval<B> _2;
  public final Eval<C> _3;
  public final Eval<D> _4;
  public final Eval<E> _5;
  public final Eval<F> _6;

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
    return new T6<>(now(a), now(b), now(c), now(d), now(e), now(f));
  }

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
  public static <A,B,C,D,E,F> T6<A,B,C,D,E,F> t6(Eval<A> a, Eval<B> b, Eval<C> c, Eval<D> d, Eval<E> e, Eval<F> f) {
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

  private T6(Eval<A> _1, Eval<B> _2, Eval<C> _3, Eval<D> _4, Eval<E> _5, Eval<F> _6) {
    this._1 = _1;
    this._2 = _2;
    this._3 = _3;
    this._4 = _4;
    this._5 = _5;
    this._6 = _6;
  }

  public <G,H,I,J,K,L> T6<G,H,I,J,K,L> map(F1<? super A,G> fn1, F1<? super B,H> fn2, F1<? super C,I> fn3
      , F1<? super D,J> fn4, F1<? super E,K> fn5, F1<? super F,L> fn6) {
    return t6(_1.map(fn1), _2.map(fn2), _3.map(fn3), _4.map(fn4), _5.map(fn5), _6.map(fn6));
  }

  public <G> T6<G,B,C,D,E,F> map1(F1<? super A,G> fn) {
   return t6(_1.map(fn), _2, _3, _4, _5, _6);
  }

  public <G> T6<A,G,C,D,E,F> map2(F1<? super B,G> fn) {
   return t6(_1, _2.map(fn), _3, _4, _5,_6);
  }

  public <G> T6<A,B,G,D,E,F> map3(F1<? super C,G> fn) {
   return t6(_1, _2, _3.map(fn), _4, _5, _6);
  }

  public <G> T6<A,B,C,G,E,F> map4(F1<? super D,G> fn) {
   return t6(_1, _2, _3, _4.map(fn), _5, _6);
  }

  public <G> T6<A,B,C,D,G,F> map5(F1<? super E,G> fn) {
   return t6(_1, _2, _3, _4, _5.map(fn), _6);
  }

  public <G> T6<A,B,C,D,E,G> map6(F1<? super F,G> fn) {
   return t6(_1, _2, _3, _4, _5, _6.map(fn));
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

  /**
   * Returns the sixth element of this product.
   * @return the sixth element of this product.
   */
  public F _6() {
    return _6.value();
  }
}
