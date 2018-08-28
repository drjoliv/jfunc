package drjoliv.jfunc.eq;

import drjoliv.jfunc.contorl.CaseOf;

import static drjoliv.jfunc.contorl.CaseOf.*;

public interface Ord<A> extends EQ<A>{

  public Ordering compare(A a, A a1);

  public default boolean lt(A a, A a1) {
    if(compare(a,a1) == Ordering.LT)
      return true;
    else
      return false;
  }

  public default boolean gt(A a, A a1) {
    if(compare(a,a1) == Ordering.GT)
      return true;
    else
      return false;
  }

  @Override
  public default boolean eq(A a, A a1) {
    if(compare(a,a1) == Ordering.EQ)
      return true;
    else
      return false;
  }

  public static <A extends Comparable<A>> Ord<A> orderable() {
    return new Ord<A>() {
      @Override
      public Ordering compare(A a, A a1) {
        int i = a.compareTo(a1);
        if(i == -1)
          return Ordering.LT;
        else if(i == 1)
          return Ordering.GT;
        else
          return Ordering.EQ;
      }
    };
  }
}
