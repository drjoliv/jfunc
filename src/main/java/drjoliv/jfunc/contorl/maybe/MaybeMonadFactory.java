package drjoliv.jfunc.contorl.maybe;

import drjoliv.jfunc.applicative.Applicative;
import drjoliv.jfunc.contorl.maybe.Maybe.μ;
import drjoliv.jfunc.monad.Monad;
import drjoliv.jfunc.monad.MonadFactory;

public class MaybeMonadFactory implements MonadFactory<Maybe.μ> {

  private static final MaybeMonadFactory INSTANCE = new MaybeMonadFactory();

  private MaybeMonadFactory(){}

  public static MaybeMonadFactory instance() {
    return INSTANCE;
  }

  @Override
  public <A> Monad<μ, A> unit(A a) {
    return Maybe.maybe(a);
  }

  @Override
  public <A> Applicative<μ, A> pure(A a) {
    return Maybe.maybe(a);
  }
}
