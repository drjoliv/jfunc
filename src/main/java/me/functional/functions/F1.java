package me.functional.functions;

import java.util.function.Function;

public interface F1<A,B>{

  public B call(A a);

  public default F1<A,F0<B>> curry() {
    return a -> () -> call(a);
  }

  public default Function<A,B> toJFunc() {
    return a -> call(a);
  }

  public default <C> F1<A,C> then(F1< ? super B, ? extends C> fn) {
    return a -> fn.call(call(a));
  }

  public default <C> F1<C,B> before(F1<C,A> fn) {
    return c -> call(fn.call(c));
  }


}
