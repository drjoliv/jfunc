package drjoliv.fjava.control.bind;

import drjoliv.fjava.control.Bind;
import drjoliv.fjava.control.BindUnit;
import drjoliv.fjava.data.Either;
import static drjoliv.fjava.data.Either.*;
import static drjoliv.fjava.Numbers.*;
import drjoliv.fjava.data.FList;
import drjoliv.fjava.data.Maybe;

import static drjoliv.fjava.data.FList.*;

import java.math.BigInteger;
import java.util.HashMap;

import drjoliv.fjava.functions.F0;
import drjoliv.fjava.functions.F1;
import drjoliv.fjava.functions.F2;
import drjoliv.fjava.hkt.Witness;

public abstract class Trampoline<A> implements Bind<Trampoline.μ,A> {

  public static <A> Trampoline<A> zipWith(F2<A,A,A> fn, Trampoline<A> t1, Trampoline<A> t2) {
      return asTrampoline(Bind.liftM2(t1,t2,fn));
  }

  abstract <B> Trampoline<B> doBind(F1<? super A, Trampoline<B>> fn);

  @Override
  public BindUnit<μ> yield() {
    return Trampoline::unit;
  }

  @Override
  public <B> Trampoline<B> map(F1<? super A, B> fn) {
    return bind(value -> done(fn.call(value)));
  }

  @Override
  public <B> Trampoline<B> bind(F1<? super A, ? extends Bind<μ, B>> fn) {
    return new TrampolineBind<A,B>(this, fn.then(Trampoline::asTrampoline));
  }

  @Override
  public <C> Trampoline<C> semi(Bind<μ, C> mb) {
    return bind(a -> mb);
  }

  public static class μ implements drjoliv.fjava.hkt.Witness {}

  public static <A> Trampoline<A> unit(A a) {
    return Trampoline.done(a);
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

  private static class TrampolineBind<A,B> extends Trampoline<B> {

    private final Trampoline<A> t;
    private final F1<? super A, Trampoline<B>> fnNext;

    private TrampolineBind(Trampoline<A> t, F1<? super A, Trampoline<B>> fn) {
      this.t = t;
      this.fnNext = fn;
    }

    @Override
    public boolean isDone() {
      return false;
    }

    @Override
    public Trampoline<B> step() {
      return t.doBind(fnNext);
    }

    @Override
    protected B get() {
      throw new UnsupportedOperationException();
    }

    @Override
    <C> Trampoline<C> doBind(F1<? super B, Trampoline<C>> fn) {
      return t.bind(a -> fnNext.call(a).bind(fn));
    }
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
    <B> Trampoline<B> doBind(F1<? super A, Trampoline<B>> fn) {
      return next.call().doBind(fn);
    }
  }

  public static <A> Trampoline<A> done$(F0<A> fn) {
    return new Done<>(fn);
  }

  public static <A> Trampoline<A> done(A a) {
    return done$(() -> a);
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
    <B> Trampoline<B> doBind(F1<? super A, Trampoline<B>> fn) {
      return fn.call(result.call());
    }
  }


  public static <A> Trampoline<A> more(F0<Trampoline<A>> more) {
    return new More<A>(more);
  }
}
