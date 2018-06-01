package drjoliv.fjava.control;

import drjoliv.fjava.hkt.Witness;

public interface BindUnit<M extends Witness> {
  public <A> Bind<M,A> unit(A a);
}
