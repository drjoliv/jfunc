package drjoliv.fjava.data;

import static drjoliv.fjava.control.Bind.join;
import static drjoliv.fjava.control.bind.Eval.later;

import java.util.function.Consumer;

import drjoliv.fjava.control.Bind;
import drjoliv.fjava.control.BindUnit;
import drjoliv.fjava.control.bind.Eval;
import drjoliv.fjava.functions.F0;
import drjoliv.fjava.functions.F1;
import drjoliv.fjava.hkt.Hkt;
import drjoliv.fjava.hkt.Hkt2;
import drjoliv.fjava.hkt.Witness;

/**
 * {@code Either<L,R> } represents two posssible values; there are two instantiable subclasses of {@code Either<l,r>}, 
 * {@code Left<L,R>} or {@code Right<L,R>}.
 *
 * @author Desonte 'drjoliv' Jolivet
 */
public abstract class Either<L,R> implements Hkt2<Either.μ,L,R> {

  public static class μ implements Witness{}

  /**
   *
   * @param fn1
   * @param fn2
   * @return the either whose contensts have been transformed by fn1, or fn2.
   */
  public abstract <A,B> Either<A,B> bimap(F1<? super L,A> fn1, F1<? super R,B> fn2);

  /**
   * Returns true if this either is right otherwise it returns false.
   * @return true if this either is right otherwise it returns false.
   */
  public abstract boolean isRight();

  /**
   *
   *
   * @param left a function applied to this instrance of either if it is a left.
   * @param right a function applied to this instance of either if it is a right.
   * @return the value reaturned by the left or right function.
   */
  public abstract <A> A match(F1<Left<L,R>,A> left, F1<Right<L,R>,A> right);

  /**
   *
   *
   * @return boolean
   */
  public abstract boolean isLeft();

  /**
   *
   *
   * @return
   */
  public abstract L valueL() throws RightException;

  /**
   *
   *
   * @return
   */
  public abstract R valueR() throws LeftException;

  /**
   *
   *
   * @return
   */
  public RightProjection<L,R> right() {
    return new RightProjection<L,R>(later(() -> this));
  }

  /**
   *
   *
   * @return
   */
  public LeftProjection<L,R> left() {
    return new LeftProjection<L,R>(later(() -> this));
  }

  /**
  *
  *
  * @param l
  * @param <L>
  * @param <R>
  * @return
  */
  public static <L,R> Either<L,R> left(L l) {
    return left$(() -> l);
  }

  /**
  *
  *
  * @param l
   * @param <L>
  * @param <R>
 * @return
  */
  public static <L,R> Either<L,R> left$(F0<L> l) {
    return new Left<>(l);
  }

  /**
  *
  *
  * @param l
    * @param <L>
  * @param <R>
* @return
  */
  public static <L,R> Either<L,R> left$(Eval<L> l) {
    return new Left<>(l);
  }

  /**
  *
  *
  * @param r the value to 
   * @param <L>
  * @param <R>
 * @return
  */
  public static <L,R> Either<L,R> right(R r) {
    return right$(() -> r);
  }


  /**
  *
  *
  * @param r
   * @param <L>
  * @param <R>
 * @return
  */
  public static <L,R> Either<L,R> right$(F0<R> r) {
    return new Right<>(r);
  }

  /**
  *
  *
  * @param r
   * @param <L>
  * @param <R>
 * @return
  */
  public static <L,R> Either<L,R> right$(Eval<R> r) {
    return new Right<>(r);
  }

  /**
   *
   *
   * @param monad
   * @param <L>
  * @param <R>
  * @return
   */
  @SuppressWarnings("unchecked")
  public static <L, R> Either<L, R> asEither(Bind<Hkt<Either.μ, L>, R> monad) {
    return (Either<L, R>) monad;
  }

  /**
  *
  */
  public static class Left<L,R> extends Either<L,R> {

    @Override
    public String toString() {
      return "Left : "   + l.value();
    }

    private final Eval<L> l;

    private Left(F0<L> l) {
      this.l = later(l);
    }

    private Left(Eval<L> l) {
      this.l = l;
    }

    @Override
    public boolean isLeft() {
      return true;
    }

    @Override
    public boolean isRight() {
      return false;
    }

    @Override
    public L valueL() throws RightException {
      return l.value();
    }

    /**
     *
     *
     * @return
     */
      public L value() {
      return l.value();
    }

    @Override
    public R valueR() throws LeftException{
      throw new LeftException();
    }


    @Override
    public <A, B> Either<A, B> bimap(F1<? super L, A> fn1, F1<? super R, B> fn2) {
      return left$(l.map(fn1));
    }

    @Override
    public <A> A match(F1<Left<L, R>, A> left, F1<Right<L, R>, A> right) {
      return left.call(this);
    }
  }

  /**
  *
  */
  public static class Right<L, R> extends Either<L, R> {

    @Override
    public String toString() {
      return "Right : " + r.value();
    }

    private final Eval<R> r;

    private Right(F0<R> r) {
      this.r = later(r);
    }

    private Right(Eval<R> r) {
      this.r = r;
    }

    /**
     *
     *
     * @return
     */
      public R value() {
      return r.value();
    }

    @Override
    public boolean isRight() {
      return true;
    }

    @Override
    public boolean isLeft() {
      return false;
    }

    @Override
    public L valueL() throws RightException {
      throw new RightException();
    }

    @Override
    public R valueR() throws LeftException {
      return r.value();
    }

    @Override
    public <A, B> Either<A, B> bimap(F1<? super L, A> fn1, F1<? super R, B> fn2) {
      return right$(r.map(fn2));
    }

    @Override
    public <A> A match(F1<Left<L, R>, A> left, F1<Right<L, R>, A> right) {
      return right.call(this);
    }
  }

  /**
  *
  */
  public static class RightProjection<L, R> implements Bind<Hkt<RightProjection.μ,L>,R>, Hkt2<RightProjection.μ,L,R>{

    public static class μ implements Witness {}

    private final Eval<Either<L,R>> e;

    private RightProjection(Eval<Either<L,R>> e) {
      this.e = e;
    }

    @Override
    public <B> RightProjection<L, B> map(F1<? super R, B> fn) {
      return new RightProjection<L,B>(e.map(et -> et.bimap(F1.<L>identity(),fn)));
    }

    @Override
    public <B> RightProjection<L, B> bind(
        F1<? super R, ? extends Bind<Hkt<drjoliv.fjava.data.Either.RightProjection.μ, L>, B>> fn) {
      return asRightProjection(join(map(fn.then(RightProjection::asRightProjection))));
    }

    @Override
    public <B> RightProjection<L, B> semi(
        Bind<Hkt<drjoliv.fjava.data.Either.RightProjection.μ, L>, B> mb) {
      return bind(a -> mb);
    }

    @Override
    public BindUnit<Hkt<drjoliv.fjava.data.Either.RightProjection.μ, L>> yield() {
      return RightProjection::rightProjection;
    }

    /**
     *
     *
     * @param consumer
     * @return
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
     *
     *
     * @return
     */
      public Either<L,R> either() {
      return e.value();
    }

    /**
     *
     *
     * @param monad
     * @return
     */
      public static <L,R> RightProjection<L,R> asRightProjection(Bind<Hkt<RightProjection.μ,L>,R> monad) {
      return (RightProjection<L,R>) monad;
    }

    private static <L,R> RightProjection<L,R> rightProjection(R r) {
      return new RightProjection<>(later(() -> right(r)));
    }
  }

  /**
  *
  */
  public static class LeftProjection<L, R> implements Bind<Hkt<LeftProjection.μ,R>,L>, Hkt2<RightProjection.μ,L,R> {

    public static class μ implements Witness {}

    private final Eval<Either<L,R>> e;

    private LeftProjection(Eval<Either<L,R>> e) {
      this.e = e;
    }

    @Override
    public <B> LeftProjection<B, R> map(F1<? super L, B> fn) {
      return new LeftProjection<B,R>(e.map(et -> et.bimap(fn,F1.<R>identity())));
    }

    @Override
    public <B> LeftProjection<B, R> bind(
        F1<? super L, ? extends Bind<Hkt<drjoliv.fjava.data.Either.LeftProjection.μ, R>, B>> fn) {
      return asLeftProjection(join(map(fn.then(LeftProjection::asLeftProjection))));
    }

    @Override
    public <B> LeftProjection<B, R> semi(
        Bind<Hkt<drjoliv.fjava.data.Either.LeftProjection.μ, R>, B> mb) {
      return bind(a -> mb);
    }

    @Override
    public BindUnit<Hkt<drjoliv.fjava.data.Either.LeftProjection.μ, R>> yield() {
      return LeftProjection::leftProjection;
    }

    /**
     *
     *
     * @param consumer
     * @return
     */
      public LeftProjection<L, R> consume(Consumer<L> consumer) {
      Either<L,R> either = e.value();
      either.bimap(l -> {
        consumer.accept(l);
        return l;
      },F1.<R>identity());
      return this;
    }

    /**
     *
     *
     * @return
     */
      public Either<L,R> either() {
      return e.value();
    }

    /**
     *
     *
     * @param l
     * @return
     */
      private static <L,R> LeftProjection<L,R> leftProjection(L l) {
      return new LeftProjection<L,R>(later(() -> left(l)));
    }

    /**
     *
     *
     * @param monad
     * @return
     */
      public static <L,R> LeftProjection<L,R> asLeftProjection(Bind<Hkt<LeftProjection.μ,R>,L> monad) {
      return (LeftProjection<L, R>) monad;
    }
  }

  public static <A> A either(Either<A,A> e) {
    return e.match(
      l -> l.value()
     ,r -> r.value() 
    );
  }

  public static class LeftException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = -690190470847815799L;

    public LeftException(){
      super("This Either contains a Left value, but valueR was called.");
    }
  }

  public static class RightException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 6550427707346121413L;

    public RightException() {
      super("This Either contains a Right value, but valueL was called.");
    }
  }

}
