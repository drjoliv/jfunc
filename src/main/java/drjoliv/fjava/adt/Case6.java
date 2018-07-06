package drjoliv.fjava.adt;

import drjoliv.fjava.functions.F1;

/**
 * Case6 represents the coproduct of a type {@code Base} that has six subclasses {@code A, B, C, D, E, F}.
 * @author Desonte 'djoliv' Jolivet : drjoliv@gmail.com
 */
public interface Case6<Base, A extends Base, B extends Base
  , C extends Base, D extends Base, E extends Base, F extends Base> {

   /**
   * Applies the appropriate function to this instance of {@code Base} returning {@code G}.
   * @param f1 a function to apply if this is type {@code A}.
   * @param f2 a function to apply if this is type {@code B}.
   * @param f3 a function to apply if this is type {@code C).
   * @param f4 a function to apply if this is type {@code D).
   * @param f5 a function to apply if this is type {@code E).
   * @param f6 a function to apply if this is type {@code F).
   * @return G
   */
  public <G> G match(F1<? super A,G> f1, F1<? super B,G> f2, F1<? super C,G> f3, F1<? super D,G> f4, F1<? super E,G> f5, F1<? super F,G> f6);
  
}
