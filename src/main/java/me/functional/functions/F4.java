package me.functional.functions;

public interface F4<A,B,C,D,E> extends QuadFunction<A,B,C,D,E> {
  public default F3<B,C,D,E> call(A a) {
    return partial(this,a);
  }

  public default F2<C,D,E> call(A a, B b) {
    return call(a).call(b);
  }

  public default F1<D,E> call(A a, B b, C c) {
    return call(a).call(b).call(c);
  }

  public E call(A a, B b, C c, D d);

  @Override
  public default E apply(A a, B b, C c, D d) {
    return call(a,b,c,d);
  }

  public static <A,B,C,D,E> F3<B,C,D,E> partial(F4<A,B,C,D,E> fn4, A a) {
    return (b,c,d) -> fn4.call(a,b,c,d);
  }

  public default F1<A,F3<B,C,D,E>> curry() {
    return a -> partial(this,a);
  }
}

