package drjoliv.jfunc.contorl.trys;

import drjoliv.jfunc.applicative.Applicative;
import drjoliv.jfunc.contorl.trys.Try.μ;
import drjoliv.jfunc.monad.Monad;
import drjoliv.jfunc.monad.MonadFactory;

public class TryMonadFactory implements MonadFactory<Try.μ> {

  private static final TryMonadFactory INSTANCE = new TryMonadFactory();

  private TryMonadFactory(){}

  public static TryMonadFactory instance() {
    return INSTANCE;
  }

  @Override
  public <A> Monad<μ, A> unit(A a) {
    return Try.success(a);
  }

  @Override
  public <A> Applicative<μ, A> pure(A a) {
    return Try.success(a);
  }
}
