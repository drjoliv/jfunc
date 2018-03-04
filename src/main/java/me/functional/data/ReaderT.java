package me.functional.data;

import java.util.function.Function;

import me.functional.hkt.Hkt2;
import me.functional.hkt.Hkt3;
import me.functional.hkt.Witness;

/**
 *
 *
 * @author
 */
public class ReaderT <M extends Witness,R,A> implements Monad<Hkt2<ReaderT.μ,M,R>,A>, Hkt3<ReaderT.μ,M,R,A>{

  public static class μ implements Witness {}

  private Function<R,Monad<M,A>> runReaderT;

  private ReaderT(Function<R,Monad<M,A>> runReaderT) {
    this.runReaderT = runReaderT;
  }

  @Override
  public <B> ReaderT<M,R,B> mBind(Function<A, ? extends Monad<Hkt2<ReaderT.μ, M, R>, B>> fn) {
    return of(r -> {
      Monad<M,A> inner = runReaderT.apply(r);
      Monad<M,B> next  = inner.mBind(a ->  asReaderT(fn.apply(a)).runReaderT.apply(r));
      return next; 
    });
  }

  @Override
  public <B> ReaderT<M,R,B> semi(Monad<Hkt2<μ, M, R>, B> mb) {
    return mBind(a -> mb);
  }

  @Override
  public <B> ReaderT<M,R,B> mUnit(B b) {
    return new ReaderT<M,R,B>(runReaderT.andThen(ma -> ma.fmap(a -> b)));
  }

  @Override
  public <B> ReaderT<M,R,B> fmap(Function<A, B> fn) {
      return of((R r) -> {
        Monad<M,A> ma = runReaderT.apply(r);
        Monad<M,B> mb = ma.fmap(fn);
       return mb;
    });
  }

  /**
   *
   *
   * @param ma
   * @return
   */
  public static <M extends Witness,R,A> ReaderT<M,R,A> liftReaderT(Monad<M,A> ma) {
    return ReaderT.of(r -> ma);
  }

  public static <M extends Witness,R,A> ReaderT<FList.μ,R,Monad<M,A>> compose(FList<ReaderT<M,R,A>> readers) {
    return ReaderT.of((R r) -> {
      return readers.fmap(reader -> reader.runReader(r));
    });
  }

  public Monad<M,A> runReader(R r) {
   return runReaderT.apply(r); 
  }

  public ReaderT<M,R,R> mAsk() {
    return ReaderT.of(r -> runReaderT.andThen(ma -> ma.fmap(a -> r)).apply(r));
  }

  public final static class Reader<R,A> extends ReaderT<Identity.μ,R,A>{

    private Reader(Function<R,Monad<Identity.μ,A>> runReaderT) {
      super(runReaderT);
    }

    @Override
    public <B> Reader<R,B> semi(
        Monad<Hkt2<μ, me.functional.data.Identity.μ, R>, B> mb) {
      return new Reader<R,B>(super.semi(mb).runReaderT);
    }

    @Override
    public <B> Reader<R,B> mBind(
        Function<A, ? extends Monad<Hkt2<μ, me.functional.data.Identity.μ, R>, B>> fn) {
      return new Reader<R,B>(super.mBind(fn).runReaderT);
    }

    @Override
    public <B> Reader<R,B> fmap(Function<A, B> fn) {
      return new Reader<R,B>(super.fmap(fn).runReaderT);
    }

    @Override
    public Identity<A> runReader(R r) {
      return (Identity<A>)super.runReader(r);
    }

    public A run(R r) {
      return ((Identity<A>)super.runReader(r)).value();
    }
  }

  public static <M extends Witness, R, A> ReaderT<M, R, A> of(Function<R, Monad<M, A>> runReaderT) {
    return new ReaderT<M, R, A>(runReaderT);
  }

  public static <M extends Witness, R, A> ReaderT<M, R, A> asReaderT(Monad<Hkt2<ReaderT.μ, M, R>, A> wider) {
    return (ReaderT<M, R, A>) wider;
  }

  public static <R, A> Reader<R, A> asReader(Monad<Hkt2<ReaderT.μ, Identity.μ, R>, A> wider) {
    if(wider instanceof ReaderT)
      return new Reader<R,A>(asReaderT(wider).runReaderT);
    else
      return (Reader<R, A>) wider;
  }

  public static <R, A> Reader<R, A> reader(Function<R,A> fn) {
      return new Reader<R,A>(r -> Identity.of(fn.apply(r)));
  }

  public static <R, A> Reader<R, A> unit(A a) {
      return reader(r -> a);
  }

  public static <R> Reader<R, R> ask() {
      return reader(r -> r);
  }
}
