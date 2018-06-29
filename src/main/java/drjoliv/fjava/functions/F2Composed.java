package drjoliv.fjava.functions;

class F2Composed<A,B,C,D> implements F2<A,B,D>{

  private final F2<A,B,C> f2;
  private final F1<? super C, ? extends D> fn;

  private F2Composed(F2<A, B, C> f2, F1<? super C, ? extends D> fn) {
    this.f2 = f2;
    this.fn = fn;
  }

  static <A,B,C,D> F2<A,B,D> doMap(F2<A,B,C> f2, final F1<? super C, ? extends D> fn) {
    return new F2Composed<A,B,C,D>(f2,fn);
  }

  @Override
  public D call(A a, B b) {
    return fn.call(f2.call(a,b));
  }

  @Override
  public <E> F2<A, B, E> then(F1<? super D, ? extends E> fn) {
    return new F2Composed<A,B,C,E>(f2, this.fn.then(fn));
  }
}
