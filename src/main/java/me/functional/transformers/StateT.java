package me.functional.transformers;

import static me.functional.data.Pair.pair;

import java.util.function.Function;

import me.functional.data.Identity;
import me.functional.data.Pair;
import me.functional.data.Identity.μ;
import me.functional.hkt.Hkt2;
import me.functional.hkt.Witness;
import me.functional.type.Monad;

/**
 *
 *
 * @author drjoliv@gmail.com
 */
public class StateT<M extends Witness,S,A> implements Monad<Hkt2<StateT.μ,M,S>,A> {

  @Override
  public <B> StateT<M,S,B> mBind(final Function<? super A, ? extends Monad<Hkt2<μ, M, S>, B>> fn) {
    return new StateT<M,S,B>(s -> {
      Monad<M,Pair<S,A>> m = runState.apply(s);
      return m.mBind(p -> { 
        StateT<M,S,B> state = asStateT(fn.apply(p.snd));
        return state.runState.apply(p.fst);
      });
    });
  }

  @Override
  public <B> StateT<M,S,B> mUnit(final B b) {
    return new StateT<M,S,B>(s -> {
      return runState.apply(s).mUnit(pair(s,b));
    });
  }

  @Override
  public <B> StateT<M,S,B> semi(final Monad<Hkt2<μ, M, S>, B> mb) {
    return mBind(a -> mb);
  }

  @Override
  public <B> Monad<Hkt2<μ, M, S>, B> fmap(final Function<? super A, B> fn) {
     return new StateT<M,S,B>(s -> {
      return runState.apply(s).fmap(p -> pair(p.fst,fn.apply(p.snd)));
    });
  }

  public static class μ implements Witness{}

  private Function<S,Monad<M,Pair<S,A>>> runState;

  private StateT(final Function<S,Monad<M,Pair<S,A>>> runState) {
    this.runState = runState;
  }



  public StateT<M,S,S> getState() {
    return new StateT<M,S,S>(s -> runState.apply(s).mUnit(pair(s,s)));
  }

  public Monad<M,Pair<S,A>> evalState(S s) {
    return runState.apply(s);
  }

  public static final class State<S,A> extends StateT<Identity.μ,S,A> {
    private State(final Function<S,Monad<Identity.μ,Pair<S,A>>> runState) {
      super(runState);
    }

    public A execute(final S s) {
      return evalState(s).value().snd;
    }

    public S executeState(final S s) {
      return evalState(s).value().fst;
    }

    public Identity<Pair<S,A>> evalState(final S s) {
      return (Identity<Pair<S,A>>)super.evalState(s);
    }

    @Override
    public <B> State<S,B> mBind(
        Function<? super A, ? extends Monad<Hkt2<μ, me.functional.data.Identity.μ, S>, B>> fn) {
      return new State<S,B>(asStateT(super.mBind(fn)).runState);
    }

    @Override
    public <B> State<S,B> mUnit(B b) {
      return new State<S,B>(asStateT(super.mUnit(b)).runState);
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

  public static <S> State<S,S> get() {
    return new State<S, S>(s -> Identity.of(pair(s, s)));
  }

  public static <S> State<S, S> put(S s) {
    return new State<S, S>(state -> Identity.of(pair(s, s)));
  }

  /**
   *
   *
   * @param runState
   * @return
   */
  public static <M extends Witness,S,A> StateT<M,S,A> stateT(final Function<S,Monad<M,Pair<S,A>>> runState) {
    return new StateT<M,S,A>(runState);
  }

  public static <S,A> State<S,A> state(final Function<S,Pair<S,A>> runState) {
    return new State<S,A>(runState.andThen(p -> Identity.of(p)));
  }

  public static <M extends Witness,S,A> StateT<M,S,A> asStateT(final Monad<Hkt2<μ, M, S>, A> monad) {
    return (StateT<M,S,A>) monad;
  }

  public static <S,A> State<S,A> asState(final Monad<Hkt2<μ, Identity.μ, S>, A> monad) {
    if(monad instanceof StateT)
      return new State<S,A>(asStateT(monad).runState);
    else
      return (State<S,A>) monad;
  }

  public static <S, A> State<S, A> unit(A a) {
    return new State<S, A>(s -> Identity.of(pair(s, a)));
  }
}
