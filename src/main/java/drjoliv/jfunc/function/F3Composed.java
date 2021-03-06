package drjoliv.jfunc.function;

import drjoliv.jfunc.functor.Functor;
import drjoliv.jfunc.hkt.Hkt3;

class F3Composed<A,B,C,D,E> implements F3<A,B,C,E> {

  private final F3<A,B,C,D> f3;
  private final F1<? super D, ? extends E> fn;

  private F3Composed(F3<A, B, C, D> f3, F1<? super D, ? extends E> fn) {
    this.f3 = f3;
    this.fn = fn;
  }

  @Override
  public E call(A a, B b, C c) {
    return fn.call(f3.call(a,b,c));
  }

  public static <A,B,C,D,E> F3<A,B,C,E> doMap(F3<A,B,C,D> f3, F1<? super D, ? extends E> fn) {
    return new F3Composed<>(f3,fn);
  }

  @Override
  public <F> F3<A,B,C,F> then(F1<? super E, ? extends F> fn) {
    return doMap(f3,this.fn.then(fn));
  }
}
