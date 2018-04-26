package me.functional.functions;

public interface F3<A,B,C,D> {

  public default F2<B,C,D> call(A a) {
    return partial(this,a);
  }

  public default F1<C,D> call(A a, B b) {
    return call(a).call(b);
  }

  public D call(A a, B b, C c);

  public static <A,B,C,D> F2<B,C,D> partial(F3<A,B,C,D> fn3, A a) {
    return (b,c) -> fn3.call(a,b,c);
  }

  public default F1<A,F2<B,C,D>> curry() {
    return a -> partial(this,a);
  }

}
