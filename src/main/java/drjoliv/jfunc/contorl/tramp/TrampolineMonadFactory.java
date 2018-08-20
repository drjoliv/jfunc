package drjoliv.jfunc.contorl.tramp;

import drjoliv.jfunc.applicative.Applicative;
import drjoliv.jfunc.contorl.tramp.Trampoline.μ;
import drjoliv.jfunc.monad.Monad;
import drjoliv.jfunc.monad.MonadFactory;

public class TrampolineMonadFactory implements MonadFactory<Trampoline.μ> {

  private static final TrampolineMonadFactory INSTANCE = new TrampolineMonadFactory();

  private TrampolineMonadFactory(){}

  public static MonadFactory<μ> instance() {
    return INSTANCE;
  }

  @Override
  public <A> Applicative<μ, A> pure(A a) {
    return Trampoline.done(a);
  }

  @Override
  public <A> Monad<μ, A> unit(A a) {
    return Trampoline.done(a);
  }
}
