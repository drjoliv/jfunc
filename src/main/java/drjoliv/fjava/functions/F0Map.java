package drjoliv.fjava.functions;

class F0Map<A,B> implements F0<B> {

  private final F0<A> supplier;
  private final F1<? super A, ? extends B> mapper;

  private F0Map(F0<A> supplier, F1<? super A, ? extends B> mapper) {
    this.supplier = supplier;
    this.mapper = mapper;
  }

static <A,B,C,D> F0<B> doMap(F0<A>f2, final F1<? super A, ? extends B> fn) {
    return new F0Map<>(f2,fn);
  }

  @Override
  public <C> F0<C> map(F1<? super B, ? extends C> fn) {
    return new F0Map<A,C>(supplier, mapper.then(fn));
  }

  @Override
  public B call() {
    return mapper.call(supplier.call());
  }
}
