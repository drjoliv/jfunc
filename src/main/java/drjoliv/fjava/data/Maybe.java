package drjoliv.fjava.data;

import java.util.function.Function;

import drjoliv.fjava.control.Alternative;
import drjoliv.fjava.control.Bind;
import drjoliv.fjava.control.BindUnit;
import drjoliv.fjava.control.bind.Eval;
import drjoliv.fjava.data.Maybe.μ;
import drjoliv.fjava.functions.F0;
import drjoliv.fjava.functions.F1;
import drjoliv.fjava.functions.F2;
import drjoliv.fjava.hkt.Hkt;
import drjoliv.fjava.hkt.Witness;

import static drjoliv.fjava.control.Bind.*;
import static drjoliv.fjava.control.bind.Eval.*;

/**
 * Represents a value of something(Just) or a value of Nothing.
 *
 * @author drjoliv@gmail.com
 */
public abstract class Maybe<A> implements Hkt<Maybe.μ, A>, Bind<Maybe.μ, A>, 
       Alternative<Hkt<Maybe.μ, A>> {

  @Override
  public Maybe<A> alt(Alternative<Hkt<Maybe.μ, A>> alt) {
    return isSome() ? this : asMaybe(alt);
  }

  @Override
  public abstract <B> Maybe<B> map(F1<? super A, B> fn);

  /**
   *
   * @param just
   * @param nothing
   * @return
   */
  public abstract A caseOf(F1<Just<A>,A> just, F1<Nothing<A>,A> nothing);


  public static interface μ extends Witness {}

  private Maybe() {}

  @Override
  public abstract <B> Maybe<B> bind(F1<? super A, ? extends Bind<μ, B>> fn);

  @Override
  public <B> Maybe<B> semi(Bind<μ, B> mb) {
    return bind(a -> mb);
  }

  @Override
  public BindUnit<μ> yield() {
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
    return (Maybe<B>) Nothing.instance;
  }

  /**
   * Narrows thw argument into a maybe.
   * @param monad the instance of bind that will be narrowed.
   * @param <B> the type of the contents of the narrowed bind.
   * @return a myabe
   */
  public static <B> Maybe<B> asMaybe(Bind<Maybe.μ, B> monad) {
    return (Maybe<B>) monad;
  }

  /**
   * 
   * Narrows thw argument into a maybe.
   * @param alt 
   * @param <B> the type of the contents of the narrowed alternative.
   * @return a maybe.
   */
  public static <B> Maybe<B> asMaybe(Alternative<Hkt<Maybe.μ, B>> alt) {
    return (Maybe<B>) alt;
  }

  /**
   * Narrows thw argument into a maybe.
   * @param monad 
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
  public static class Nothing<A> extends Maybe<A> {
    private static Nothing<?> instance = new Nothing<>();

    private Nothing() {}

    @Override
    public boolean isSome() {
      return false;
    }

    @Override
    public <B> Maybe<B> map(F1<? super A, B> fn) {
      return nothing();
    }

    @Override
    public <B> Maybe<B> bind(F1<? super A, ? extends Bind<μ, B>> fn) {
      return nothing();
    }

    @Override
    public A caseOf(F1<Just<A>, A> just, F1<Nothing<A>, A> nothing) {
      return nothing.call(this);
    }

    @Override
    public A toNull() {
      return null;
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
    public <B> Maybe<B> bind(F1<? super A, ? extends Bind<μ, B>> fn) {
      return asMaybe(Bind.join(map(fn.then(Maybe::asMaybe))));
    }

    @Override
    public A caseOf(F1<Just<A>, A> just, F1<Nothing<A>, A> nothing) {
      return just.call(this);
    }
  }

  public static <A,B> F2<Maybe<A>, F1<? super A, B>, Maybe<B>> map() {
    return (m, f) -> m.map(f);
  }

  public static <A> F1<Maybe<Maybe<A>>, Maybe<A>> join() {
    return m -> Bind.<μ,A>join()
      .then(Maybe::asMaybe)
      .call(m);
  }
}
