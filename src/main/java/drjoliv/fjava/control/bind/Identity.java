package drjoliv.fjava.control.bind;

import drjoliv.fjava.control.Bind;
import drjoliv.fjava.control.BindUnit;
import drjoliv.fjava.functions.F1;
import drjoliv.fjava.hkt.Hkt;
import drjoliv.fjava.hkt.Witness;

/**
 * A computational context, it simply applies the binded function with no further effect.
 * Idenetity is used within monad transforms, to emmbed a monad that has no extra effects.
 *
 * @author Desonte 'drjoliv' Jolivet
 */
public final class Identity<E> implements Bind<Identity.μ,E>, Hkt<Identity.μ,E> {

  public static class μ implements Witness{}

  private final E e;

  private Identity(E e) {
    this.e = e;
  }

  /**
   * Constructs an identity from a given element.
   *
   * @param e the element to be wrapped into an identity.
   * @return the identity contianing {@code e}.
   */
  public static <E> Identity<E> id(E e) {
    return new Identity<E>(e);
  }

  @Override
  public <B> Identity<B> bind(F1<? super E, ? extends Bind<Identity.μ, B>> fn) {
    return asIdentity(fn.call(e));
  }

  @Override
  public <B> Identity<B> semi(Bind<Identity.μ, B> mIdentity) {
    return bind(e -> mIdentity);
  }


  @Override
  public BindUnit<μ> yield() {
    return Identity::id;
  }

  @Override
  public <A> Identity<A> map(F1<? super E, A> fn) {
    return new Identity<A>(fn.call(e));
  }

  /**
   * Returns the value contained within this identity.
   * @return the value contained within this identity.
   */
  public E value() {
    return e;
  }

  /**
   * Transforms a bind into an identity.
   * @param bind the bind that will be narrowed into an identity.
   * @return an identity.
   */
  public static <A> Identity<A> asIdentity(Bind<Identity.μ, A> bind) {
    return (Identity<A>) bind;
  }

  /**
   * Transforms a hkt into an identity.
   * @param monad the hkt that will be narrowed into an identity.
   * @return an identity.
   */
  public static <A> Identity<A> asIdentity(Hkt<Identity.μ, A> monad) {
    return (Identity<A>) monad;
  }

}
