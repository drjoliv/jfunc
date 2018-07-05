package drjoliv.jfunc.contorl;

import drjoliv.jfunc.function.F1;

/**
 * Case3 represents the coproduct of a type {@code Base} that has three subclasses {@code A, B, C}.
 * @author Desonte 'djoliv' Jolivet : drjoliv@gmail.com
 */
public interface Case3<Base, A extends Base, B extends Base, C extends Base> {

  /**
   * Applies the appropriate function to this instance of {@code Base} returning {@code A}.
   * @param f1 a function to apply if this is type {@code A}.
   * @param f2 a function to apply if this is type {@code B}.
   * @param f3 a function to apply if this is type {@code C).
   * @return D
   */
  public <D> D match(F1<A, D> f1, F1<B, D> f2, F1<C, D> f3);
}
