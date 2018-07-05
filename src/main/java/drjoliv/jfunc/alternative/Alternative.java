package drjoliv.jfunc.alternative;

import drjoliv.jfunc.applicative.Applicative;

/**
 * A monoid for applicative functors.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public interface Alternative<M, A, Fa extends Applicative<M,A>> {

  /**
   * Returns the identity of this applicative.
   * @return the identity of this applicative.
   */
  public  Fa empty();

  /**
   * An associative binary function.
   * @param a2 an applicative functor.
   * @param a2 an applicative functor.
   * @return an applicative functor.
   */
  public Fa alt( Fa a1, Fa a2);
}
