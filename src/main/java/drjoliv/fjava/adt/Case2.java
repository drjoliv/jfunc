package drjoliv.fjava.adt;

import drjoliv.fjava.functions.F1;

public interface Case2<Base,A extends Base,B extends Base> {
  public <C> C match(F1<A,C> f1, F1<B,C> f2);
}
