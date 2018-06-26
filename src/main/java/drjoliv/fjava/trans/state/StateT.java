package drjoliv.fjava.trans.state;

import drjoliv.fjava.functions.F1;
import static drjoliv.fjava.functions.F1.*;
import static drjoliv.fjava.hlist.T2.t2;

import drjoliv.fjava.adt.Either;
import drjoliv.fjava.applicative.Applicative;
import drjoliv.fjava.applicative.ApplicativePure;
import drjoliv.fjava.functions.F2;
import drjoliv.fjava.hkt.Hkt2;
import drjoliv.fjava.hkt.Witness;
import drjoliv.fjava.hlist.T2;
import drjoliv.fjava.monad.Monad;
import drjoliv.fjava.monad.MonadUnit;
import drjoliv.fjava.trans.state.StateT.μ;
import drjoliv.fjava.monad.Identity;

/**
 * The StateT monad.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public abstract class StateT<M extends Witness,S,A> implements Monad<Hkt2<StateT.μ,M,S>,A> {

  /**
   * The Witness type of {@code StateT}.
   */
  public static class μ implements Witness {private μ(){}}

  private final F1<S, Monad<M, T2<S, A>>> runState;

  private final MonadUnit<M> mUnit;

  private StateT(final F1<S, Monad<M, T2<S, A>>> runState, final MonadUnit<M> mUnit) {
    this.runState = runState;
    this.mUnit = mUnit;
  }

  @Override
  public abstract <B> StateT<M, S, B> map(final F1<? super A, ? extends B> fn);

  @Override
  public abstract <B> StateT<M, S, B> apply(
      Applicative<Hkt2<μ, M, S>, ? extends F1<? super A, ? extends B>> applicative);

  @Override
  public ApplicativePure<Hkt2<μ, M, S>> pure() {
    return new ApplicativePure<Hkt2<μ, M, S>>() {
      @Override
      public <B> Applicative<Hkt2<μ, M, S>, B> pure(B b) {
        return new StateTImpl<>(s -> mUnit(t2(s, b)), mUnit());
      }
    };
  }

  @Override
  public abstract <B> StateT<M,S,B> bind(final F1<? super A, ? extends Monad<Hkt2<μ, M, S>, B>> fn);

  @Override
  public abstract <B> StateT<M,S,B> semi(final Monad<Hkt2<μ, M, S>, B> mb);

  @Override
  public MonadUnit<Hkt2<drjoliv.fjava.trans.state.StateT.μ, M, S>> yield() {
    return unit(mUnit());
  }

  /**
  * @return the mUnit
  */
  public MonadUnit<M> mUnit() {
    return mUnit;
  }

  /**
   *
   *
   * @param s
   * @return
   */
   public Monad<M,T2<S,A>> runState(S s) {
    return runState.call(s);
   }

  /**
   *@return the runState
   */
  public F1<S, Monad<M, T2<S, A>>> runState() {
    return runState;
  }

  <C> Monad<M,C> mUnit(C c) {
    return mUnit().unit(c);
  }


  private static class StateTImpl<M extends Witness, S, A> extends StateT<M, S, A>{

    private StateTImpl(final F1<S,Monad<M,T2<S,A>>> runState, final MonadUnit<M> mUnit){
     super(runState, mUnit);
    }

    @Override
    public <B> StateT<M,S,B> map(final F1<? super A, ? extends B> fn) {
       return new StateTImpl<M,S,B>(s -> {
        return runState(s).map(p -> p.map2(fn));
      },mUnit());
    }

    @Override
    public <B> StateT<M,S,B> apply(Applicative<Hkt2<μ, M, S>, ? extends F1<? super A, ? extends B>> applicative) {
      return new StateTImpl<>(s -> {
       Monad<M,T2<S,A>> mv = runState(s);
       return mv.bind(t -> {
          Monad<M,T2<S,F1<? super A, B>>> mf = ((StateT<M, S, F1<? super A, B>>)applicative).runState(s);
          return mf.map( t2 -> t2.map2( fn -> fn.call(t._2()))); 
       });
      }, mUnit());
    }

    @Override
    public <B> StateT<M,S,B> bind(final F1<? super A, ? extends Monad<Hkt2<μ, M, S>, B>> fn) {
      return new StateTImpl<M,S,B>(s -> {
        return Monad.For(runState(s), t -> go(fn, t._2(), t._1()));
      }, mUnit());
    }

    private static <M extends Witness,S,A,B> Monad<M,T2<S,B>> go(final F1<? super A, ? extends Monad<Hkt2<μ, M, S>, B>> fn, A a, S s) {
      return monad(fn.call(a)).runState(s);
    }

    @Override
    public <B> StateT<M,S,B> semi(final Monad<Hkt2<μ, M, S>, B> mb) {
      return bind(a -> mb);
    }
  }

  public static final class State<S,A> extends StateT<Identity.μ,S,A> {
    private State(final F1<S,Monad<Identity.μ,T2<S,A>>> runState) {
      super(runState, Identity::id);
    }

    public A execute(final S s) {
      return runState(s).value()._2();
    }

    public S executeState(final S s) {
      return runState(s).value()._1();
    }

    @Override
    public <B> State<S,B> map(F1<? super A, ? extends B> fn) {
      return new State<S,B>(runState()
          .map(ma -> ma.map(t -> t.map2(fn)))
        );
    }

    @Override
    public <B> State<S,B> apply(Applicative<Hkt2<μ, Identity.μ, S>, ? extends F1<? super A, ? extends B>> app) {
      return new State<>(s -> {
       Monad<Identity.μ,T2<S,A>> mv = runState(s);
       return mv.bind(t -> {
          Monad<Identity.μ,T2<S,F1<A, B>>> mf = applicative(app).runState(s);
          return mf.map( t2 -> t2.map2( fn -> fn.call(t._2()))); 
       });
      });
    }

    @Override
    public Identity<T2<S,A>> runState(final S s) {
      return (Identity<T2<S,A>>)super.runState(s);
    }

    @Override
    public <B> State<S,B> bind(
        F1<? super A, ? extends Monad<Hkt2<μ, drjoliv.fjava.monad.Identity.μ, S>, B>> fn) {
          return new State<S,B>(s -> {
            return Monad.For(runState(s), t -> go(fn, t._2(), t._1()));
          });
    }

    @Override
    public <B> State<S,B> semi(Monad<Hkt2<μ, drjoliv.fjava.monad.Identity.μ, S>, B> mb) {
      return bind(s -> mb);
    }

    private static <M extends Witness,S,A,B> Monad<M,T2<S,B>> go(final F1<? super A, ? extends Monad<Hkt2<μ, M, S>, B>> fn, A a, S s) {
      return monad(fn.call(a)).runState(s);
    }
  }


  /**
   *
   *
   * @param runState
   * @return
   */
  public static <M extends Witness,S,A> StateT<M,S,A> stateT(final F1<S,T2<S,A>> runState, MonadUnit<M> mUnit) {
    return new StateTImpl<M,S,A>(runState.then(p -> mUnit.unit(p)), mUnit);
  }

  public static <S,A> State<S,A> state(final F1<S,T2<S,A>> runState) {
    return new State<S,A>(runState.then(p -> Identity.id(p)));
  }

  @SuppressWarnings("unchecked")
  public static <M extends Witness,S,A> StateT<M,S,A> monad(final Monad<Hkt2<μ, M, S>, A> monad) {
    return (StateT<M,S,A>) monad;
  }

  @SuppressWarnings("unchecked")
  public static <M extends Witness,S,A,B> StateT<M,S,F1<A,B>> applicative(final Applicative<Hkt2<μ, M, S>, ? extends F1<? super A, ? extends B>> app) {
    return (StateT<M,S,F1<A,B>>) app;
  }

  public static <S,A> State<S,A> asState(final Monad<Hkt2<μ, Identity.μ, S>, A> monad) {
    if(monad instanceof StateT)
      return new State<S,A>(monad(monad).runState);
    else
      return (State<S, A>) monad;
  }

  public static <S> State<S,S> get() {
    return new State<S, S>(s -> Identity.id(t2(s, s)));
  }

  public static <S> State<S, S> put(S s) {
    return new State<S, S>(state -> Identity.id(t2(s, s)));
  }

  public static <M extends Witness, S> StateT<M,S,S> get(MonadUnit<M> mUnit) {
    return new StateTImpl<M, S, S>(s -> mUnit.unit(t2(s, s)), mUnit);
  }

  public static <M extends Witness, S> F2<MonadUnit<M>,S,StateT<M,S, S>> put() {
    return (mUnit, s) -> new StateTImpl<M, S, S>(state -> mUnit.unit((t2(s, s))), mUnit);
  }

  public static <S, A> State<S, A> ret(A a) {
    return new State<S, A>(s -> Identity.id(t2(s, a)));
  }

  public static <M extends Witness,S> MonadUnit<Hkt2<StateT.μ,M,S>> unit(MonadUnit<M> mUnit) {
    return new MonadUnit<Hkt2<StateT.μ,M,S>>() {
      @Override
      public <A> Monad<Hkt2<μ, M, S>, A> unit(A a) {
        return new StateTImpl<M, S, A>(s -> mUnit.unit(t2(s, a)), mUnit);
      }
    };
  }
}
