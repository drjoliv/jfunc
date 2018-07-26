package drjoliv.jfunc.contorl.either;

import drjoliv.jfunc.applicative.Applicative;
import drjoliv.jfunc.contorl.either.Either.RightProjection;
import drjoliv.jfunc.contorl.either.Either.RightProjection.μ;
import drjoliv.jfunc.hkt.Hkt;
import drjoliv.jfunc.monad.Monad;
import drjoliv.jfunc.monad.MonadFactory;

public class RightProjectionMonadFactory<L> implements MonadFactory<Hkt<RightProjection.μ,L>> {

  private static final RightProjectionMonadFactory INSTANCE = new RightProjectionMonadFactory();

  public static <L> RightProjectionMonadFactory<L> instance() {
    return INSTANCE;
  }

  @Override
  public <A> Monad<Hkt<μ, L>, A> unit(A a) {
    return RightProjection.rightProjection(a);
  }

  @Override
  public <A> Applicative<Hkt<μ, L>, A> pure(A a) {
    return RightProjection.rightProjection(a);
  }
}
