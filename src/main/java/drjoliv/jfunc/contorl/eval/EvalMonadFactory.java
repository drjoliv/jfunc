package drjoliv.jfunc.contorl.eval;

import drjoliv.jfunc.applicative.Applicative;
import drjoliv.jfunc.contorl.eval.Eval.μ;
import drjoliv.jfunc.monad.Monad;
import drjoliv.jfunc.monad.MonadFactory;

public class EvalMonadFactory implements MonadFactory<Eval.μ>{

  private static final EvalMonadFactory INSTANCE = new EvalMonadFactory();

  private EvalMonadFactory(){}

  public static EvalMonadFactory instance() {
    return INSTANCE;
  }

  @Override
  public <A> Monad<μ, A> unit(A a) {
    return Eval.now(a);
  }

  @Override
  public <A> Applicative<μ, A> pure(A a) {
    return Eval.now(a);
  }
}
