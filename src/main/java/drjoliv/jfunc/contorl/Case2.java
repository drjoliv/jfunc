package drjoliv.jfunc.contorl;

import drjoliv.jfunc.function.F1;

/**
 * Case2 represents the coproduct of a type {@code Base} that has two subclasses {@code A,B}.
 * @author Desonte 'djoliv' Jolivet : drjoliv@gmail.com
 */
public interface Case2<Base,A extends Base,B extends Base> {

  /**
   * Applies the appropriate function to this instance of {@code Base} returning {@code A}.
   * @param f1 a function to apply if this is type {@code A}.
   * @param f2 a function to apply if this is type {@code B}.
   * @return C
   */
  public <C> C match(F1<A,C> f1, F1<B,C> f2);
}
