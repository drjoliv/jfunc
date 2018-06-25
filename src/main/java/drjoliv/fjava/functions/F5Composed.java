package drjoliv.fjava.functions;

class F5Composed<A,B,C,D,E,F,G> implements F5<A,B,C,D,E,G>{

  private final F5<A,B,C,D,E,F> f5;
  private final F1<? super F, ? extends G> fn;

  private F5Composed(F5<A, B, C, D, E, F> f5, F1<? super F, ? extends G> fn) {
    this.f5 = f5;
    this.fn = fn;
  }

  @Override
  public <H> F5<A, B, C, D, E, H> map(F1<? super G, ? extends H> fn) {
    return doMap(f5,this.fn.then(fn));
  }

   static <A, B, C, D, E, F, G> F5<A, B, C, D, E, G> doMap(F5<A, B, C, D, E, F> f5, F1<? super F, ? extends G> fn) {
    return new F5Composed<>(f5,fn);
  }

  @Override
  public G call(A a, B b, C c, D d, E e) {
    return fn.call(f5.call(a,b,c,d,e));
  }
}
