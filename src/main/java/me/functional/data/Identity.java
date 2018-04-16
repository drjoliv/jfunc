package me.functional.data;

import java.util.function.Function;

import me.functional.functions.F1;
import me.functional.hkt.Hkt;
import me.functional.hkt.Witness;
import me.functional.type.Bind;
import me.functional.type.BindUnit;

/**
 *
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
   *
   *
   * @param e
   * @return
   */
  public static <E> Identity<E> id(E e) {
    return new Identity<E>(e);
  }

  @Override
  public <B> Identity<B> mBind(F1<? super E, ? extends Bind<Identity.μ, B>> fn) {
    return asIdentity(fn.call(e));
  }

  @Override
  public <B> Identity<B> semi(Bind<Identity.μ, B> mIdentity) {
    return mBind(e -> mIdentity);
  }


  @Override
  public BindUnit<μ> yield() {
    return Identity::id;
  }

  @Override
  public <A> Identity<A> fmap(F1<? super E, A> fn) {
    return new Identity<A>(fn.call(e));
  }

  public E value() {
    return e;
  }

  public static <A> Identity<A> asIdentity(Bind<Identity.μ, A> monad) {
    return (Identity<A>) monad;
  }
}
