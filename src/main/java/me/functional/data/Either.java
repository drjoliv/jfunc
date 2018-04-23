package me.functional.data;

import static me.functional.functions.Eval.later;
import static me.functional.type.Bind.join;

import java.util.function.Consumer;

import me.functional.functions.Eval;
import me.functional.functions.F0;
import me.functional.functions.F1;
import me.functional.hkt.Hkt;
import me.functional.hkt.Hkt2;
import me.functional.hkt.Witness;
import me.functional.type.Bind;
import me.functional.type.BindUnit;

/**
 *
 *
 * @author Desonte 'drjoliv' Jolivet
 */
public abstract class Either<L,R> implements Hkt2<Either.μ,L,R> {

  public static class μ implements Witness{}

  public abstract <A,B> Either<A,B> bimap(F1<? super L,A> fn1, F1<? super R,B> fn2);

  /**
   *
   *
   * @return
   */
  public abstract boolean isRight();

  /**
   *
   *
   * @return
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
  * @return
  */
  public static <L,R> Either<L,R> left(L l) {
    return new Left<>(() -> l);
  }

  /**
  *
  *
  * @param l
  * @return
  */
  public static <L,R> Either<L,R> left$(F0<L> l) {
    return new Left<>(l);
  }

  /**
  *
  *
  * @param l
  * @return
  */
  public static <L,R> Either<L,R> left$(Eval<L> l) {
    return new Left<>(l);
  }

  /**
  *
  *
  * @param r
  * @return
  */
  public static <L,R> Either<L,R> right(R r) {
    return new Right<>(() -> r);
  }

  /**
  *
  *
  * @param r
  * @return
  */
  public static <L,R> Either<L,R> right$(F0<R> r) {
    return new Right<>(r);
  }

  /**
  *
  *
  * @param r
  * @return
  */
  public static <L,R> Either<L,R> right$(Eval<R> r) {
    return new Right<>(r);
  }

  /**
   *
   *
   * @param monad
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <L, R> Either<L, R> asEither(Bind<Hkt<Either.μ, L>, R> monad) {
    return (Either<L, R>) monad;
  }

  private static class Left<L,R> extends Either<L,R> {

    private final Eval<L> l;

    public Left(F0<L> l) {
      this.l = later(l);
    }

    public Left(Eval<L> l) {
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

    @Override
    public R valueR() throws LeftException{
      throw new LeftException();
    }


    @Override
    public <A, B> Either<A, B> bimap(F1<? super L, A> fn1, F1<? super R, B> fn2) {
      return left$(l.map(fn1));
    }
  }

  private static class Right<L, R> extends Either<L, R> {

    private final Eval<R> r;

    public Right(F0<R> r) {
      this.r = later(r);
    }

    public Right(Eval<R> r) {
      this.r = r;
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
        F1<? super R, ? extends Bind<Hkt<me.functional.data.Either.RightProjection.μ, L>, B>> fn) {
      return asRightProjection(join(map(fn.then(RightProjection::asRightProjection))));
    }

    @Override
    public <B> RightProjection<L, B> semi(
        Bind<Hkt<me.functional.data.Either.RightProjection.μ, L>, B> mb) {
      return bind(a -> mb);
    }

    @Override
    public BindUnit<Hkt<me.functional.data.Either.RightProjection.μ, L>> yield() {
      return RightProjection::rightProjection;
    }

    public RightProjection<L, R> consume(Consumer<R> consumer) {
      Either<L,R> either = e.value();
      either.bimap(F1.<L>identity(), r -> {
        consumer.accept(r);
        return r;
      });
      return this;
    }

    public Either<L,R> either() {
      return e.value();
    }

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
        F1<? super L, ? extends Bind<Hkt<me.functional.data.Either.LeftProjection.μ, R>, B>> fn) {
      return asLeftProjection(join(map(fn.then(LeftProjection::asLeftProjection))));
    }

    @Override
    public <B> LeftProjection<B, R> semi(
        Bind<Hkt<me.functional.data.Either.LeftProjection.μ, R>, B> mb) {
      return bind(a -> mb);
    }

    @Override
    public BindUnit<Hkt<me.functional.data.Either.LeftProjection.μ, R>> yield() {
      return LeftProjection::leftProjection;
    }

    public LeftProjection<L, R> consume(Consumer<L> consumer) {
      Either<L,R> either = e.value();
      either.bimap(l -> {
        consumer.accept(l);
        return l;
      },F1.<R>identity());
      return this;
    }

    public Either<L,R> either() {
      return e.value();
    }

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

    public RightException(){
      super("This Either contains a Right value, but valueL was called.");
    }
  }

}
