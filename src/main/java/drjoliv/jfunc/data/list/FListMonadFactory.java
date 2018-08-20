package drjoliv.jfunc.data.list;

import drjoliv.jfunc.applicative.Applicative;
import drjoliv.jfunc.data.list.FList.μ;
import drjoliv.jfunc.monad.MonadFactory;

public class FListMonadFactory implements MonadFactory<FList.μ> {

  private static final FListMonadFactory INSTANCE = new FListMonadFactory();

  public static FListMonadFactory instance() {
    return INSTANCE;
  }

  @Override
  public <A> FList<A> unit(A a) {
    return FList.flist(a);
  }

  @Override
  public <A> Applicative<μ, A> pure(A a) {
    return FList.flist(a);
  }
}
