package drjoliv.fjava.functions;

import drjoliv.fjava.data.Dequeue;

class ComposedFunc<A,B> implements F1<A,B> {

  @SuppressWarnings("rawtypes")
  static boolean isComposedFunc(F1 fn) {
      return fn instanceof ComposedFunc;
    }

  @SuppressWarnings("rawtypes")
  static ComposedFunc asComposedFunc(F1 fn) {
      return (ComposedFunc)fn;
    }

    @SuppressWarnings("rawtypes")
    private final Dequeue<F1> dequeue;

    ComposedFunc(@SuppressWarnings("rawtypes") Dequeue<F1> dequeue) {
      this.dequeue = dequeue;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public B call(A a) {
      Object b = a; 
      for(F1 f : dequeue)
        b = f.call(b);
      return (B) b;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C> F1<A, C> then(F1<? super B, C> fn) {
     return isComposedFunc(fn)
          ? new ComposedFunc<A,C>(dequeue.concat(asComposedFunc(fn).dequeue))
          : new ComposedFunc<A,C>(dequeue.pushBack(fn));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C> F1<C, B> before(F1<C, ? extends A> fn) {
     return isComposedFunc(fn)
          ? new ComposedFunc<C,B>(asComposedFunc(fn).dequeue.concat(dequeue))
          : new ComposedFunc<C,B>(dequeue.pushFront(fn));
    }
}
