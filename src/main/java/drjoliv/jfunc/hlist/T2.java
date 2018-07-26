package drjoliv.jfunc.hlist;

import static drjoliv.jfunc.contorl.eval.Eval.*;

import drjoliv.jfunc.contorl.eval.Eval;
import drjoliv.jfunc.function.F1;

/**
 * The product of {@code A} and {@code B}.
 *
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public class T2<A,B> {
  public final Eval<A> _1;
  public final Eval<B> _2;

  private T2(Eval<A> a, Eval<B> b) {
    this._1 = a;
    this._2 = b;
  }

  public <C> T2<C,B> set1(Eval<C> eval) {
    return new T2<>(eval, _2);
  }

  public <C> T2<A,C> set2(Eval<C> eval) {
    return new T2<>(_1, eval);
  }

  /**
   * Creates the product of {@code a} and {@code b}.
   *
   * @param a the first element of the tuple.
   * @param b the second element of the tuple.
   * @return the tuple produce from the product of {@code a} and {@code b}.
   */
  public static <A,B> T2<A,B> t2(A a, B b) {
    return new T2<A,B>(now(a),now(b));
  }

  /**
   * Creates the product of {@code a} and {@code b}.
   *
   * @param a the first element of the tuple.
   * @param b the second element of the tuple.
   * @return the tuple produce from the product of {@code a} and {@code b}.
   */
  public static <A,B> T2<A,B> t2(Eval<A> a, Eval<B> b) {
    return new T2<A,B>(a,b);
  }

  /**
   * Returns tuple produced by transforming the fisrt and second elements of this product.
   *
   * @param fst transforms the first element of this product.
   * @param snd transforms the second element of this product.
   * @return the tuple produced by transforming the fisrt and second elements of this product.
   */
  public <C,D > T2<C,D> bimap(F1<? super A, ? extends C> fst, F1<? super B, ? extends D>snd) {
    return t2(_1.map(fst),_2.map(snd));
  }

  public <C> T2<C,B> map1(F1<? super A, ? extends C> fn) {
   return t2(_1.map(fn),_2);
  }

  public <C> T2<A,C> map2(F1<? super B, ? extends C> fn) {
   return t2(_1,_2.map(fn));
  }

  /**
   *
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

  public boolean equals( Object obj ) {
     if(obj instanceof T2) {
       T2<A,B> t = (T2<A,B>) obj;
       return _1().equals(t._1()) && _2().equals(t._2());
     }
     else
       return false;
  }

  public int hashCode() {
    return _1().hashCode() + _2().hashCode();
  }
}
