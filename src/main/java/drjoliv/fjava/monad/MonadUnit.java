package drjoliv.fjava.monad;

import drjoliv.fjava.hkt.Witness;

/**
 * A strategy for creating a monad.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public interface MonadUnit<M extends Witness> {

  /**
   * Returns a monad containing the agiven argument.
   * @param a an argument.
   * @return a monad contained the given argument.
   */
  public <A> Monad<M,A> unit(A a);
}
