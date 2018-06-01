package drjoliv.fjava.functions;

import drjoliv.fjava.control.Functor;
import drjoliv.fjava.data.T4;
import drjoliv.fjava.hkt.Hkt4;
import drjoliv.fjava.hkt.Witness;

public interface F4<A,B,C,D,E> extends Functor<Hkt4<F4.μ,A,B,C,D>,E> {

  public static class μ implements Witness{}

  @Override
  public default <F> F4<A,B,C,D,F> map(F1<? super E, F> fn) {
    return F4Composed.doMap(this,fn);
  }

  public E call(A a, B b, C c, D d);

  public default F1<D,E> call(A a, B b, C c) {
    return call(a).call(b).call(c);
  }

  public default F2<C,D,E> call(A a, B b) {
    return call(a).call(b);
  }

  public default F3<B,C,D,E> call(A a) {
    return partial(this,a);
  }

  public default F1<A,F3<B,C,D,E>> curry() {
    return a -> partial(this,a);
  }

  public default F1<T4<A,B,C,D>,E> t4() {
    return t -> call(t._1(),t._2(),t._3(),t._4());
  }

  public static <A,B,C,D,E> F3<B,C,D,E> partial(F4<A,B,C,D,E> fn4, A a) {
    return (b,c,d) -> fn4.call(a,b,c,d);
  }
}

