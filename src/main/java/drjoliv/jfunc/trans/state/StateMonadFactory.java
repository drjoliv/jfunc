package drjoliv.jfunc.trans.state;

import drjoliv.jfunc.data.Unit;
import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.hlist.T2;
import drjoliv.jfunc.monad.Identity;
import drjoliv.jfunc.monad.Identity.μ;
import drjoliv.jfunc.monad.IdentityMonadFactory;
import drjoliv.jfunc.monad.Monad;
import drjoliv.jfunc.monad.MonadFactory;
import drjoliv.jfunc.trans.state.State.StateImpl;

public class StateMonadFactory<S> extends StateTMonadFactory<Identity.μ, S> {

  private static final StateMonadFactory INSTANCE = new StateMonadFactory();

  public static <S> StateMonadFactory<S> instance() {
    return INSTANCE;
  }

  StateMonadFactory() {
    super(IdentityMonadFactory.instance());
  }

  @Override
  public <A> State<S, A> pure(A a) {
    return new StateImpl<>(super.pure(a));
  }

  @Override
  public <A> State<S, A> unit(A a) {
    return new StateImpl<>(super.unit(a));
  }

  @Override
  public State<S, S> get() {
    return new StateImpl<>(super.get());
  }

  @Override
  public <A> State<S, A> gets(F1<S, A> fn) {
    return new StateImpl<>(super.gets(fn));
  }

  @Override
  public <A> State<S, A> lift(Monad<μ, A> ma) {
    return new StateImpl<>(super.lift(ma));
  }

  @Override
  public State<S, Unit> modify(F1<S, S> fn) {
    return new StateImpl<>(super.modify(fn));
  }

  @Override
  public <A> State<S, A> of(F1<S, Monad<μ, T2<S, A>>> fn) {
    return new StateImpl<>(super.of(fn));
  }

  @Override
  public State<S, Unit> put(S s) {
    return new StateImpl<>(super.put(s));
  }
}
