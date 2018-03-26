package me.functional.data;

import java.util.function.Function;

import me.functional.hkt.Hkt;
import me.functional.hkt.Hkt2;
import me.functional.hkt.Witness;
import me.functional.type.Monad;
import me.functional.type.MonadUnit;

/**
 *
 *
 * @author Desonte 'drjoliv' Jolivet
 */
public abstract class Either<L,R> implements Hkt2<Either.μ,L,R> {

  public static class μ implements Witness{}

  /**
  *
  *
  * @param l
  * @return
  */
  public static <L,R> Either<L,R> left(L l) {
    return new Left<>(l);
  }

  /**
  *
  *
  * @param r
  * @return
  */
  public static <L,R> Either<L,R> right(R r) {
    return new Right<>(r);
  }

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
  public abstract L valueL();

  /**
   *
   *
   * @return
   */
  public abstract R valueR();

  /**
   *
   *
   * @return
   */
  public RightProjection<L,R> right() {
    return new RightProjection<>(this);
  }

  /**
   *
   *
   * @return
   */
  public LeftProjection<L,R> left() {
    return new LeftProjection<L,R>(this);
  }

  private static class Left<L,R> extends Either<L,R> {

    private final L l;

    public Left(L l) {
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
    public L valueL() {
      return l;
    }

    @Override
    public R valueR() {
      throw new UnsupportedOperationException("This is not right value.");
    }
  }

  private static class Right<L, R> extends Either<L, R> {

    private final R r;

    public Right(R r) {
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
    public L valueL() {
      throw new UnsupportedOperationException("This is not a left vlaue.");
    }

    @Override
    public R valueR() {
      return r;
    }
  }

  /**
  *
  */
  public static class RightProjection<L, R> implements Monad<Hkt<RightProjection.μ,L>,R>, Hkt2<RightProjection.μ,L,R>{

    public static class μ implements Witness {}

    private final Either<L,R> e;

    private RightProjection(Either<L,R> e) {
      this.e = e;
    }

    private RightProjection(R r) {
      this(right(r));
    }

    public static <L> MonadUnit<Hkt<RightProjection.μ,L>> monadUnit() {
      return new MonadUnit<Hkt<RightProjection.μ,L>>() {
        @Override
        public <A> Monad<Hkt<me.functional.data.Either.RightProjection.μ, L>, A> unit(A a) {
          return rProjection(a);
        }
      };
    }

    public Either<L,R> either() {
      return e;
    }

    /**
     *
     *
     * @param r
     * @return
     */
      public static <L,R> RightProjection<L, R> rProjection(R r) {
      return new RightProjection<L,R>(r);
    }

    @Override
    public <B> RightProjection<L, B> fmap(Function<? super R, B> fn) {
      return e.isRight()
        ? rProjection(fn.apply(e.valueR()))
        : new RightProjection<>(left(e.valueL()));
    }

    @Override
    public <B> RightProjection<L, B> mBind(
        Function<? super R, ? extends Monad<Hkt<me.functional.data.Either.RightProjection.μ, L>, B>> fn) {
      return e.isRight()
        ? asRightProjection(fn.apply(e.valueR()))
        : new RightProjection<>(left(e.valueL()));
    }

    @Override
    public <B> RightProjection<L, B> semi(
        Monad<Hkt<me.functional.data.Either.RightProjection.μ, L>, B> mb) {
      return mBind(a -> mb);
    }

    @Override
    public MonadUnit<Hkt<me.functional.data.Either.RightProjection.μ, L>> yield() {
      return monadUnit();
    }

    public static <L,R> RightProjection<L,R> asRightProjection(Monad<Hkt<RightProjection.μ,L>,R> monad) {
      return (RightProjection<L,R>) monad;
    }
  }

  /**
  *
  */
  public static class LeftProjection<L, R> implements Monad<Hkt<LeftProjection.μ,R>,L>, Hkt2<RightProjection.μ,L,R> {

    public static class μ implements Witness {}

    private final Either<L,R> e;

    private LeftProjection(Either<L,R> e) {
      this.e = e;
    }

    private LeftProjection(L l) {
      this(left(l));
    }

    public Either<L,R> either() {
      return e;
    }

    /**
     *
     *
     * @param l
     * @return
     */
      public static <L,R> LeftProjection<L, R> lProjection(L l) {
      return new LeftProjection<L,R>(l);
    }

    /**
     *
     *
     * @return
     */
      public static <R> MonadUnit<Hkt<LeftProjection.μ,R>> monadUnit() {
      return new MonadUnit<Hkt<LeftProjection.μ,R>>() {
        @Override
        public <A> Monad<Hkt<me.functional.data.Either.LeftProjection.μ, R>, A> unit(A a) {
          return lProjection(a);
        }
      };
    }

    @Override
    public <B> LeftProjection<B, R> fmap(Function<? super L, B> fn) {
      return e.isLeft()
        ? lProjection(fn.apply(e.valueL()))
        : new LeftProjection<>(right(e.valueR()));
    }

    @Override
    public <B> LeftProjection<B, R> mBind(
        Function<? super L, ? extends Monad<Hkt<me.functional.data.Either.LeftProjection.μ, R>, B>> fn) {
      return e.isLeft()
        ? asLeftProjection(fn.apply(e.valueL()))
        : new LeftProjection<>(right(e.valueR()));
    }

    @Override
    public <B> LeftProjection<B, R> semi(
        Monad<Hkt<me.functional.data.Either.LeftProjection.μ, R>, B> mb) {
      return mBind(a -> mb);
    }

    @Override
    public MonadUnit<Hkt<me.functional.data.Either.LeftProjection.μ, R>> yield() {
      return monadUnit();
    }

    /**
     *
     *
     * @param monad
     * @return
     */
      public static <L,R> LeftProjection<L,R> asLeftProjection(Monad<Hkt<LeftProjection.μ,R>,L> monad) {
      return (LeftProjection<L, R>) monad;
    }
  }

  /**
   *
   *
   * @param monad
   * @return
   */
  public static <L, R> Either<L, R> asEither(Monad<Hkt<Either.μ, L>, R> monad) {
    return (Either<L, R>) monad;
  }
}
