package me.functional.data;

import java.util.function.BiFunction;
import java.util.function.Function;

import me.functional.hkt.Hkt;
import me.functional.hkt.Witness;

/**
 * Represents a value of something(Just) or a value of Nothing.
 *
 * @author drjoliv@gmail.com
 */
public abstract class Maybe<A> implements Hkt<Maybe.μ, A>, Monad<Maybe.μ,A> {

  @Override
  public <B> Maybe<B> fmap(Function<A, B> fn) {
     if(isSome())
      return new Just<B>(fn.apply(value()));
    else
      return Maybe.nothing();
  }

  @Override
  public <B> Maybe<B> mBind(Function<A, ? extends Monad<μ, B>> fn) {
    if(isSome())
      return (Maybe<B>)fn.apply(value());
    else return nothing();
  }

  @Override
  public <B> Maybe<B> semi(Monad<μ, B> mb) {
    return mBind(a -> mb);
  }

  /**
   * The witness type of Maybe
   */
    public static class μ implements Witness{}

  private Maybe(){}

  public abstract boolean isSome();
  public abstract A value();

  /**
   * Applys the given function to the contents of this Maybe, and returns the result of that function
   * wrapped within a Maybe.
   *
   * @param fn a function that will be applyed to the contents of this Myabe.
   * @return a Maybe.
   */
  public abstract <B> Maybe<B> map(final Function<A,B> fn);

  /**
   * Applys the given function to the contents of this Maybe, the given function must return a Maybe.
   *
   * @param fn a function that returns a Maybe
   * @return a Maybe.
   */
  public abstract <B> Maybe<B> bind(final Function<A,Maybe<B>> fn);

  @Override
  public <B> Maybe<B> mUnit(B b) {
    if(b == null)
      return nothing();
    else
      return new Just<B>(b);
  }


  /**
   * Returns an instance of Maybe that contains nothing.
   *
   * @return an intstance of Maybe that contains Nothing.
   */
  @SuppressWarnings("unchecked")
  public static <B> Maybe<B> nothing() {
    return (Maybe<B>)Nothing.instance;
  }


  /**
   * Brings the given value into the context of a Maybe, if the value is null
   * the returned maybe will be Nothing, otherwise it will be a Just coontaining
   * given value.
   *
   * @param b a value to wrap within a Maybe.
   * @return a Maybe contianing the given value.
   */

  /**
   * Creates a Higher Kinded Type from this Maybe.
   *
   * @return a High Kinded Type whose witness is Maybe.μ.
   */
  public final Hkt<Maybe.μ, A> widen(){
    return (Hkt<Maybe.μ,A>) this;
  }

  /**
  * The instance of Maybe that does not contain a value.
  */
  private static class Nothing<A> extends Maybe<A> {
    private static Nothing<?> instance = new Nothing<>();
    private Nothing(){}

    @Override
    public <B> Maybe<B> map(final Function<A, B> fn) {
      return nothing();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <B> Maybe<B> bind(final Function<A, Maybe<B>> fn) {
      return (Maybe<B>)instance;
    }

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
      public <B> Maybe<B> map(final Function<A, B> fn) {
        return new Just<B>(fn.apply(this.value));
      }

      @Override
      public <B> Maybe<B> bind(final Function<A, Maybe<B>> fn) {
        return fn.apply(this.value);
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


  public static <B> Maybe<B> narrow(Monad<Maybe.μ,B> monad) {
    return (Maybe<B>) monad; 
  }

  public static <A> Maybe<A> of(A a) {
    if (a == null)
      return nothing();
    else
      return new Just<A>(a);
  }
}
