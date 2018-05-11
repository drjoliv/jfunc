package drjoliv.fjava.data;

import drjoliv.fjava.functions.F1;

/**
 * The product of {@code A} and {@code B}.
 *
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public class T2<A,B> {
  public final A _1;
  public final B _2;

  private T2(A a, B b) {
    this._1 = a;
    this._2 = b;
  }

  /**
   * Creates the product of {@code a} and {@code b}.
   *
   * @param a the first element of the tuple.
   * @param b the second element of the tuple.
   * @return the tuple produce from the product of {@code a} and {@code b}.
   */
  public static <A,B> T2<A,B> t2(A a, B b) {
    return new T2<A,B>(a,b);
  }

  /**
   * Returns tuple produced by transforming the fisrt and second elements of this product.
   *
   * @param fst transforms the first element of this product.
   * @param snd transforms the second element of this product.
   * @return the tuple produced by transforming the fisrt and second elements of this product.
   */
  public <C,D >T2<C,D> bimap(F1<? super A,C> fst, F1<? super B,D>snd) {
    return t2(fst.call(this._1),snd.call(this._2));
  }

  /**
   *
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
}
