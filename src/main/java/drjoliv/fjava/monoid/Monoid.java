package drjoliv.fjava.monoid;

import drjoliv.fjava.functions.F2;

/**
 * An associative operation that has an identity element.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public interface Monoid<W> {

  /**
   * Returns the identity element of this monoid.
   * @return the identity element of this monoid.
   */
  public W mempty();

  /**
   * Combines two arugments.
   * @param w1 an argumnent.
   * @param w2 an argumnent.
   * @return a value obtained from combining the two arguments.
   */
  public W mappend(W w1, W w2);

  /**
   * Creates a monoid.
   * @param mappend An assoicative operation.
   * @param mempty The identity element.
   * @return a monoid.
   */
  public static <W> Monoid<W> monoid(F2<W,W,W> mappend, W mempty) {
    return new Monoid<W>() {

      @Override
      public W mappend(W w1, W w2) {
        return mappend.call(w1,w2);
      }

      @Override
      public W mempty() {
        return mempty;
      }
    };
  }
}
