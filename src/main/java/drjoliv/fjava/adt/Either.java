package drjoliv.fjava.adt;

import static drjoliv.fjava.adt.Eval.later;
import static drjoliv.fjava.adt.Eval.now;
import static drjoliv.fjava.monad.Monad.join;

import java.util.function.Consumer;

import drjoliv.fjava.applicative.Applicative;
import drjoliv.fjava.applicative.ApplicativePure;
import drjoliv.fjava.functions.F0;
import drjoliv.fjava.functions.F1;
import drjoliv.fjava.hkt.Hkt;
import drjoliv.fjava.hkt.Hkt2;
import drjoliv.fjava.hkt.Witness;
import drjoliv.fjava.monad.Monad;
import drjoliv.fjava.monad.MonadUnit;

/**
 * Represents two posssible values either {@code Left<L,R>} or {@code Right<L,R>}.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public abstract class Either<L,R> implements Hkt2<Either.μ,L,R>, Case2<Either<L,R>, Either.Left<L,R>, Either.Right<L,R>> {

  /**
  * The witness type of {@code Either}.
  */
  public static class μ implements drjoliv.fjava.hkt.Witness{private μ(){}}

  /**
   * Applies the functions {@code fn1} and {@code fn2} to the contents of this either,
   * returning a new either whose content is the result of that function application.
   * @param fn1 a function for mapping the left content of this either.
   * @param fn2 a function for mapping the right side of this either.
   * @return the either whose contensts have been transformed by fn1 or fn2.
   */
  public abstract <A,B> Either<A,B> bimap(F1<? super L, ? extends A> fn1, F1<? super R, ? extends B> fn2);

  /**
   * Returns a maybe containing the value of this either if this either is left otherwise a maybe containing nothing.
   * @return a maybe containing the value of this either if this either is left otherwise a maybe containing nothing.
   */
  public Maybe<L> valueL() {
    return match(l -> Maybe.maybe$(l.value$())
               , r -> Maybe.nothing());
  }

  /**
   * Returns a maybe containing the value of this either if this either is right otherwise a maybe containing nothing.
   * @return a maybe containing the value of this either if this either is right otherwise a maybe containing nothing.
   */
  public Maybe<R> valueR() {
    return match(l -> Maybe.nothing()
               , r -> Maybe.maybe$(r.value$()));
  }

  /**
   * Returns the right projection of this either.
   * @return the right projection of this either.
   */
  public RightProjection<L,R> right() {
    return new RightProjection<L,R>(later(() -> this));
  }

 /**
  * Returns the left projection of this either.
  * @return the left projection of this either.
  */
  public LeftProjection<L,R> left() {
    return new LeftProjection<L,R>(later(() -> this));
  }

 /**
  * Creates an either that is of type left.
  * @param l the value to store wihin a left.
  * @param <L> the type of the left value.
  * @param <R> the type of the right value.
  * @return an either of type left.
  */
  public static <L,R> Either<L,R> left(L l) {
    return left$(now(l));
  }

  /**
  * Creates an either that is of type left.
  * @param l the value to store wihin a left.
  * @param <L> the type of the left value.
  * @param <R> the type of the right value.
  * @return an either of type left.
  */
  public static <L,R> Either<L,R> left$(Eval<L> l) {
    return new Left<>(l);
  }

  /**
  * Creates an either that is of type right.
  * @param l the value to store wihin a right.
  * @param <L> the type of the left value.
  * @param <R> the type of the right value.
  * @return an either of type right.
  */
  public static <L,R> Either<L,R> right(R r) {
    return right$(now(r));
  }


  /**
  * Creates an either that is of type right.
  * @param l the value to store wihin a right.
  * @param <L> the type of the left value.
  * @param <R> the type of the right value.
  * @return an either of type right.
  */
  public static <L,R> Either<L,R> right$(Eval<R> r) {
    return new Right<>(r);
  }


 /**
  * The left value of an either.
  */
  public static class Left<L,R> extends Either<L,R> {

    public final Eval<L> l;

    private Left(F0<L> l) {
      this.l = later(l);
    }

    private Left(Eval<L> l) {
      this.l = l;
    }

    /**
     * Returns the value in this left.
     * @return the value in this left.
     */
      public L value() {
      return l.value();
    }

    /**
     * Returns the value in this left.
     * @return the value in this left.
     */
      public Eval<L> value$() {
      return l;
    }

    @Override
    public <A, B> Either<A, B> bimap(F1<? super L, ? extends A> fn1, F1<? super R, ? extends B> fn2) {
      return left$(l.map(fn1));
    }

    @Override
    public <A> A match(F1<Left<L, R>, A> left, F1<Right<L, R>, A> right) {
      return left.call(this);
    }
  }

 /**
  * The right value of an either.
  */
  public static class Right<L, R> extends Either<L, R> {

    public final Eval<R> r;

    private Right(F0<R> r) {
      this.r = later(r);
    }

    private Right(Eval<R> r) {
      this.r = r;
    }

    /**
     * Returns the value in this right.
     * @return the value in this right.
     */
    public R value() {
      return r.value();
    }

    /**
     * Returns the value in this right.
     * @return the value in this right.
     */
    public Eval<R> value$() {
      return r;
    }

    @Override
    public <A, B> Either<A, B> bimap(F1<? super L, ? extends A> fn1, F1<? super R, ? extends B> fn2) {
      return right$(r.map(fn2));
    }

    @Override
    public <A> A match(F1<Left<L, R>, A> left, F1<Right<L, R>, A> right) {
      return right.call(this);
    }
  }

  /**
  * The right projection of an either.
  */
  public static class RightProjection<L, R> implements Monad<Hkt<RightProjection.μ,L>,R>, Hkt2<RightProjection.μ,L,R>{

    /**
     * The witness type of {@code RightProjection}.
     */
    public static class μ implements drjoliv.fjava.hkt.Witness {}

    private final Eval<Either<L,R>> e;

    private RightProjection(Eval<Either<L,R>> e) {
      this.e = e;
    }

    @Override
    public ApplicativePure<Hkt<μ, L>> pure() {
      return RightProjection::rightProjection;
    }

    @Override
    public <B> RightProjection<L, B> apply(Applicative<Hkt<μ, L>, ? extends F1<? super R, ? extends B>> f) {
      RightProjection<L, F1<?  super R,  B>> mf = (RightProjection<L, F1<?  super R,  B>>) f;
      return monad(Monad.liftM2(this, mf, (a,fn) -> fn.call(a)));
    }

    @Override
    public <B> RightProjection<L, B> map(F1<? super R, ? extends B> fn) {
      return new RightProjection<L,B>(e.map(et -> et.bimap(F1.<L>identity(),fn)));
    }

    @Override
    public <B> RightProjection<L, B> bind(
        F1<? super R, ? extends Monad<Hkt<drjoliv.fjava.adt.Either.RightProjection.μ, L>, B>> fn) {
      return monad(join(map(fn.then(RightProjection::monad))));
    }

    @Override
    public <B> RightProjection<L, B> semi(
        Monad<Hkt<drjoliv.fjava.adt.Either.RightProjection.μ, L>, B> mb) {
      return bind(a -> mb);
    }

    @Override
    public MonadUnit<Hkt<drjoliv.fjava.adt.Either.RightProjection.μ, L>> yield() {
      return RightProjection::rightProjection;
    }

    /**
     * Performs the action for the element in this right projection if it contains an element.
     * @param consumer an action to be peformed for the element in this.
     * @return this right projection.
     */
    public RightProjection<L, R> consume(Consumer<R> consumer) {
      Either<L,R> either = e.value();
      either.bimap(F1.<L>identity(), r -> {
        consumer.accept(r);
        return r;
      });
      return this;
    }

    /**
     * Converts this right projection into an either.
     * @return an either.
     */
    public Either<L,R> either() {
      return e.value();
    }

    /**
     * A helper function to convert/narrow a reference from a monad to its underlying type.
     * @param monad the right projection to be casted to its original type.
     * @return a right projection.
     */
     public static <L,R> RightProjection<L,R> monad(Monad<Hkt<RightProjection.μ,L>,R> monad) {
      return (RightProjection<L,R>) monad;
    }

   /**
    * Creates a right porjection from the given value.
    * @param r the value to be injected into a left projection.
    * @return a right projection.
    */
    public static <L,R> RightProjection<L,R> rightProjection(R r) {
      return new RightProjection<>(later(() -> right(r)));
    }
  }

  /**
  * The left projection of an either.
  */
  public static class LeftProjection<L, R> implements Monad<Hkt<LeftProjection.μ,R>,L>, Hkt2<RightProjection.μ,L,R> {

    /**
     * The witness type of {@code LeftProjection}.
     */
    public static class μ implements drjoliv.fjava.hkt.Witness {private μ(){}}

    private final Eval<Either<L,R>> e;

    private LeftProjection(Eval<Either<L,R>> e) {
      this.e = e;
    }

    @Override
    public <B> LeftProjection<B, R> map(F1<? super L, ? extends B> fn) {
      return new LeftProjection<B,R>(e.map(et -> et.bimap(fn,F1.<R>identity())));
    }

    @Override
    public <B> LeftProjection<B, R> apply(
        Applicative<Hkt<drjoliv.fjava.adt.Either.LeftProjection.μ, R>, ? extends F1<? super L, ? extends B>> f) {
      LeftProjection<F1<?  super L,  B>, R> mf = (LeftProjection<F1<?  super L,  B>, R>) f;
      return monad(Monad.liftM2(this, mf, (a,fn) -> fn.call(a)));
    }

    @Override
    public ApplicativePure<Hkt<drjoliv.fjava.adt.Either.LeftProjection.μ, R>> pure() {
      return LeftProjection::leftProjection;
    }

    @Override
    public <B> LeftProjection<B, R> bind(
        F1<? super L, ? extends Monad<Hkt<drjoliv.fjava.adt.Either.LeftProjection.μ, R>, B>> fn) {
      return monad(join(map(fn.then(LeftProjection::monad))));
    }

    @Override
    public <B> LeftProjection<B, R> semi(
        Monad<Hkt<drjoliv.fjava.adt.Either.LeftProjection.μ, R>, B> mb) {
      return bind(a -> mb);
    }

    @Override
    public MonadUnit<Hkt<drjoliv.fjava.adt.Either.LeftProjection.μ, R>> yield() {
      return LeftProjection::leftProjection;
    }

    /**
     * Performs the action for the element in this leftprojection if it contains a value.
     * @param consumer an action to be peformed for the element in this.
     * @return this left projection.
     */
    public LeftProjection<L, R> consume(Consumer<L> consumer) {

      Either<L,R> either = e.value();

      either.bimap(l -> {
        consumer.accept(l);
        return l;
      }, F1.<R>identity());

      return this;
    }

    /**
     * Converts this left projection into an either.
     * @return an either.
     */
    public Either<L,R> either() {
      return e.value();
    }

    /**
     * Creates a left porjection from the given value.
     * @param l the value to be injected into a left projection.
     * @return a left projection.
     */
    public static <L,R> LeftProjection<L,R> leftProjection(L l) {
      return new LeftProjection<L,R>(later(() -> left(l)));
    }

    /**
     * A helper function to convert/narrow a reference from a monad to its underlying type.
     * @param monad the left projection to be casted to its original type.
     * @return a left projection.
     */
    public static <L,R> LeftProjection<L,R> monad(Monad<Hkt<LeftProjection.μ,R>,L> monad) {
      return (LeftProjection<L, R>) monad;
    }
  }

  /**
   * Returns true if the given either is a left and false otherwise.
   * @param e the eiter whose type to check.
   * @return true if the given either is a left and false otherwise.
   */
  public static <L,R> boolean isLeft(Either<L,R> e) {
    return e.match( l -> true, r -> false );
  }

  /**
   * Returns true if the given either is a right and false otherwise.
   * @param e the either whose type to check.
   * @return true if the given either is a right and false otherwise.
   */
  public static <L,R> boolean isRight(Either<L,R> e) {
    return !isLeft(e);
  }

  /**
   * Returns a first class function of {@link isRight}.
   * @return a first class function of {@link isRight}.
   */
  public static <L,R> F1<Either<L,R>,Boolean> isRight() {
    return Either::isRight;
  }

  /**
   * Returns a first class function of {@link isLeft}.
   * @return a first class function of {@link isLeft}.
   */
  public static <L,R> F1<Either<L,R>,Boolean> isLeft() {
    return Either::isLeft;
  }

  /**
   * Extracts the value out of an either.
   * @param e the either whose value will be extracted.
   * @return the value within an either.
   */
  public static <A> A either(Either<A,A> e) {
    return e.match(l -> l.value(), r -> r.value() );
  }
}
