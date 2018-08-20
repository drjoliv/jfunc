package drjoliv.jfunc.contorl.either;

import drjoliv.jfunc.applicative.Applicative;
import drjoliv.jfunc.contorl.either.Either.LeftProjection;
import drjoliv.jfunc.contorl.either.Either.LeftProjection.μ;
import drjoliv.jfunc.hkt.Hkt;
import drjoliv.jfunc.monad.Monad;
import drjoliv.jfunc.monad.MonadFactory;

public class LeftProjectionMonadFactory<R> implements MonadFactory<Hkt<LeftProjection.μ,R>>{

  private static final LeftProjectionMonadFactory INSTANCE = new LeftProjectionMonadFactory();

  public LeftProjectionMonadFactory(){}

  public static final <R> LeftProjectionMonadFactory<R> instance() {
    return INSTANCE;
  }

  @Override
  public <A> Monad<Hkt<μ, R>, A> unit(A a) {
    return LeftProjection.leftProjection(a);
  }

  @Override
  public <A> Applicative<Hkt<μ, R>, A> pure(A a) {
    return LeftProjection.leftProjection(a);
  }
}
