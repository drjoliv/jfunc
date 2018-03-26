package me.functional.data;

import java.util.function.Function;

import me.functional.hkt.Hkt;
import me.functional.hkt.Witness;
import me.functional.type.Monad;
import me.functional.type.MonadUnit;

/**
 * Represents a value of something(Just) or a value of Nothing.
 *
 * @author drjoliv@gmail.com
 */
public abstract class Maybe<A> implements Hkt<Maybe.μ, A>, Monad<Maybe.μ, A> {

  public static class μ implements Witness {}

  private Maybe() {}

  @Override
  public <B> Maybe<B> fmap(Function<? super A, B> fn) {
    if (isSome())
      return new Just<B>(fn.apply(value()));
    else
      return Maybe.nothing();
  }


  @Override
  public <B> Maybe<B> mBind(Function<? super A, ? extends Monad<μ, B>> fn) {
    if (isSome())
      return (Maybe<B>) fn.apply(value());
    else
      return nothing();
  }

  @Override
  public <B> Maybe<B> semi(Monad<μ, B> mb) {
    return mBind(a -> mb);
  }

  @Override
  public MonadUnit<μ> yield() {
    return monadUnit;
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

  public static MonadUnit<Maybe.μ> monadUnit = new MonadUnit<Maybe.μ>() {
    @Override
    public <A> Monad<μ, A> unit(A a) {
      return Maybe.maybe(a);
    }
  };

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
  public static <B> Maybe<B> asMaybe(Monad<Maybe.μ, B> monad) {
    return (Maybe<B>) monad;
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
      return new Just<A>(a);
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
  }

  /**
   * The instance of Myabe that contains a value.
   */
  private static class Just<A> extends Maybe<A> {

    private A value;

    private Just(A value) {
      this.value = value;
    }

    @Override
    public boolean isSome() {
      return true;
    }

    @Override
    public A value() {
      return value;
    }
  }
}
