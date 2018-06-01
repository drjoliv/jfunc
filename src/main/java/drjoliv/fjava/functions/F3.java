package drjoliv.fjava.functions;

import drjoliv.fjava.control.Functor;
import drjoliv.fjava.data.T3;
import drjoliv.fjava.hkt.Hkt3;
import drjoliv.fjava.hkt.Witness;

public interface F3<A,B,C,D> extends Functor<Hkt3<F1.μ,A,B,C>,D> {

  public static class μ implements Witness {}

  @Override
  public default <E> F3<A,B,C,E> map(F1<? super D, E> fn) {
    return F3Composed.doMap(this,fn);
  }

  public D call(A a, B b, C c);

  public default F1<C,D> call(A a, B b) {
    return call(a).call(b);
  }

  public default F2<B,C,D> call(A a) {
    return partial(this,a);
  }

  public default F1<T3<A,B,C>,D> t3() {
    return t -> call(t._1(),t._2(), t._3());
  }

  public default F1<A,F2<B,C,D>> curry() {
    return a -> partial(this,a);
  }

  public static <A,B,C,D> F2<B,C,D> partial(F3<A,B,C,D> fn3, A a) {
    return (b,c) -> fn3.call(a,b,c);
  }

}
