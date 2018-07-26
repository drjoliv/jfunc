package drjoliv.jfunc.trans.state;

import drjoliv.jfunc.applicative.Applicative;
import drjoliv.jfunc.data.Unit;
import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.function.F2;
import drjoliv.jfunc.function.F3;
import drjoliv.jfunc.function.F4;
import drjoliv.jfunc.function.F5;
import drjoliv.jfunc.hkt.Hkt2;
import drjoliv.jfunc.hlist.T2;
import drjoliv.jfunc.monad.Identity;
import drjoliv.jfunc.monad.Identity.μ;
import drjoliv.jfunc.monad.Monad;
import drjoliv.jfunc.monad.MonadFactory;

public abstract class State<S,A> extends StateT<Identity.μ, S, A>  {

  private State(final MonadFactory<Identity.μ> innerMonadFactory, MonadFactory<Hkt2<statet, Identity.μ, S>> factory) {
    super(innerMonadFactory, factory);
  }

  @Override
  public abstract <B> State<S, B> map(F1<? super A, ? extends B> fn);

  @Override
  public abstract <B, C> State<S, F1<B, C>> map(F2<? super A, B, C> fn);

  @Override
  public abstract <B, C, D> State<S, F1<B, F1<C, D>>> map(F3<? super A, B, C, D> fn);

  @Override
  public abstract <B, C, D, E> State<S, F1<B, F1<C, F1<D, E>>>> map(F4<? super A, B, C, D, E> fn);

  @Override
  public abstract <B, C, D, E, G> State<S, F1<B, F1<C, F1<D, F1<E, G>>>>> map(F5<? super A, B, C, D, E, G> fn);

  @Override
  public abstract <B> State<S, B> apply(
      Applicative<Hkt2<statet, Identity.μ, S>, ? extends F1<? super A, ? extends B>> applicative);

  @Override
  public abstract <B, C> State<S, C> For(F1<? super A, ? extends Monad<Hkt2<statet, μ, S>, B>> fn,
      F2<? super A, ? super B, ? extends Monad<Hkt2<statet, μ, S>, C>> fn2);

  @Override
  public abstract <B, C, D> State<S, D> For(F1<? super A, ? extends Monad<Hkt2<statet, μ, S>, B>> fn,
      F2<? super A, ? super B, ? extends Monad<Hkt2<statet, μ, S>, C>> fn2,
      F3<? super A, ? super B, ? super C, ? extends Monad<Hkt2<statet, μ, S>, D>> fn3);

  @Override
  public abstract <B, C, D, E> State<S, E> For(F1<? super A, ? extends Monad<Hkt2<statet, μ, S>, B>> fn,
      F2<? super A, ? super B, ? extends Monad<Hkt2<statet, μ, S>, C>> fn2,
      F3<? super A, ? super B, ? super C, ? extends Monad<Hkt2<statet, μ, S>, D>> fn3,
      F4<? super A, ? super B, ? super C, ? super D, ? extends Monad<Hkt2<statet, μ, S>, E>> fn4);

  @Override
  public abstract <B> State<S, B> bind(F1<? super A, ? extends Monad<Hkt2<statet, μ, S>, B>> fn);

  @Override
  public abstract <B> State<S, B> semi(Monad<Hkt2<statet, μ, S>, B> mb);

  @Override
  public abstract Identity<T2<S, A>> call(S s);

  public A evalState(S s) {
    return call(s).value()._2();
  }

  public S execState(S s) {
    return call(s).value()._1();
  }

  public T2<S,A> run(S s) {
    return call(s).value();
  }

  public static <S> State<S, S> get() {
    StateMonadFactory<S> stf = StateMonadFactory.instance();
    return stf.get();
  }

  public static <S,A> State<S, A> gets(F1<S, A> fn) {
    StateMonadFactory<S> stf = StateMonadFactory.instance();
    return stf.gets(fn);
  }

  public static <S,A> State<S,A> state(F1<S, T2<S,A>> fn) {
    StateMonadFactory<S> stf = StateMonadFactory.instance();
    return stf.of(fn.then(t -> Identity.id(t)));
  }

  public static <S> State<S, Unit> put(S s) {
    StateMonadFactory<S> stf = StateMonadFactory.instance();
    return stf.put(s);
  }

  public static <S,A> State<S, A> ret(A a) {
    StateMonadFactory<S> stf = StateMonadFactory.instance();
    return stf.unit(a);
  }

  public static <S> State<S,Unit> modify(F1<S, S> fn) {
    StateMonadFactory<S> stf = StateMonadFactory.instance();
    return stf.modify(fn);
  }

  static class StateImpl<S,A> extends State<S,A> {

    private final StateT<Identity.μ, S, A> state;

    StateImpl(StateT<Identity.μ, S, A> state) {
      super(state.getInnerMonadFactory(), state.yield());
      this.state = state;
    }

    @Override
    public <B> State<S, B> map(F1<? super A, ? extends B> fn) {
      return new StateImpl<>(state.map(fn));
    }

    @Override
    public <B, C> State<S, F1<B, C>> map(F2<? super A, B, C> fn) {
      return new StateImpl<>(state.map(fn));
    }

    @Override
    public <B, C, D> State<S, F1<B, F1<C, D>>> map(F3<? super A, B, C, D> fn) {
      return new StateImpl<>(state.map(fn));
    }

    @Override
    public <B, C, D, E> State<S, F1<B, F1<C, F1<D, E>>>> map(F4<? super A, B, C, D, E> fn) {
      return new StateImpl<>(state.map(fn));
    }

    @Override
    public <B, C, D, E, G> State<S, F1<B, F1<C, F1<D, F1<E, G>>>>> map(F5<? super A, B, C, D, E, G> fn) {
      return new StateImpl<>(state.map(fn));
    }

    @Override
    public <B> State<S, B> apply(
        Applicative<Hkt2<statet, Identity.μ, S>, ? extends F1<? super A, ? extends B>> applicative) {
      return new StateImpl<>(state.apply(applicative));
    }

    @Override
    public <B> State<S, B> bind(F1<? super A, ? extends Monad<Hkt2<statet, μ, S>, B>> fn) {
      return new StateImpl<>(state.bind(fn));
    }

    @Override
    public <B> State<S, B> semi(Monad<Hkt2<statet, μ, S>, B> mb) {
      return bind(m -> mb);
    }

    @Override
    public <B, C> State<S, C> For(F1<? super A, ? extends Monad<Hkt2<statet, μ, S>, B>> fn,
        F2<? super A, ? super B, ? extends Monad<Hkt2<statet, μ, S>, C>> fn2) {
      return new StateImpl<>(state.For(fn,fn2));
    }

    @Override
    public <B, C, D> State<S, D> For(F1<? super A, ? extends Monad<Hkt2<statet, μ, S>, B>> fn,
        F2<? super A, ? super B, ? extends Monad<Hkt2<statet, μ, S>, C>> fn2,
        F3<? super A, ? super B, ? super C, ? extends Monad<Hkt2<statet, μ, S>, D>> fn3) {
      return new StateImpl<>(state.For(fn, fn2, fn3));
    }

    @Override
    public <B, C, D, E> State<S, E> For(F1<? super A, ? extends Monad<Hkt2<statet, μ, S>, B>> fn,
        F2<? super A, ? super B, ? extends Monad<Hkt2<statet, μ, S>, C>> fn2,
        F3<? super A, ? super B, ? super C, ? extends Monad<Hkt2<statet, μ, S>, D>> fn3,
        F4<? super A, ? super B, ? super C, ? super D, ? extends Monad<Hkt2<statet, μ, S>, E>> fn4) {
      return new StateImpl<>(state.For(fn, fn2, fn3, fn4));
    }

    @Override
    public Identity<T2<S, A>> call(S s) {
      return Identity.monad(state.call(s));
    }
  }
}
