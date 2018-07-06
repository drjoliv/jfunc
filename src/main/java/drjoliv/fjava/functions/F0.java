package drjoliv.fjava.functions;

import java.util.function.Consumer;
import java.util.function.Supplier;

import drjoliv.fjava.adt.FList;
import drjoliv.fjava.functor.Functor;
import drjoliv.fjava.hkt.Witness;

/**
 * A computation that supplies a value.
 * @author Deaonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
@FunctionalInterface
public interface F0<A> extends Supplier<A>, Functor<F0.μ,A> {

  /**
  * The witness type of {@code F0}.
  */
  public class μ implements Witness{private μ(){}}

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
