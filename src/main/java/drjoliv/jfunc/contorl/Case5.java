package drjoliv.jfunc.contorl;

import drjoliv.jfunc.function.F1;

/**
 * Case5 represents the coproduct of a type {@code Base} that has five subclasses {@code A, B, C, D, E}.
 * @author Desonte 'djoliv' Jolivet : drjoliv@gmail.com
 */
public interface Case5<Base, A extends Base, B extends Base, C extends Base, D extends Base, E extends Base> {

  /**
   * Applies the appropriate function to this instance of {@code Base} returning {@code F}.
   * @param f1 a function to apply if this is type {@code A}.
   * @param f2 a function to apply if this is type {@code B}.
   * @param f3 a function to apply if this is type {@code C}.
   * @param f4 a function to apply if this is type {@code D}.
   * @param f5 a function to apply if this is type {@code E}.
   * @return F
   */
  public <F> F match(F1<A, F> f1, F1<B, F> f2, F1<C, F> f3, F1<D, F> f4, F1<E, F> f5);

}
