package drjoliv.fjava.functions;

import java.util.function.Consumer;
import java.util.function.Supplier;

import drjoliv.fjava.control.Functor;
import drjoliv.fjava.data.FList;
import drjoliv.fjava.hkt.Witness;

public interface F0<A> extends Supplier<A>, Functor<F0.μ,A> {

  public interface μ extends Witness{}

  public A call();

  @Override
  public default <B> F0<B> map(F1<? super A, B> fn) {
    return fn.curry().call(call());
  }

  @Override
  public default A get() {
    return call();
  }
}
