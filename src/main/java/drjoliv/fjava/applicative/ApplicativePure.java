package drjoliv.fjava.applicative;

import drjoliv.fjava.hkt.Witness;

/**
 * A strategy for lifting values into an applicative context.
 * @author Desonte 'drjoilv' Jolivet : drjoliv@gmail.com
 */
public interface ApplicativePure<M extends Witness> {

  /**
   * Returns an applicative containing the argument.
   * @param a the value to lift into an applicative.
   * @return an applicative containing the argumentn.
   */
  public <A> Applicative<M,A> pure(A a);
}
