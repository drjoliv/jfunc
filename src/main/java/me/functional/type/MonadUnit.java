package me.functional.type;

import me.functional.hkt.Witness;

public interface MonadUnit<M extends Witness> {
  public <A> Monad<M,A> unit(A a);
}
