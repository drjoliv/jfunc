package me.functional.transformers;

import static me.functional.data.Pair.pair;

import java.util.function.Function;

import me.functional.data.Identity;
import me.functional.data.Pair;
import me.functional.functions.F2;
import me.functional.hkt.Hkt2;
import me.functional.hkt.Witness;
import me.functional.type.Monad;
import me.functional.type.MonadUnit;

/**
 *
 *
 * @author drjoliv@gmail.com
 */
public class StateT<M extends Witness,S,A> implements Monad<Hkt2<StateT.μ,M,S>,A> {

  public static class μ implements Witness{}

  private final Function<S,Monad<M,Pair<S,A>>> runState;
  private final MonadUnit<M> mUnit;

  private StateT(final Function<S,Monad<M,Pair<S,A>>> runState, final MonadUnit<M> mUnit) {
    this.runState = runState;
    this.mUnit    = mUnit;
  }

  @Override
  public <B> StateT<M,S,B> mBind(final Function<? super A, ? extends Monad<Hkt2<μ, M, S>, B>> fn) {
    return new StateT<M,S,B>(s -> {
      return runState.apply(s)
        .mBind(p -> {
        return asStateT(fn.apply(p.snd)).runState.apply(p.fst);
      });
    }, mUnit);
  }

  @Override
  public <B> StateT<M,S,B> semi(final Monad<Hkt2<μ, M, S>, B> mb) {
    return mBind(a -> mb);
  }

  @Override
  public <B> Monad<Hkt2<μ, M, S>, B> fmap(final Function<? super A, B> fn) {
     return new StateT<M,S,B>(s -> {
      return runState.apply(s).fmap(p -> pair(p.fst,fn.apply(p.snd)));
    },mUnit);
  }

  @Override
  public MonadUnit<Hkt2<me.functional.transformers.StateT.μ, M, S>> yield() {
    return unit(mUnit);
  }

  public StateT<M,S,S> getState() {
    return new StateT<M,S,S>(s -> mUnit.unit(pair(s,s)),mUnit);
  }

  public Monad<M,Pair<S,A>> evalState(S s) {
    return runState.apply(s);
  }

  public static final class State<S,A> extends StateT<Identity.μ,S,A> {
    private State(final Function<S,Monad<Identity.μ,Pair<S,A>>> runState) {
      super(runState, Identity.monadUnit);
    }

    public A execute(final S s) {
      return evalState(s).value().snd;
    }

    public S executeState(final S s) {
      return evalState(s).value().fst;
    }

    @Override
    public Identity<Pair<S,A>> evalState(final S s) {
      return (Identity<Pair<S,A>>)super.evalState(s);
    }

    @Override
    public <B> State<S,B> mBind(
        Function<? super A, ? extends Monad<Hkt2<μ, me.functional.data.Identity.μ, S>, B>> fn) {
      return new State<S,B>(asStateT(super.mBind(fn)).runState);
    }

    @Override
    public <B> State<S,B> semi(Monad<Hkt2<μ, me.functional.data.Identity.μ, S>, B> mb) {
      return mBind(s -> mb);
    }
    
    @Override
    public <B> State<S,B> fmap(Function<? super A, B> fn) {
      return new State<S,B>(asStateT(super.fmap(fn)).runState);
    }
  }


  /**
   *
   *
   * @param runState
   * @return
   */
  public static <M extends Witness,S,A> StateT<M,S,A> stateT(final Function<S,Pair<S,A>> runState, MonadUnit<M> mUnit) {
    return new StateT<M,S,A>(runState.andThen(p -> mUnit.unit(p)), mUnit);
  }

  public static <S,A> State<S,A> state(final Function<S,Pair<S,A>> runState) {
    return new State<S,A>(runState.andThen(p -> Identity.id(p)));
  }

  @SuppressWarnings("unchecked")
  public static <M extends Witness,S,A> StateT<M,S,A> asStateT(final Monad<Hkt2<μ, M, S>, A> monad) {
    return (StateT<M,S,A>) monad;
  }

  public static <S,A> State<S,A> asState(final Monad<Hkt2<μ, Identity.μ, S>, A> monad) {
    if(monad instanceof StateT)
      return new State<S,A>(asStateT(monad).runState);
    else
      return (State<S, A>) monad;
  }

  public static <S> State<S,S> get() {
    return new State<S, S>(s -> Identity.id(pair(s, s)));
  }

  public static <S> State<S, S> put(S s) {
    return new State<S, S>(state -> Identity.id(pair(s, s)));
  }

  public static <M extends Witness, S> StateT<M,S,S> get(MonadUnit<M> mUnit) {
    return new StateT<M, S, S>(s -> mUnit.unit(pair(s, s)), mUnit);
  }

  public static <M extends Witness, S> F2<MonadUnit<M>,S,StateT<M,S, S>> put() {
    return (mUnit, s) -> new StateT<M, S, S>(state -> mUnit.unit((pair(s, s))), mUnit);
  }

  public static <S, A> State<S, A> unit(A a) {
    return new State<S, A>(s -> Identity.id(pair(s, a)));
  }

  public static <M extends Witness, S, A> F2<MonadUnit<M>,A,StateT<M, S, A>> unit() {
    return (mUnit, a) -> new StateT<M, S, A>(s -> mUnit.unit((pair(s, a))), mUnit);
  }

  public static <M extends Witness,S> MonadUnit<Hkt2<StateT.μ,M,S>> unit(MonadUnit<M> mUnit) {
    return new MonadUnit<Hkt2<StateT.μ,M,S>>() {
      @Override
      public <A> Monad<Hkt2<μ, M, S>, A> unit(A a) {
        return new StateT<M,S,A>(s -> mUnit.unit(pair(s,a)), mUnit);
      }
    };
  }

  public static <S> MonadUnit<Hkt2<StateT.μ,Identity.μ,S>> stateUnit() {
    return new MonadUnit<Hkt2<StateT.μ,Identity.μ,S>>() {
      @Override
      public <A> Monad<Hkt2<μ, me.functional.data.Identity.μ, S>, A> unit(A a) {
        return StateT.unit(a);
      }
    };
  }
}
