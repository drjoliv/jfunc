package drjoliv.jfunc.applicative;


/**
 * A strategy for lifting values into an applicative context.
 * @author Desonte 'drjoilv' Jolivet : drjoliv@gmail.com
 */
public interface ApplicativeFactory<M> {

  /**
   * Returns an applicative containing the argument.
   * @param a the value to lift into an applicative.
   * @return an applicative containing the argumentn.
   */
  public <A> Applicative<M,A> pure(A a);
}
