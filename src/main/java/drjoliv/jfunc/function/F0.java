package drjoliv.jfunc.function;

import java.util.function.Supplier;

import drjoliv.jfunc.functor.Functor;

/**
 * A computation that supplies a value.
 * @author Deaonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
@FunctionalInterface
public interface F0<A> extends Supplier<A>, Functor<F0.μ,A> {

  /**
  * The witness type of {@code F0}.
  */
  public class μ {private μ(){}}

  /**
   * Returns a result.
   * @return a result.
   */
  public A call();

  @Override
  public default <B> F0<B> map(F1<? super A, ? extends B> fn) {
    return F0Map.doMap(this,fn);
  }

  @Override
  public default A get() {
    return call();
  }
}
