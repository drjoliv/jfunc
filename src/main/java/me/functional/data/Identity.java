package me.functional.data;

import java.util.function.Function;

import me.functional.hkt.Hkt;
import me.functional.hkt.Witness;

public final class Identity<E> implements Monad<Identity.μ,E>, Hkt<Identity.μ,E> {

  public static <E> Identity<E> of(E e) {
    return new Identity<E>(e);
  }

  public static <E> Identity<E> narrow(Monad<Identity.μ,E> e) {
    return (Identity<E>) e;
  }

  @Override
  public <B> Identity<B> mBind(Function<E, ? extends Monad<Identity.μ, B>> fn) {
    return narrow(fn.apply(e));
  }

  @Override
  public <B> Identity<B> semi(Monad<Identity.μ, B> mIdentity) {
    return mBind(e -> mIdentity);
  }

  @Override
  public <B> Monad<Identity.μ, B> mUnit(B b) {
    return of(b);
  }

  public E value() {
    return e;
  }

  public static class μ implements Witness{}

  private final E e;

  private Identity(E e) {
    this.e = e;
  }

  public <A> Identity<A> fmap(Function<E,A> fn) {
    return new Identity<A>(fn.apply(e));
  }
}
