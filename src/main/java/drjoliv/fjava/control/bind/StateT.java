package me.functional.transformers;

import static me.functional.data.T2.t2;

import me.functional.data.Identity;
import me.functional.data.T2;
import me.functional.functions.F1;
import me.functional.functions.F2;
import me.functional.hkt.Hkt2;
import me.functional.hkt.Witness;
import me.functional.type.Bind;
import me.functional.type.BindUnit;

/**
 *
 *
 * @author drjoliv@gmail.com
 */
public class StateT<M extends Witness,S,A> implements Bind<Hkt2<StateT.μ,M,S>,A> {

  public static class μ implements Witness{}

  private final F1<S,Bind<M,T2<S,A>>> runState;
  private final BindUnit<M> mUnit;

  private StateT(final F1<S,Bind<M,T2<S,A>>> runState, final BindUnit<M> mUnit) {
    this.runState = runState;
    this.mUnit    = mUnit;
  }

  @Override
  public <B> StateT<M,S,B> bind(final F1<? super A, ? extends Bind<Hkt2<μ, M, S>, B>> fn) {
    return new StateT<M,S,B>(s -> {
      return runState.call(s)
        .bind(p -> {
        return asStateT(fn.call(p.snd)).runState.call(p.fst);
      });
    }, mUnit);
  }

  @Override
  public <B> StateT<M,S,B> semi(final Bind<Hkt2<μ, M, S>, B> mb) {
    return bind(a -> mb);
  }

  @Override
  public <B> Bind<Hkt2<μ, M, S>, B> map(final F1<? super A, B> fn) {
     return new StateT<M,S,B>(s -> {
      return runState.call(s).map(p -> t2(p.fst,fn.call(p.snd)));
    },mUnit);
  }

  @Override
  public BindUnit<Hkt2<me.functional.transformers.StateT.μ, M, S>> yield() {
    return unit(mUnit);
  }

  public StateT<M,S,S> getState() {
    return new StateT<M,S,S>(s -> mUnit.unit(t2(s,s)),mUnit);
  }

  public Bind<M,T2<S,A>> evalState(S s) {
    return runState.call(s);
  }

  public static final class State<S,A> extends StateT<Identity.μ,S,A> {
    private State(final F1<S,Bind<Identity.μ,T2<S,A>>> runState) {
      super(runState, Identity::id);
    }

    public A execute(final S s) {
      return evalState(s).value().snd;
    }

    public S executeState(final S s) {
      return evalState(s).value().fst;
    }

    @Override
    public Identity<T2<S,A>> evalState(final S s) {
      return (Identity<T2<S,A>>)super.evalState(s);
    }

    @Override
    public <B> State<S,B> bind(
        F1<? super A, ? extends Bind<Hkt2<μ, me.functional.data.Identity.μ, S>, B>> fn) {
      return new State<S,B>(asStateT(super.bind(fn)).runState);
    }

    @Override
    public <B> State<S,B> semi(Bind<Hkt2<μ, me.functional.data.Identity.μ, S>, B> mb) {
      return bind(s -> mb);
    }
    
    @Override
    public <B> State<S,B> map(F1<? super A, B> fn) {
      return new State<S,B>(asStateT(super.map(fn)).runState);
    }
  }


  /**
   *
   *
   * @param runState
   * @return
   */
  public static <M extends Witness,S,A> StateT<M,S,A> stateT(final F1<S,T2<S,A>> runState, BindUnit<M> mUnit) {
    return new StateT<M,S,A>(runState.then(p -> mUnit.unit(p)), mUnit);
  }

  public static <S,A> State<S,A> state(final F1<S,T2<S,A>> runState) {
    return new State<S,A>(runState.then(p -> Identity.id(p)));
  }

  @SuppressWarnings("unchecked")
  public static <M extends Witness,S,A> StateT<M,S,A> asStateT(final Bind<Hkt2<μ, M, S>, A> monad) {
    return (StateT<M,S,A>) monad;
  }

  public static <S,A> State<S,A> asState(final Bind<Hkt2<μ, Identity.μ, S>, A> monad) {
    if(monad instanceof StateT)
      return new State<S,A>(asStateT(monad).runState);
    else
      return (State<S, A>) monad;
  }

  public static <S> State<S,S> get() {
    return new State<S, S>(s -> Identity.id(t2(s, s)));
  }

  public static <S> State<S, S> put(S s) {
    return new State<S, S>(state -> Identity.id(t2(s, s)));
  }

  public static <M extends Witness, S> StateT<M,S,S> get(BindUnit<M> mUnit) {
    return new StateT<M, S, S>(s -> mUnit.unit(t2(s, s)), mUnit);
  }

  public static <M extends Witness, S> F2<BindUnit<M>,S,StateT<M,S, S>> put() {
    return (mUnit, s) -> new StateT<M, S, S>(state -> mUnit.unit((t2(s, s))), mUnit);
  }

  public static <S, A> State<S, A> unit(A a) {
    return new State<S, A>(s -> Identity.id(t2(s, a)));
  }

  public static <M extends Witness, S, A> F2<BindUnit<M>,A,StateT<M, S, A>> unit() {
    return (mUnit, a) -> new StateT<M, S, A>(s -> mUnit.unit((t2(s, a))), mUnit);
  }

  public static <M extends Witness,S> BindUnit<Hkt2<StateT.μ,M,S>> unit(BindUnit<M> mUnit) {
    return new BindUnit<Hkt2<StateT.μ,M,S>>() {
      @Override
      public <A> Bind<Hkt2<μ, M, S>, A> unit(A a) {
        return new StateT<M,S,A>(s -> mUnit.unit(t2(s,a)), mUnit);
      }
    };
  }

  public static <S> BindUnit<Hkt2<StateT.μ,Identity.μ,S>> stateUnit() {
    return new BindUnit<Hkt2<StateT.μ,Identity.μ,S>>() {
      @Override
      public <A> Bind<Hkt2<μ, me.functional.data.Identity.μ, S>, A> unit(A a) {
        return StateT.unit(a);
      }
    };
  }
}
