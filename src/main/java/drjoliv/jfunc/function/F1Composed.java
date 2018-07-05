package drjoliv.jfunc.function;

import drjoliv.jfunc.collection.Dequeue;

class F1Composed<A,B> implements F1<A,B> {

    @SuppressWarnings("rawtypes")
    static boolean isComposedFunc(F1 fn) {
      return fn instanceof F1Composed;
    }

    @SuppressWarnings("rawtypes")
    static F1Composed asComposedFunc(F1 fn) {
      return (F1Composed)fn;
    }

    @SuppressWarnings("rawtypes")
    private final Dequeue<F1> dequeue;

    F1Composed(@SuppressWarnings("rawtypes") Dequeue<F1> dequeue) {
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
    public <C> F1<A, C> then(F1<? super B, ? extends C> fn) {
     return isComposedFunc(fn)
          ? new F1Composed<A,C>(dequeue.concat(asComposedFunc(fn).dequeue))
          : new F1Composed<A,C>(dequeue.pushBack(fn));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C> F1<C, B> before(F1<? super C, ? extends A> fn) {
     return isComposedFunc(fn)
          ? new F1Composed<C,B>(asComposedFunc(fn).dequeue.concat(dequeue))
          : new F1Composed<C,B>(dequeue.pushFront(fn));
    }
}
