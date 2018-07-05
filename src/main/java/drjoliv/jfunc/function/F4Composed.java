package drjoliv.jfunc.function;

class F4Composed<A,B,C,D,E,F> implements F4<A,B,C,D,F> {
  
  private final F4<A,B,C,D,E> f4;
  private final F1<? super E, ? extends F> fn;

  private F4Composed(F4<A, B, C, D, E> f4, F1<? super E, ? extends F> fn) {
    this.f4 = f4;
    this.fn = fn;
  }

  @Override
  public F call(A a, B b, C c, D d) {
    return fn.call(f4.call(a,b,c,d));
  }

  @Override
  public <G> F4<A, B, C, D, G> then(F1<? super F, ? extends G> fn) {
    return doMap(f4, this.fn.then(fn));
  }

   static <A, B, C, D, E, F> F4<A, B, C, D, F> doMap(F4<A, B, C, D, E> f4, F1<? super E, ? extends F> fn) {
    return new F4Composed<>(f4,fn);
  }
}
