package drjoliv.jfunc.monad;

import drjoliv.jfunc.applicative.Applicative;
import drjoliv.jfunc.monad.Identity.μ;

public class IdentityMonadFactory implements MonadFactory<Identity.μ>{

  private final static IdentityMonadFactory INSTANCE = new IdentityMonadFactory();

  private IdentityMonadFactory(){}

  public static IdentityMonadFactory instance() {
    return INSTANCE;
  }

  @Override
  public <A> Monad<μ, A> unit(A a) {
    return Identity.id(a);
  }

  @Override
  public <A> Applicative<μ, A> pure(A a) {
    return Identity.id(a);
  }
}
