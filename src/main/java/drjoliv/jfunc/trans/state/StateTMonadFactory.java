package drjoliv.jfunc.trans.state;

import drjoliv.jfunc.data.Unit;
import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.hkt.Hkt2;
import drjoliv.jfunc.hlist.T2;
import drjoliv.jfunc.monad.Monad;
import drjoliv.jfunc.monad.MonadFactory;
import drjoliv.jfunc.trans.state.StateT.StateTImpl;

public class StateTMonadFactory<M,S> implements MonadFactory<Hkt2<statet,M,S>> {

  private final MonadFactory<M> innerMonadFactory;

  StateTMonadFactory(MonadFactory<M> innerMonadFactory) {
    this.innerMonadFactory = innerMonadFactory;
  }

  public static <M,S> StateTMonadFactory<M,S> stateTFactory(MonadFactory<M> innerMonadFactory) {
    return new StateTMonadFactory<>(innerMonadFactory);
  }

  @Override
  public <A> StateT<M,S,A> unit(A a) {
    return new StateTImpl<>(s -> innerMonadFactory.unit(T2.t2(s,a)), innerMonadFactory, this);
  }

  @Override
  public <A> StateT<M,S,A> pure(A a) {
    return new StateTImpl<>(s -> innerMonadFactory.unit(T2.t2(s,a)), innerMonadFactory, this);
  }

  public <A> StateT<M,S,A> lift(Monad<M,A> ma) {
    return new StateTImpl<>(s -> ma.map(a -> T2.t2(s,a)), innerMonadFactory, this);
  }

  public <A> StateT<M,S,A> of(F1<S,Monad<M,T2<S,A>>> fn) {
    return new StateTImpl<>(fn, innerMonadFactory, this);
  }

  public StateT<M,S,S> get() {
    return new StateTImpl<>(s -> innerMonadFactory.unit(T2.t2(s, s)), innerMonadFactory, this);
  }

  public StateT<M, S, Unit> put(S s) {
    return new StateTImpl<>(ignore -> innerMonadFactory.unit(T2.t2(s, Unit.unit)), innerMonadFactory, this);
  }

  public StateT<M, S, Unit> modify(F1<S,S> fn) {
    return get().bind(s -> put(fn.call(s)));
  }

  public <A> StateT<M, S, A> gets(F1<S,A> fn) {
    return get().bind(s -> unit(fn.call(s)));
  }
}
