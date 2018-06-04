package drjoliv.fjava.hlist;

import static drjoliv.fjava.adt.Eval.*;

import drjoliv.fjava.adt.Eval;
import drjoliv.fjava.functions.F1;
import drjoliv.fjava.functions.F7;

/**
 * The product of {@code A} x {@code B} x {@code C} x {@code D} x {@code E} x {@code F} x {@code G}.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public class T7<A,B,C,D,E,F,G> {
  public final Eval<A> _1;
  public final Eval<B> _2;
  public final Eval<C> _3;
  public final Eval<D> _4;
  public final Eval<E> _5;
  public final Eval<F> _6;
  public final Eval<G> _7;

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
   * @param g the seventh element of this product.
   * @return the product of {@code a} x {@code b} x {@code c} x {@code d} x {@code e} x {@code f} x {@code g}.
   */
  public static <A,B,C,D,E,F,G> T7<A,B,C,D,E,F,G> t7(A a, B b, C c, D d, E e, F f, G g) {
    return new T7<>(now(a), now(b), now(c), now(d), now(e), now(f), now(g));
  }

  public static <A,B,C,D,E,F,G> T7<A,B,C,D,E,F,G> t7(Eval<A> a, Eval<B> b, Eval<C> c, Eval<D> d
      , Eval<E> e, Eval<F> f, Eval<G> g) {
    return new T7<>(a, b, c, d, e, f, g);
  }

  /**
   * Returns a first class fucntion that creates the 
   * product of {@code A} x {@code A} x {@code C} x {@code D} x {@code E} x {@code F} x {@code  G}.
   *
   * @return The first class fucntion that creates the product
   * of {@code A} x {@code A} x {@code C} x {@code D} x {@code E} x {@code F} x {@code G}.
   */
  public static <A,B,C,D,E,F,G> F7<A,B,C,D,E,F,G,T7<A,B,C,D,E,F,G>> t7() {
    return T7::t7;
  }

  private T7(Eval<A> _1, Eval<B> _2, Eval<C> _3, Eval<D> _4, Eval<E> _5, Eval<F> _6, Eval<G> _7) {
    this._1 = _1;
    this._2 = _2;
    this._3 = _3;
    this._4 = _4;
    this._5 = _5;
    this._6 = _6;
    this._7 = _7;
  }

  public <H,I,J,K,L,M,N> T7<H,I,J,K,L,M,N> map(F1<? super A,H> fn1, F1<? super B,I> fn2, F1<? super C,J> fn3
      , F1<? super D,K> fn4, F1<? super E,L> fn5, F1<? super F,M> fn6, F1<? super G,N> fn7) {
    return t7(_1.map(fn1), _2.map(fn2), _3.map(fn3), _4.map(fn4), _5.map(fn5), _6.map(fn6), _7.map(fn7));
      
  }

  public <H> T7<H,B,C,D,E,F,G> map1(F1<? super A,H> fn) {
   return t7(_1.map(fn), _2, _3, _4, _5, _6, _7);
  }

  public <H> T7<A,H,C,D,E,F,G> map2(F1<? super B,H> fn) {
   return t7(_1, _2.map(fn), _3, _4, _5,_6,_7);
  }

  public <H> T7<A,B,H,D,E,F,G> map3(F1<? super C,H> fn) {
   return t7(_1, _2, _3.map(fn), _4, _5, _6, _7);
  }

  public <H> T7<A,B,C,H,E,F,G> map4(F1<? super D,H> fn) {
   return t7(_1, _2, _3, _4.map(fn), _5, _6, _7);
  }

  public <H> T7<A,B,C,D,H,F,G> map5(F1<? super E,H> fn) {
   return t7(_1, _2, _3, _4, _5.map(fn), _6, _7);
  }

  public <H> T7<A,B,C,D,E,H,G> map6(F1<? super F,H> fn) {
   return t7(_1, _2, _3, _4, _5, _6.map(fn), _7);
  }

  public <H> T7<A,B,C,D,E,F,H> map7(F1<? super G,H> fn) {
   return t7(_1, _2, _3, _4, _5, _6, _7.map(fn));
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

  /**
   * Returns the seventh element of this product.
   * @return the seventh element of this product.
   */
  public G _7() {
    return _7.value();
  }
}
