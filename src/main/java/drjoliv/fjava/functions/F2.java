package drjoliv.fjava.functions;

import java.util.function.BiFunction;

import drjoliv.fjava.control.Functor;
import drjoliv.fjava.data.T2;
import drjoliv.fjava.hkt.Hkt2;
import drjoliv.fjava.hkt.Witness;

public interface F2<A,B,C> extends BiFunction<A,B,C>, Functor<Hkt2<F2.μ,A,B>,C> {

  public static class μ implements Witness{}

  public C call(A a, B b);

  @Override
  public default C apply(A a, B b) {
    return call(a,b);
  }

  @Override
  public default <D> F2<A,B,D> map(final F1<? super C, D> fn) {
    return F2Composed.doMap(this, fn);
  }

  public default F1<B,C> call(A a) {
    return partial(this,a);
  }

  public default F1<T2<A,B>, C> tuple() {
    return t -> call(t._1(),t._2());
  }

  public static <A,B,C> F1<B,C> partial(F2<A,B,C> fn2, A a) {
    return b -> fn2.call(a,b);
  }
}
