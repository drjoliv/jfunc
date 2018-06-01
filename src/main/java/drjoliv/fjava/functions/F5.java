package drjoliv.fjava.functions;

import drjoliv.fjava.control.Functor;
import drjoliv.fjava.data.T5;
import drjoliv.fjava.hkt.Hkt5;
import drjoliv.fjava.hkt.Witness;

public interface F5<A,B,C,D,E,F> extends Functor<Hkt5<F5.μ,A,B,C,D,E>,F> {

 public F call(A a, B b, C c, D d, E e);

  public static class μ implements Witness{}

  @Override
  public default <G> F5<A,B,C,D,E,G> map(F1<? super F, G> fn) {
    return F5Composed.doMap(this,fn);
  }

  public default F1<E,F> call(A a, B b, C c, D d) {
    return call(a).call(b).call(c).call(d);
  }

  public default F3<C,D,E,F> call(A a, B b) {
    return call(a).call(b);
  }

  public default F4<B,C,D,E,F> call(A a) {
    return partial(this,a);
  }

  public default F1<A,F4<B,C,D,E,F>> curry() {
    return a -> partial(this,a);
  }

  public default F1<T5<A,B,C,D,E>,F> t5() {
    return t -> call(t._1(),t._2(),t._3(), t._4(), t._5());
  }

  public static <A,B,C,D,E,F> F4<B,C,D,E,F> partial(F5<A,B,C,D,E,F> fn4, A a) {
    return (b,c,d,e) -> fn4.call(a,b,c,d,e);
  }
}
