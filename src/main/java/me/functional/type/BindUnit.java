package me.functional.type;

import me.functional.hkt.Witness;

public interface BindUnit<M extends Witness> {
  public <A> Bind<M,A> unit(A a);
}
