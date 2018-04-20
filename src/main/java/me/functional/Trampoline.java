package me.functional;

import java.util.function.BiFunction;

import me.functional.Trampoline.μ;
import me.functional.functions.F0;
import me.functional.functions.F1;
import me.functional.hkt.Witness;
import me.functional.type.Bind;
import me.functional.type.BindUnit;

public abstract class Trampoline<A> implements Bind<Trampoline.μ,A> {

  @Override
  public BindUnit<μ> yield() {
    return Trampoline::unit;
  }

  @Override
  public abstract <B> Trampoline<B> fmap(F1<? super A, B> fn);

  @Override
  public abstract <B> Trampoline<B> mBind(F1<? super A, ? extends Bind<μ, B>> fn);

  @Override
  public abstract <B> Trampoline<B> semi(Bind<μ, B> mb);

  public static class μ implements Witness {}

  public static <A> Trampoline<A> unit(A a) {
    return Trampoline.done(() -> a);
  }

  private Trampoline(){}

  public A result() {
    Trampoline<A> trampoline = this;
    while(trampoline.isDone() == false) {
     trampoline = trampoline.step();
    }
    return trampoline.get();
  }

  public abstract boolean isDone();

  protected abstract A get();

  public abstract Trampoline<A> step();

  public static <B> Trampoline<B> asTrampoline(Bind<Trampoline.μ,B> monad) {
    return (Trampoline<B>) monad;
  }

  private static class More<A> extends Trampoline<A> {

    private final F0<Trampoline<A>> next;

    private More(F0<Trampoline<A>> next) {
      this.next = next;
    }

    @Override
    public boolean isDone() {
      return false;
    }

    @Override
    public Trampoline<A> step() {
      return next.call();
    }

    @Override
    protected A get() {
      throw new UnsupportedOperationException();
    }

    @Override
    public <B> Trampoline<B> mBind(F1<? super A, ? extends Bind<μ, B>> fn) {
      return more(() -> asTrampoline(next.call().mBind(fn)));
    }

    @Override
    public <B> Trampoline<B> semi(Bind<μ, B> mb) {
      return mBind(a -> mb);
    }

    @Override
    public <B> Trampoline<B> fmap(F1<? super A, B> fn) {
      return more(() -> next.call().fmap(fn));
    }

  }

  public static <A> Trampoline<A> done(F0<A> fn) {
    return new Done<>(fn);
  }

  private static class Done<A> extends Trampoline<A> {

    private final F0<A> result;

    private Done(F0<A> result) {
      this.result = result;
    }

    @Override
    public boolean isDone() {
      return true;
    }

    @Override
    public Trampoline<A> step() {
      return this;
    }

    @Override
    protected A get() {
      return result.call();
    }

    @Override
    public <B> Trampoline<B> mBind(F1<? super A, ? extends Bind<μ, B>> fn) {
      return more(() -> asTrampoline(fn.call(result.get())));
    }

    @Override
    public <B> Trampoline<B> semi(Bind<μ, B> mb) {
      return mBind(a -> mb);
    }

    @Override
    public <B> Trampoline<B> fmap(F1<? super A, B> fn) {
      return more(() -> done(() -> fn.call(result.call())));
    }

  }


  public static <A> Trampoline<A> more(F0<Trampoline<A>> more) {
    return new More<A>(more);
  }

}
