package me.functional.data;


import java.util.function.BiFunction;
import java.util.function.Function;

import me.functional.hkt.Hkt;

/**
 * Represents a value of something(Just) or a value of Nothing.
 *
 * @author drjoliv@gmail.com
 */
public abstract class Maybe<A> implements Hkt<Maybe.μ, A> {

  /**
  * The witness type of Maybe
  */
  public static class μ {}

  private Maybe(){}

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

  /**
   * Wraps the given function within a Maybe.
   *
   * @param fn A function that will be wrapped within a Maybe.
   * @return A maybe that contains the given function.
   */
  public static final  <A,B> Maybe<Function<A,B>> pure(final Function<A,B> fn){
    return unit(fn);  
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
  public static  final <B> Maybe<B> unit(final B b) {
    if(b == null)
      return nothing();
    else
      return new Just<B>(b);
  }

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
  public static class Nothing<A> extends Maybe<A> {
    private static Nothing<?> instance = new Nothing<>();
    private Nothing(){

    }

    @SuppressWarnings("unchecked")
    @Override
    public <B> Maybe<B> map(final Function<A, B> fn) {
      return (Maybe<B>)instance;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <B> Maybe<B> bind(final Function<A, Maybe<B>> fn) {
      return (Maybe<B>)instance;
    }
  }

  /**
  * The instance of Myabe that contains a value.
  */
  public static class Just<A> extends Maybe<A> {
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
  }

  /**
   * 
   *
   * @param ma a Maybe.
   * @param fn a function that will be applied to the contents of the Maybe.
   * @return a Maybe.
   */
  public static <A,B> Maybe<B> For(final Maybe<A> ma, final Function<A,Maybe<B>> fn){
    return ma.bind(fn);
  }

  /**
   *
   *
   * @param ma a Maybe
   * @param fn a function that will be applied to the contents of the Maybe.
   * @param fn2 a function that cpatures the the values passed into the previous function.
   * @return a Maybe.
   */
  public static <A,B,C> Maybe<C> For(final Maybe<A> ma, final Function<A,Maybe<B>> fn,
      final BiFunction<A,B,Maybe<C>> fn2){
    return ma.bind(a -> fn.apply(a)
                          .bind(b -> fn2.apply(a,b)));
  }

  /**
   * Splat allows a function to be brought within the context of Maybe.
   *
   * @param fn a function that will be wrapped within a Maybe.
   * @param ma Applies the content of ma to the function wrapped within a maybe.
   * @return a Maybe.
   */
  public static <A,B> Maybe<B> Splat(final Function<A,B> fn, final Maybe<A> ma) {
    return pure(fn)
      .bind(f -> ma.map(f));
  }
}
