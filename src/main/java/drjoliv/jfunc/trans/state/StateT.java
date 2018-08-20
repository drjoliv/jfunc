package drjoliv.jfunc.trans.state;

import drjoliv.jfunc.applicative.Applicative;
import drjoliv.jfunc.applicative.ApplicativeFactory;
import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.function.F2;
import drjoliv.jfunc.function.F3;
import drjoliv.jfunc.function.F4;
import drjoliv.jfunc.function.F5;
import drjoliv.jfunc.hkt.Hkt2;
import drjoliv.jfunc.hlist.T2;
import drjoliv.jfunc.monad.Monad;
import drjoliv.jfunc.monad.MonadFactory;

/**
 * The StateT monad.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public abstract class StateT<M,S,A> implements Monad<Hkt2<statet,M,S>,A> {

  private final MonadFactory<M> innerMonadFactory;
  private final MonadFactory<Hkt2<statet,M,S>> factory;

  StateT(final MonadFactory<M> innerMonadFactory, MonadFactory<Hkt2<statet,M,S>> factory) {
    this.innerMonadFactory = innerMonadFactory;
    this.factory = factory;
  }

  @Override
  public abstract <B> StateT<M, S, B> map(final F1<? super A, ? extends B> fn);

  @Override
  public <B, C> StateT<M, S, F1<B, C>> map(F2<? super A, B, C> fn) {
    return (StateT<M, S, F1<B, C>>)Monad.super.map(fn);
  }

  @Override
  public <B, C, D> StateT<M, S, F1<B, F1<C, D>>> map(F3<? super A, B, C, D> fn) {
    return (StateT<M, S, F1<B, F1<C, D>>>)Monad.super.map(fn);
  }

  @Override
  public <B, C, D, E> StateT<M, S, F1<B, F1<C, F1<D, E>>>> map(F4<? super A, B, C, D, E> fn) {
    return (StateT<M, S, F1<B, F1<C, F1<D, E>>>>)Monad.super.map(fn);
  }

  @Override
  public <B, C, D, E, G> StateT<M, S, F1<B, F1<C, F1<D, F1<E, G>>>>> map(
      F5<? super A, B, C, D, E, G> fn) {
    return (StateT<M, S, F1<B, F1<C, F1<D, F1<E, G>>>>>)Monad.super.map(fn);
  }

  @Override
  public abstract <B> StateT<M, S, B> apply(
      Applicative<Hkt2<statet, M, S>, ? extends F1<? super A, ? extends B>> applicative);

  @Override
  public abstract <B> StateT<M,S,B> bind(final F1<? super A, ? extends Monad<Hkt2<statet, M, S>, B>> fn);

  @Override
  public abstract <B> StateT<M,S,B> semi(final Monad<Hkt2<statet, M, S>, B> mb);

  @Override
  public <B, C> StateT<M, S, C> For(F1<? super A, ? extends Monad<Hkt2<statet, M, S>, B>> fn,
      F2<? super A, ? super B, ? extends Monad<Hkt2<statet, M, S>, C>> fn2) {
    return (StateT<M, S, C>)Monad.super.For(fn, fn2);
  }

  @Override
  public <B, C, D> StateT<M, S, D> For(F1<? super A, ? extends Monad<Hkt2<statet, M, S>, B>> fn,
      F2<? super A, ? super B, ? extends Monad<Hkt2<statet, M, S>, C>> fn2,
      F3<? super A, ? super B, ? super C, ? extends Monad<Hkt2<statet, M, S>, D>> fn3) {
    return (StateT<M, S, D>)Monad.super.For(fn, fn2, fn3);
  }

  @Override
  public <B, C, D, E> StateT<M, S, E> For(F1<? super A, ? extends Monad<Hkt2<statet, M, S>, B>> fn,
      F2<? super A, ? super B, ? extends Monad<Hkt2<statet, M, S>, C>> fn2,
      F3<? super A, ? super B, ? super C, ? extends Monad<Hkt2<statet, M, S>, D>> fn3,
      F4<? super A, ? super B, ? super C, ? super D, ? extends Monad<Hkt2<statet, M, S>, E>> fn4) {
    return (StateT<M, S, E>)Monad.super.For(fn, fn2, fn3, fn4);
  }

  @Override
  public ApplicativeFactory<Hkt2<statet, M, S>> pure() {
    return factory;
  }

  @Override
  public MonadFactory<Hkt2<statet, M, S>> yield() {
    return factory;
  }

  public abstract Monad<M,T2<S,A>> call(S s);

  public F1<S, Monad<M, T2<S, A>>> runState() {
    return this::call;
  }

  MonadFactory<M> getInnerMonadFactory() {
    return innerMonadFactory;
  }

  static class StateTImpl<M, S, A> extends StateT<M, S, A> {

    private final F1<S,Monad<M,T2<S,A>>> runState;

    StateTImpl(final F1<S,Monad<M,T2<S,A>>> runState, final MonadFactory<M> innerMonadFactory, MonadFactory<Hkt2<statet,M,S>> factory){
     super(innerMonadFactory, factory);
     this.runState = runState;
    }

    @Override
    public <B> StateT<M,S,B> map(final F1<? super A, ? extends B> fn) {
       return new StateTImpl<M,S,B>(s -> {
        return call(s).map(p -> p.map2(fn));
      }, getInnerMonadFactory(), yield());
    }

    @Override
    public <B> StateT<M,S,B> apply(Applicative<Hkt2<statet, M, S>, ? extends F1<? super A, ? extends B>> applicative) {
      return new StateTImpl<>(s -> {
       Monad<M,T2<S,A>> mv = call(s);
       return mv.bind(t -> {
          Monad<M,T2<S,F1<? super A, B>>> mf = ((StateT<M, S, F1<? super A, B>>)applicative).call(s);
          return mf.map( t2 -> t2.map2( fn -> fn.call(t._2()))); 
       });
      }, getInnerMonadFactory(), yield());
    }

    @Override
    public <B> StateT<M,S,B> bind(final F1<? super A, ? extends Monad<Hkt2<statet, M, S>, B>> fn) {
      return new StateTImpl<M,S,B>(s -> {
        return Monad.For(call(s), t -> go(fn, t._2(), t._1()));
      }, getInnerMonadFactory(), yield());
    }

    private static <M,S,A,B> Monad<M,T2<S,B>> go(final F1<? super A, ? extends Monad<Hkt2<statet, M, S>, B>> fn, A a, S s) {
      return monad(fn.call(a)).call(s);
    }

    @Override
    public <B> StateT<M,S,B> semi(final Monad<Hkt2<statet, M, S>, B> mb) {
      return bind(a -> mb);
    }

    @Override
    public Monad<M, T2<S, A>> call(S s) {
      return runState.call(s);
    }
  }

  @SuppressWarnings("unchecked")
  public static <M,S,A> StateT<M,S,A> monad(final Monad<Hkt2<statet, M, S>, A> monad) {
    return (StateT<M,S,A>) monad;
  }
  public static <M, S, A, B> StateT<M, S, F1<A, B>> applicative(
      final Applicative<Hkt2<statet, M, S>, ? extends F1<? super A, ? extends B>> app) {
    return (StateT<M, S, F1<A, B>>) app;
  }
}
