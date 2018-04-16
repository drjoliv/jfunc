package me.functional.functions;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface F0<A> extends Supplier<A> {
  public A call();

  @Override
  public default A get() {
    return call();
  }

}
