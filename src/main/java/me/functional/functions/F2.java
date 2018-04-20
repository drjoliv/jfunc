package me.functional.functions;

import java.util.function.BiFunction;

public interface F2<A,B,C> extends BiFunction<A,B,C> {

  public C call(A a, B b);

  public default F1<B,C> call(A a) {
    return partial(this,a);
  }

  @Override
  public default C apply(A a, B b) {
    return call(a,b);
  }

  public static <A,B,C> F1<B,C> partial(F2<A,B,C> fn2, A a) {
    return b -> fn2.call(a,b);
  }
}
