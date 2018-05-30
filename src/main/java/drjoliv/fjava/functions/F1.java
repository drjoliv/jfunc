package drjoliv.fjava.functions;

import static drjoliv.fjava.data.Dequeue.dequeue;
import static drjoliv.fjava.functions.ComposedFunc.isComposedFunc;

import java.util.function.Function;

import drjoliv.fjava.control.Functor;
import drjoliv.fjava.hkt.Hkt;
import drjoliv.fjava.hkt.Witness;

public interface F1<A,B> extends Functor<Hkt<F1.μ,A>,B>{

  public class μ implements Witness{}

  public B call(A a);

  public default F1<A,F0<B>> curry() {
    return a -> () -> call(a);
  }

  @Override
  public default <C> F1<A,C> map(F1< ? super B, C> fn) {
    return then(fn);
  }

  public default Function<A,B> toJFunc() {
    return a -> call(a);
  }


  public default <C> F1<A,C> then(F1< ? super B, C> fn) {
    return fn.before(this);
  }

  public default <C> F1<C,B> before(F1<C ,? extends A> fn) {
    return isComposedFunc(fn)
      ? fn.then(this)
      : new ComposedFunc<C,B>(dequeue(fn,this));
  }

  public static <A> F1<A, A> identity() {
    return a -> a;
  }

  public static <A, B> F1<A, B> fromJFunction(Function<A, B> fn) {
    return a -> fn.apply(a);
  }
}
