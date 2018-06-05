package drjoliv.fjava.adt;

import java.util.function.Function;

import drjoliv.fjava.adt.Maybe.μ;
import drjoliv.fjava.alternative.Alternative;
import drjoliv.fjava.functions.F0;
import drjoliv.fjava.functions.F1;
import drjoliv.fjava.functions.F2;
import drjoliv.fjava.hkt.Hkt;
import drjoliv.fjava.hkt.Hkt2;
import drjoliv.fjava.hkt.Witness;
import drjoliv.fjava.monad.Monad;
import drjoliv.fjava.monad.MonadUnit;

import static drjoliv.fjava.adt.Eval.*;
import static drjoliv.fjava.monad.Monad.*;

/**
 * Represents a value of something(Just) or a value of Nothing.
 *
 * @author drjoliv@gmail.com
 */
public abstract class Maybe<A> implements Hkt<Maybe.μ, A>, Monad<Maybe.μ, A>, 
       Alternative<Hkt<Maybe.μ, A>>, Case2<Maybe<A>,Maybe.None<A>,Maybe.Just<A>>{

  @Override
  public Maybe<A> alt(Alternative<Hkt<Maybe.μ, A>> alt) {
    return isSome() ? this : asMaybe(alt);
  }

  @Override
  public abstract <B> Maybe<B> map(F1<? super A, B> fn);


  public static interface μ extends Witness {}

  private Maybe() {}

  @Override
  public abstract <B> Maybe<B> bind(F1<? super A, ? extends Monad<μ, B>> fn);

  @Override
  public <B> Maybe<B> semi(Monad<μ, B> mb) {
    return bind(a -> mb);
  }

  @Override
  public MonadUnit<μ> yield() {
    return Maybe::maybe;
  }

  /**
   * Returns <code> a </code> if <code> this </code> maybe is <code> nothing </code>, otherwise it returns the value contained within just.
   * @param a the value return if this is nothing.
   * @return <code> a </code> if <code> this </code> maybe is <code> nothing </code>.
   */
  public A orSome(A a) {
    return isSome() ? toNull() : a;
  }

  /**
   * Returns the given argument if this is nothing, or this if this is just.
   * @param maybe the value returned if this is nothing.
   * @return the arugment if this is nothing, or this if this is just.
   */
  public Maybe<A> orElse(Maybe<A> maybe) {
    return isSome() ? this : maybe;
  }

  /**
   * True if <code> this </code> maybe contains a value and false otherwise.
   * @return True if <code> this </code> Maybe contains a value and false otherwise.
   */
  public abstract boolean isSome();

  /**
   * Returns the value of this maybe if there is one or null otherwise.
   * @return Returns the value of this maybe if there is one or null otherwise.
   */
  public abstract A toNull();

 /**
  * Returns an instance of Maybe that contains nothing.
  *
  * @return an intstance of Maybe that contains Nothing.
  */
  @SuppressWarnings("unchecked")
  public static <B> Maybe<B> nothing() {
    return (Maybe<B>) None.instance;
  }

  /**
   * Narrows thw argument into a maybe.
   * @param monad the instance of bind that will be narrowed.
   * @param <B> the type of the contents of the narrowed bind.
   * @return a myabe
   */
  public static <B> Maybe<B> asMaybe(Monad<Maybe.μ, B> monad) {
    return (Maybe<B>) monad;
  }

  /**
   * 
   * Narrows thw argument into a maybe.
   * @param alt the alternaive that will be casted into a maybe.
   * @param <B> the type of the contents of the narrowed alternative.
   * @return a maybe.
   */
  public static <B> Maybe<B> asMaybe(Alternative<Hkt<Maybe.μ, B>> alt) {
    return (Maybe<B>) alt;
  }

  /**
   * Narrows thw argument into a maybe.
   * @param hkt the higher kinded type that will be casted to a maybe. 
   * @param <B> the type of the contents of the narrowed hkt.
   * @return a myabe.
   */
  public static <B> Maybe<B> asMaybe(Hkt<Maybe.μ, B> hkt) {
    return (Maybe<B>) hkt;
  }

 /**
  * Constructs a maybe from the given argument.
  *
  * @param a the value to be wrappped into a myabe.
  * @param <A> the type of the contents of the created maybe.
  * @return the maybe value containing gthe argument.
  */
  public static <A> Maybe<A> maybe(A a) {
      return maybe$(() -> a);
  }

 /**
  * Constructs a maybe from the value returned by the supplier.
  *
  * @param supplier a supplier that returns a value that is used to construct a maybe.
  * @param <A> the type of the contents of the created maybe.
  * @return the maybe created from the argument.
  */
  public static <A> Maybe<A> maybe$(F0<A> supplier) {
      return new Just<A>(later(supplier));
  }

 /**
  *
  *
  * @param eval the comutation whose result will be inserted in a maybe.
  * @param <A> the type of the contents of the created maybe.
  * @return a myabe conatining the result of the argument.
  */
  public static <A> Maybe<A> maybe$(Eval<A> eval) {
      return new Just<A>(eval);
  }

  /**
   * The instance of maybe that does not contain a value.
   */
  public static class None<A> extends Maybe<A> {
    private static None<?> instance = new None<>();

    private None() {}

    @Override
    public boolean isSome() {
      return false;
    }

    @Override
    public <B> Maybe<B> map(F1<? super A, B> fn) {
      return nothing();
    }

    @Override
    public <B> Maybe<B> bind(F1<? super A, ? extends Monad<μ, B>> fn) {
      return nothing();
    }

    @Override
    public A toNull() {
      return null;
    }

    @Override
    public <B> B match(F1<None<A>, B> left, F1<Just<A>, B> right) {
      return left.call(this);
    }
  }

  /**
   * The instance of maybe that contains a value.
   */
  public static class Just<A> extends Maybe<A> {

    private Eval<A> value;

    private Just(Eval<A> value) {
      this.value = value;
    }

    @Override
    public boolean isSome() {
      return true;
    }

    @Override
    public A toNull() {
        return value.value();
    }

    /**
     * Returns the value contained within this maybe.
     * @return the value contained within this maybe.
     */
      public A value() {
        return value.value();
    }

    @Override
    public <B> Maybe<B> map(F1<? super A, B> fn) {
      return new Just<B>(value.map(fn));
    }

    @Override
    public <B> Maybe<B> bind(F1<? super A, ? extends Monad<μ, B>> fn) {
      return asMaybe(Monad.join(map(fn.then(Maybe::asMaybe))));
    }

    @Override
    public <B> B match(F1<None<A>, B> left, F1<Just<A>, B> right) {
      return right.call(this);
    }
  }

  public static <A,B> F2<Maybe<A>, F1<? super A, B>, Maybe<B>> map() {
    return (m, f) -> m.map(f);
  }

  public static <A> F1<Maybe<Maybe<A>>, Maybe<A>> join() {
    return m -> Monad.<μ, A>join().then(Maybe::asMaybe).call(m);
  }

public static class MaybeT<M extends Witness,A> implements Monad<Hkt<MaybeT.μ,M>,A>, Hkt2<MaybeT.μ,M,A> {

  public static class μ implements Witness{}

  private final Monad<M,Maybe<A>> runMaybeT;
  private final MonadUnit<M> mUnit;

  @Override
  public <B> MaybeT<M,B> map(F1<? super A, B> fn){
    return maybeT(runMaybeT.map(maybe_value -> maybe_value.map(fn)));
  }

  @Override
  public <B> MaybeT<M,B> semi(Monad<Hkt<μ, M>, B> mb) {
    return bind(a -> mb);
  }

  @Override
  public <B> MaybeT<M,B> bind(F1<? super A, ? extends Monad<Hkt<μ, M>, B>> fn) {
    return maybeT(runMaybeT.bind(maybe_value -> {
      return maybe_value.match(
          n -> mUnit.unit(nothing())
        , j -> asMaybeT(fn.call(j.value())).runMaybeT);
   }));
  }

  @Override
  public MonadUnit<Hkt<μ, M>> yield() {
    return new MonadUnit<Hkt<μ, M>>() {
      @Override
      public <B> Monad<Hkt<μ, M>, B> unit(B b) {
        return MaybeT.<M,B>maybeT().call(mUnit,b);
      }
    };
  }


  private MaybeT(Monad<M,Maybe<A>> runMaybeT) {
    this.runMaybeT = runMaybeT;  
    this.mUnit = runMaybeT.yield();
  }

  public static <M extends Witness,A> MaybeT<M,A> liftMaybeT(Monad<M,A> m) {
    return new MaybeT<M,A>(m.map(a -> Maybe.maybe(a)));
  }

  public static <M extends Witness, A> F2<MonadUnit<M>,A,MaybeT<M,A>> maybeT() {
    return (mUnit, a) -> new MaybeT<M, A>(mUnit.unit(Maybe.maybe(a)));
  }

  public static <M extends Witness,A> MaybeT<M,A> maybeT(Monad<M,Maybe<A>> runMaybeT) {
    return new MaybeT<M,A>(runMaybeT);
  }

  public static <M extends Witness,A> MaybeT<M,A> maybeT(A a, MonadUnit<M> mUnit) {
    return liftMaybeT(mUnit.unit(a));
  }

  public Monad<M,Maybe<A>> runMaybeT() {
    return runMaybeT;
  }

  public static <M extends Witness, A> MaybeT<M, A> asMaybeT(Monad<Hkt<MaybeT.μ, M>, A> wider) {
    return (MaybeT<M, A>) wider;
  }
}
}
