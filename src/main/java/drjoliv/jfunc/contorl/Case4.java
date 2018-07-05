package drjoliv.jfunc.contorl;

import drjoliv.jfunc.function.F1;

/**
 * Case4 represents the coproduct of a type {@code Base} that has four subclasses {@code A, B, C, D}.
 * @author Desonte 'djoliv' Jolivet : drjoliv@gmail.com
 */
public interface Case4<Base, A extends Base, B extends Base, C extends Base, D extends Base> {

  /**
   * Applies the appropriate function to this instance of {@code Base} returning {@code E}.
   * @param f1 a function to apply if this is type {@code A}.
   * @param f2 a function to apply if this is type {@code B}.
   * @param f3 a function to apply if this is type {@code C).
   * @param f4 a function to apply if this is type {@code D).
   * @return E
   */
  public <E> E match(F1<A, E> f1, F1<B, E> f2, F1<C, E> f3, F1<D, E> f4);
}
