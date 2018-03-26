package me.functional.transformers;

import static me.functional.data.FList.flist;

import java.util.function.Function;

import me.functional.data.FList;
import me.functional.data.Identity;
import me.functional.data.Maybe;
import me.functional.data.Unit;
import me.functional.functions.F1;
import me.functional.functions.F2;
import me.functional.hkt.Hkt2;
import me.functional.hkt.Hkt3;
import me.functional.hkt.Witness;
import me.functional.type.Monad;
import me.functional.type.MonadUnit;

/**
 *
 * @author Desonte 'drjoliv' Jolivet
 */
public class ReaderT <M extends Witness,R,A> implements Monad<Hkt2<ReaderT.μ,M,R>,A>, Hkt3<ReaderT.μ,M,R,A> {

  public static class μ implements Witness {}

  private final Function<R,Monad<M,A>> runReaderT;
  private final MonadUnit<M> mUnit;

  private ReaderT(Function<R,Monad<M,A>> runReaderT, MonadUnit<M> mUnit) {
    this.runReaderT = runReaderT;
    this.mUnit = mUnit;
  }

  @Override
  public MonadUnit<Hkt2<me.functional.transformers.ReaderT.μ, M, R>> yield() {
    return new MonadUnit<Hkt2<me.functional.transformers.ReaderT.μ, M, R>>() {
      @Override
      public <B> Monad<Hkt2<μ, M, R>, B> unit(B b) {
        return ReaderT.<M,R,B>unit().call(mUnit,b);
      }
    };
  }

  @Override
  public <B> ReaderT<M,R,B> mBind(Function<? super A, ? extends Monad<Hkt2<ReaderT.μ, M, R>, B>> fn) {
    return readerT(r -> {
      Monad<M,A> inner = runReaderT.apply(r);
      Monad<M,B> next  = inner.mBind(a ->  asReaderT(fn.apply(a)).runReaderT.apply(r));
      return next; 
    }, mUnit);
  }

  @Override
  public <B> ReaderT<M,R,B> semi(Monad<Hkt2<μ, M, R>, B> mb) {
    return mBind(a -> mb);
  }

  @Override
  public <B> ReaderT<M,R,B> fmap(Function<? super A, B> fn) {
      return readerT((R r) -> {
        Monad<M,A> ma = runReaderT.apply(r);
        Monad<M,B> mb = ma.fmap(fn);
       return mb;
    }, mUnit);
  }

  /**
  *
  * @param ma A monad that will be lifted into the context of a ReaderT
  * @return a ReaderT
  * 
  */
  public static <M extends Witness,R,A> ReaderT<M,R,A> lift(Monad<M,A> ma) {
    return ReaderT.readerT(r -> ma, ma.yield());
  }

  /**
  *
  * @param readers
  * @return
  */
  public static <M extends Witness,R,A> ReaderT<FList.μ,R,Monad<M,A>> compose(FList<ReaderT<M,R,A>> readers) {
    return ReaderT.readerT((R r) -> {
      return readers.fmap(reader -> reader.runReader(r));
    }, FList.monadUnit);
  }

  /**
   *
   * @param r
   * @return
   */
  public Monad<M,A> runReader(R r) {
   return runReaderT.apply(r); 
  }

  /**
   *
   * @return
   */
  public static <M extends Witness, R> ReaderT<M,R,R> ask(MonadUnit<M> mUnit) {
    return ReaderT.readerT(r -> mUnit.unit(r), mUnit);
  }

  /**
   *
   * @return
   */
  public static <M extends Witness, R> F1<MonadUnit<M>, ReaderT<M,R,R>> ask() {
    return (mUnit) -> ReaderT.readerT(r -> mUnit.unit(r), mUnit);
  }

  /**
   *
   * @return
   */
  public static <M extends Witness, R, A> F2<F1<R,R>, ReaderT<M,R,A>, ReaderT<M,R,A>> local() {
    return (fn, m) -> {
      return ReaderT.readerT(fn.andThen(m.runReaderT), m.mUnit);
    };
  }

  /**
   *
   * @return
   */
  public static <M extends Witness, R, A> ReaderT<M,R,A> local(F1<R,R> fn, ReaderT<M,R,A> m) {
      return ReaderT.readerT(fn.andThen(m.runReaderT), m.mUnit);
  }

  /**
   *
   *
   * @param runReaderT
   * @param mUnit
   * @return
   */
  public static <M extends Witness, R, A> ReaderT<M, R, A> readerT(Function<R, Monad<M, A>> runReaderT, MonadUnit<M> mUnit) {
    return new ReaderT<M, R, A>(runReaderT, mUnit);
  }

  @SuppressWarnings("unchecked")
  public static <M extends Witness, R, A> ReaderT<M, R, A> asReaderT(Monad<Hkt2<ReaderT.μ, M, R>, A> wider) {
    return (ReaderT<M, R, A>) wider;
  }

  /**
   *
   * @return
   */
  public static <M extends Witness, R, A> F2<MonadUnit<M>, A, ReaderT<M, R, A>> unit() {
    return (mUnit, a) -> new ReaderT<M, R, A>(r -> mUnit.unit(a), mUnit);
  }

  /**
   *
   *
   * @param flist
   * @return
   */
  public static <M extends Witness, R, A> Maybe<ReaderT<M,R,FList<A>>> sequence(FList<ReaderT<M,R,A>> flist) {
    return flist
      .fmap(ma -> ma.fmap(a -> flist(a)))
      .reduce((m1, m2) -> asReaderT(Monad.liftM2(m1, m2, (a1, a2) -> a1.concat(a2))));
  }

  /**
   *
   *
   * @param flist
   * @return
   */
  public static <M extends Witness, R, A> Maybe<ReaderT<M,R,Unit>> sequence_(FList<ReaderT<M,R,A>> flist) {
    return flist
      .reduce((m1, m2) -> m1.semi(m2))
      .fmap(r -> r.fmap(a -> Unit.unit));
  }

  /**
  *
  */
  public final static class Reader<R,A> extends ReaderT<Identity.μ,R,A>{

    private Reader(Function<R,Monad<Identity.μ,A>> runReaderT) {
      super(runReaderT, Identity.monadUnit);
    }

    @Override
    public <B> Reader<R,B> semi(
        Monad<Hkt2<μ, me.functional.data.Identity.μ, R>, B> mb) {
      return new Reader<R,B>(super.semi(mb).runReaderT);
    }

    @Override
    public <B> Reader<R,B> mBind(
        Function<? super A, ? extends Monad<Hkt2<μ, me.functional.data.Identity.μ, R>, B>> fn) {
      return new Reader<R,B>(super.mBind(fn).runReaderT);
    }

    @Override
    public <B> Reader<R,B> fmap(Function<? super A, B> fn) {
      return new Reader<R,B>(super.fmap(fn).runReaderT);
    }

    @Override
    public Identity<A> runReader(R r) {
      return (Identity<A>)super.runReader(r);
    }

    /**
     *
     * @param r
     * @return
     */
    public A run(R r) {
      return ((Identity<A>)super.runReader(r)).value();
    }

    /**
     *
     * @param wider
     * @return
     */
    public static <R, A> Reader<R, A> asReader(Monad<Hkt2<ReaderT.μ, Identity.μ, R>, A> wider) {
      if(wider instanceof ReaderT)
        return new Reader<R,A>(asReaderT(wider).runReaderT);
      else
        return (Reader<R, A>) wider;
    }

    /**
     *
     * @param fn
     * @return
     */
    public static <R, A> Reader<R, A> reader(Function<R,A> fn) {
      return new Reader<R,A>(r -> Identity.id(fn.apply(r)));
    }

    /**
     * A Reader
     * @param a
     * @return
     */
    public static <R, A> Reader<R, A> reader(A a) {
      return reader(r -> a);
    }

    /**
     *
     *
     * @return
     */
    public static <R> Reader<R, R> readerAsk() {
      return asReader(ask(Identity.monadUnit));
    }

    /**
     *
     *
     * @param fn
     * @param ma
     * @return
     */
    public static <R,A> Reader<R, A> readerLocal(F1<R,R> fn, Reader<R,A> ma) {
      return asReader(local(fn,ma));
    }
  }
}
