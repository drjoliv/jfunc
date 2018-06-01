package drjoliv.fjava.functions;

import drjoliv.fjava.control.Functor;
import drjoliv.fjava.data.T6;
import drjoliv.fjava.hkt.Hkt6;
import drjoliv.fjava.hkt.Witness;

public interface F6<A,B,C,D,E,F,G> extends Functor<Hkt6<F6.μ,A,B,C,D,E,F>,G>{

  public G call(A a, B b, C c, D d, E e, F f);

  public static class μ implements Witness{}

  @Override
  public default <H> F6<A,B,C,D,E,F,H> map(F1<? super G, H> fn) {
    return F6Composed.doMap(this,fn);
  }

  public default F1<F,G> call(A a, B b, C c, D d, E e) {
    return call(a).call(b).call(c).call(d).call(e);
  }

  public default F2<E,F,G> call(A a, B b, C c, D d) {
    return call(a).call(b).call(c).call(d);
  }

  public default F3<D,E,F,G> call(A a, B b, C c) {
    return call(a).call(b).call(c);
  }

  public default F4<C,D,E,F,G> call(A a, B b) {
    return call(a).call(b);
  }

  public default F5<B,C,D,E,F,G> call(A a) {
    return partial(this,a);
  }

  public default F1<A,F5<B,C,D,E,F,G>> curry() {
    return a -> partial(this,a);
  }

  public default F1<T6<A,B,C,D,E,F>,G> t6() {
    return t -> call(t._1(),t._2(),t._3(), t._4(), t._5(), t._6());
  }

  public static <A,B,C,D,E,F,G> F5<B,C,D,E,F,G> partial(F6<A,B,C,D,E,F,G> fn6, A a) {
    return (b,c,d,e,f) -> fn6.call(a,b,c,d,e,f);
  }
}
