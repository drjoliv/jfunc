package me.functional.data;

import java.util.function.Function;

import me.functional.data.Maybe.μ;
import me.functional.functions.Eval;
import me.functional.functions.F0;
import me.functional.functions.F1;

import static me.functional.functions.Eval.*;
import me.functional.hkt.Hkt;
import me.functional.hkt.Witness;
import me.functional.type.Alternative;
import me.functional.type.Bind;
import me.functional.type.BindUnit;

/**
 * Represents a value of something(Just) or a value of Nothing.
 *
 * @author drjoliv@gmail.com
 */
public abstract class Maybe<A> implements Hkt<Maybe.μ, A>, Bind<Maybe.μ, A>, 
       Alternative<Hkt<Maybe.μ, A>> {

  @Override
  public Maybe<A> alt(Hkt<μ, A> m) {
    return isSome() ? this : asMaybe(m);
  }

  @Override
  public abstract <B> Maybe<B> fmap(F1<? super A, B> fn);

  public static class μ implements Witness {}

  private Maybe() {}

  @Override
  public <B> Maybe<B> mBind(F1<? super A, ? extends Bind<μ, B>> fn) {
    if (isSome())
      return (Maybe<B>) fn.call(value());
    else
      return nothing();
  }

  @Override
  public <B> Maybe<B> semi(Bind<μ, B> mb) {
    return mBind(a -> mb);
  }

  @Override
  public BindUnit<μ> yield() {
    return Maybe::maybe;
  }

  /**
   * Returns <code> a </code> if <code> this </code> Maybe is <code> Nothing </code>.
   * @param A the return type of <code>orSome</code>.
   * @return <code> A </code> if <code> this </code> Maybe is <code> Nothing </code>.
   */
  public A orSome(A a) {
    return isSome() ? value() : a;
  }

  /**
   *
   *
   * @param maybe
   * @return
   */
  public Maybe<A> orElse(Maybe<A> maybe) {
    return isSome() ? this : maybe;
  }

  /**
   * True if <code> this </code> Maybe contains a value and false otherwise.
   * @return True if <code> this </code> Maybe contains a value and false otherwise.
   */
  public abstract boolean isSome();

  /**
   * Returns the value of this Maybe if there is one and a RuntimeException otherwise.
   * @return Returns the value of this Maybe if there is one and a RuntimeException otherwise.
   */
  public abstract A value();

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
   *
   * @param monad 
   * @return
   */
  public static <B> Maybe<B> asMaybe(Bind<Maybe.μ, B> monad) {
    return (Maybe<B>) monad;
  }

  /**
   *
   * @param monad 
   * @return
   */
  public static <B> Maybe<B> asMaybe(Hkt<Maybe.μ, B> hkt) {
    return (Maybe<B>) hkt;
  }

 /**
  *
  *
  * @param a
  * @return
  */
  public static <A> Maybe<A> maybe(A a) {
    if (a == null)
      return nothing();
    else
      return new Just<A>(now(a));
  }

 /**
  *
  *
  * @param a
  * @return
  */
  public static <A> Maybe<A> maybe$(F0<A> fn) {
      return new Just<A>(later(fn));
  }

  /**
   * The instance of Maybe that does not contain a value.
   */
  private static class Nothing<A> extends Maybe<A> {
    private static Nothing<?> instance = new Nothing<>();

    private Nothing() {}

    @Override
    public boolean isSome() {
      return false;
    }

    @Override
    public A value() {
      throw new UnsupportedOperationException("There is no value in this Maybe");
    }

    @Override
    public <B> Maybe<B> fmap(F1<? super A, B> fn) {
      return nothing();
    }
  }

  /**
   * The instance of Myabe that contains a value.
   */
  private static class Just<A> extends Maybe<A> {

    private Eval<A> value;

    private Just(Eval<A> value) {
      this.value = value;
    }

    @Override
    public boolean isSome() {
      return (value.value() != null) ? true : false;
    }

    @Override
    public A value() {
        return value.value();
    }

    @Override
    public <B> Maybe<B> fmap(F1<? super A, B> fn) {
      return new Just<B>(value.fmap(fn));
    }
  }
}
