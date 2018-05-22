package drjoliv.fjava.functions;

import java.util.ArrayList;
import java.util.function.Function;

import drjoliv.fjava.data.DList;
import drjoliv.fjava.data.Dequeue;
import static drjoliv.fjava.data.Dequeue.*;
import drjoliv.fjava.data.FList;
import static drjoliv.fjava.data.FList.*;
import drjoliv.fjava.data.Stack;
import static drjoliv.fjava.functions.ComposedFunc.*;

public interface F1<A,B>{

  public B call(A a);

  public default F1<A,F0<B>> curry() {
    return a -> () -> call(a);
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
