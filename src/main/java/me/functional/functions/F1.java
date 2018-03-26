package me.functional.functions;

import java.util.function.Function;

public interface F1<A,B> extends Function<A,B> {

  public B call(A a);

  @Override
  public default B apply(A a) {
    return call(a);
  }
}
