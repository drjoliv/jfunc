package drjoliv.fjava.functions;

class F6Composed<A,B,C,D,E,F,G,H> implements F6<A,B,C,D,E,F,H>{
  private final F6<A,B,C,D,E,F,G> f6;
  private final F1<? super G, ? extends H> fn;

  public F6Composed(F6<A, B, C, D, E, F, G> f6, F1<? super G, ? extends H> fn) {
    this.f6 = f6;
    this.fn = fn;
  }

   static <A, B, C, D, E, F, G, H> F6<A, B, C, D, E, F, H> doMap(F6<A, B, C, D, E, F, G> f6, F1<? super G, ? extends H> fn) {
    return new F6Composed<>(f6,fn);
  }

  @Override
  public H call(A a, B b, C c, D d, E e, F f) {
    return fn.call(f6.call(a,b,c,d,e,f));
  }

  @Override
  public <I> F6<A, B, C, D, E, F, I> then(F1<? super H, ? extends I> fn) {
    return doMap(f6, this.fn.then(fn));
  }
}
