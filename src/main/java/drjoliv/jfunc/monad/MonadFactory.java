package drjoliv.jfunc.monad;

import drjoliv.jfunc.applicative.ApplicativeFactory;

/**
 * A strategy for creating a monad.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public interface MonadFactory<M> extends ApplicativeFactory<M> {

  /**
   * Returns a monad containing the agiven argument.
   * @param a an argument.
   * @return a monad contained the given argument.
   */
  public <A> Monad<M,A> unit(A a);
}
