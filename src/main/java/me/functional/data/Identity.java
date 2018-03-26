package me.functional.data;

import java.util.function.Function;

import me.functional.hkt.Hkt;
import me.functional.hkt.Witness;
import me.functional.type.Monad;
import me.functional.type.MonadUnit;

/**
 *
 *
 * @author
 */
public final class Identity<E> implements Monad<Identity.μ,E>, Hkt<Identity.μ,E> {

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
  public <B> Identity<B> mBind(Function<? super E, ? extends Monad<Identity.μ, B>> fn) {
    return asIdentity(fn.apply(e));
  }

  @Override
  public <B> Identity<B> semi(Monad<Identity.μ, B> mIdentity) {
    return mBind(e -> mIdentity);
  }


  @Override
  public MonadUnit<μ> yield() {
    return monadUnit;
  }

  @Override
  public <A> Identity<A> fmap(Function<? super E,A> fn) {
    return new Identity<A>(fn.apply(e));
  }

  public E value() {
    return e;
  }

  public static MonadUnit<Identity.μ> monadUnit = new MonadUnit<Identity.μ>(){
    @Override
    public <A> Monad<μ, A> unit(A a) {
      return id(a);
    }
  };

  public static <A> Identity<A> asIdentity(Monad<Identity.μ, A> monad) {
    return (Identity<A>) monad;
  }
}
