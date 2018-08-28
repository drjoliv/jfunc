package drjoliv.jfunc.foldable;

import static drjoliv.jfunc.contorl.eval.Eval.later;

import java.util.Comparator;

import drjoliv.jfunc.contorl.eval.Eval;
import drjoliv.jfunc.contorl.maybe.Maybe;
import drjoliv.jfunc.data.list.FList;
import drjoliv.jfunc.function.F2;
import drjoliv.jfunc.function.F3;
import drjoliv.jfunc.functor.Functor;
import drjoliv.jfunc.nums.Numbers;

/**
 * A data structure that can be folded.
 * <pre>{@code
 * F2<Integer,Integer,Integer> go = (i, ii) -> i + ii;
 * FList<Integer> ints = range(0, 10);
 * Integer sum = ints.foldr(go, 0)
 * // sum == 45
 * }}</pre>

 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public interface Foldable<M, A> extends Functor<M, A>{

  /**
   * Right associative fold of this structure.
   * @param fn function to fold over this data structure.
   * @param b seed value.
   * @return a reduced value.
   */
  public <B> B foldr(F2<B,? super A,B> fn, B b);

  /**
   * A lazy version of foldr.
   * @param fn function to fold over this data structure.
   * @param b seed value.
   * @return a eval containin a reduced value.
   */
  public default <B> Eval<B> foldr$(F2<B,A,B> fn, B b) {
    return later(() -> foldr(fn,b));
  }

  public class EmptyFoldableException extends RuntimeException{}

  public default A maximum(Comparator<A> comp) {
    Maybe<A> ma = safeMaximum(comp);
    if(ma.isSome())
      return ma.toNull();
    else
      throw new EmptyFoldableException();
  }

  /**
   * Retuns the largest value within this structure or nothing if it is empty.
   * @param comp a strategy for ordering the data within this structure.
   * @return the largest value within this structure or nothing if it is empty.
   */
  public default Maybe<A> safeMaximum(Comparator<A> comp) {
    F3<Comparator<A>,A,A,A> m = Numbers::max;
    F2<A,A,A> max = m.call(comp);
    F2<Maybe<A>, A, Maybe<A>> go = (ma, a) -> {
      if(ma.isSome())
        return ma.map(max.call(a));
      else
        return Maybe.maybe(a);
    };
    return foldr(go, Maybe.nothing());
  }

  public default A minimum(Comparator<A> comp) {
    Maybe<A> ma = safeMinimum(comp);
    if(ma.isSome())
      return ma.toNull();
    else
      throw new EmptyFoldableException();
  }

  /**
   * Retuns the smallest value within this structure or nothing if it is empty.
   * @param comp a strategy for ordering the data within this structure.
   * @return the smallest value within this structure or nothing if it is empty.
   */
  public default Maybe<A> safeMinimum(Comparator<A> comp) {
    F3<Comparator<A>,A,A,A> m = Numbers::min;
    F2<A,A,A> min = m.call(comp);
    F2<Maybe<A>, A, Maybe<A>> go = (ma, a) -> {
      if(ma.isSome())
        return ma.map(min.call(a));
      else
        return Maybe.maybe(a);
    };
    return foldr(go, Maybe.nothing());
  }

  /**
   * Returns a FList representation of this data structure.
   * @return a FList representation of this data structure.
   */
  public default FList<A> toFList() {
    F2<FList<A>,A,FList<A>> go = (l, a) -> l.cons(a);
    return foldr(go,FList.empty());
  }

  /**
   * Returns the number of elements within this foldable.
   * @return the number of elements in this foldable.
   */
  public default int length() {
    F2<Integer,A,Integer> go = (i, a) -> i + 1;
    return foldr(go, 0);
  }

  /**
   * Returns true if this data structure is empty and false otherwise.
   * @return true if this data structure is empty and false othherwise.
   */
  public default boolean isNull() {
    return length() == 0;
  }

}
