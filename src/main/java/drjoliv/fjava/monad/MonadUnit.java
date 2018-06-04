package drjoliv.fjava.monad;

import drjoliv.fjava.hkt.Witness;

public interface MonadUnit<M extends Witness> {
  public <A> Monad<M,A> unit(A a);
}
