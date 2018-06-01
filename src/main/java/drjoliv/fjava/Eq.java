package drjoliv.fjava;

import drjoliv.fjava.data.FList;
import drjoliv.fjava.data.T2;
import drjoliv.fjava.functions.F2;

import static drjoliv.fjava.data.FList.*;

public interface Eq<A> {
  public boolean call(A a1, A a2);

  public static Eq<Integer> eqInt = defaultEq();

  public static <A> Eq<A> defaultEq() {
    return (a1,a2) -> a1.equals(a2);
  }

  public static <A> Eq<FList<A>> eqFList(Eq<A> eq) {
    return new Eq<FList<A>>() {
      F2<A,A,Boolean> fn = eq::call;
      @Override
      public boolean call(FList<A> a1, FList<A> a2) {
        return a1.size() == a2.size()
          && trues((zip(a1,a2)
          .map(fn.tuple())));
      }
    };
  }
}
