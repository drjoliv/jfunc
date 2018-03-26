package me.functional.transformers;

import static me.functional.data.FList.flist;
import static me.functional.data.Pair.pair;

import java.util.function.Function;
import java.util.function.Supplier;

import me.functional.data.FList;
import me.functional.data.Identity;
import me.functional.data.Maybe;
import me.functional.data.Pair;
import me.functional.data.Unit;
import me.functional.functions.F1;
import me.functional.functions.F3;
import me.functional.hkt.Hkt2;
import me.functional.hkt.Hkt3;
import me.functional.hkt.Witness;
import me.functional.type.Monad;
import me.functional.type.MonadUnit;
import me.functional.type.Monoid;

/**
 *
 *
 * @author Desonte 'drjoliv' Jolivet
 */
public class WriterT <M extends Witness,W,A> implements Monad<Hkt2<WriterT.μ,M,W>,A>, Hkt3<WriterT.μ,M,W,A>{

  public static class μ implements Witness{}

  private final Supplier<Monad<M,Pair<A,W>>> runWriterT;
  private final Monoid<W> monoid;
  private final MonadUnit<M> mUnit;

  private WriterT(Supplier<Monad<M,Pair<A,W>>> runWriterT, Monoid<W> monoid, MonadUnit<M> mUnit) {
    this.runWriterT = runWriterT;
    this.monoid     = monoid;
    this.mUnit      = mUnit;
  }

  @Override
  public <B> WriterT<M,W,B> fmap(Function<? super A, B> fn) {
    return new WriterT<M,W,B>(() -> runWriterT.get().fmap(p -> Pair.of(fn.apply(p.fst),p.snd)), monoid, mUnit);
  }

  @Override
  public <B> WriterT<M,W,B> mBind(Function<? super A, ? extends Monad<Hkt2<μ, M, W>, B>> fn) {
    return new WriterT<M,W,B>(() -> {
      return Monad.For(runWriterT.get()
          , p     -> asWriterT(fn.apply(p.fst)).runWriterT.get()
          ,(p,p2) -> mUnit.unit(Pair.of(p2.fst, monoid.mappend(p.snd,p2.snd))));
    }
    , monoid, mUnit);
  }

  @Override
  public <B> WriterT<M,W,B> semi(Monad<Hkt2<μ, M, W>, B> mb) {
    return mBind(a -> mb);
  }

  @Override
  public MonadUnit<Hkt2<μ, M, W>> yield() {
    return new MonadUnit<Hkt2<μ, M, W>>(){
      @Override
      public <B> Monad<Hkt2<μ, M, W>, B> unit(B b) {
        return WriterT.<M,W,B>unit().call(mUnit,monoid,b);
      }
    };
  }

  /**
   *
   *
   * @return
   */
  public Monoid<W> monoid() {
    return monoid;
  }

  /**
   *
   *
   * @return
   */
  public Monad<M,Pair<A,W>> runWriterT(){
    return runWriterT.get();
  }

  /**
   *
   *
   * @return
   */
  public Monad<M,W> execWriterT(){
    return runWriterT.get().fmap(p -> p.snd);
  }

  /**
   *
   *
   * @return
   */
  public static <M extends Witness,W,B> F3<MonadUnit<M>, Monoid<W>, B, WriterT<M,W,B>>unit() {
    return (mUnit, monoid, b) -> new WriterT<M,W,B>(() -> mUnit.unit(pair(b,monoid.mempty())), monoid, mUnit);
  }

  @SuppressWarnings("unchecked")
  public static <M extends Witness, W, A> WriterT<M, W, A> asWriterT(Monad<Hkt2<Writer.μ, M, W>, A> mb) {
    return (WriterT<M, W, A>) mb;
  }

  /**
  *
  */
  public static final class Writer<W,A> extends WriterT<Identity.μ,W,A> {

    private Writer(Supplier<Monad<Identity.μ,Pair<A,W>>> runWriterT, Monoid<W> monoid) {
      super(runWriterT, monoid, Identity.monadUnit);
    }

    /**
     *
     *
     * @return
     */
    public Pair<A,W> run() {
      return runWriterT().value();
    }

    /**
     *
     *
     * @return
     */
    public W log() {
      return runWriterT().value().snd;
    }

    /**
     *
     *
     * @return
     */
    public A exec() {
      return runWriterT().value().fst;
    }

    @Override
    public <B> Writer<W,B> fmap(Function<? super A, B> fn) {
      final WriterT<Identity.μ,W,B> w = super.fmap(fn);
      return new Writer<W,B>(w.runWriterT,w.monoid);
    }

    @Override
    public <B> Writer<W,B> mBind(
        Function<? super A, ? extends Monad<Hkt2<μ, me.functional.data.Identity.μ, W>, B>> fn) {
      final WriterT<Identity.μ,W,B> w = super.mBind(fn);
      return new Writer<W,B>(w.runWriterT,w.monoid);
    }

    @Override
    public <B> Writer<W,B> semi(
      final Monad<Hkt2<μ, me.functional.data.Identity.μ, W>, B> mb) {
      final WriterT<Identity.μ,W,B> w = super.semi(mb);
      return new Writer<W,B>(w.runWriterT,w.monoid);
    }

    @Override
    public Identity<Pair<A, W>> runWriterT() {
      return (Identity<Pair<A, W>>)super.runWriterT();
    }

    /**
     *
     *
     * @param flist
     * @return
     */
    public static <M extends Witness, W, A> Maybe<WriterT<M,W,FList<A>>> sequence(FList<WriterT<M,W,A>> flist) {
        return flist
          .fmap(ma -> ma.fmap(a -> flist(a)))
          .reduce((m1, m2) -> asWriterT(Monad.liftM2(m1, m2, (a1, a2) -> a1.concat(a2))));
    }

    /**
     *
     *
     * @param flist
     * @return
     */
    public static <M extends Witness, W, A> Maybe<WriterT<M,W,Unit>> sequence_(FList<WriterT<M,W,A>> flist) {
      return flist
        .reduce((m1,m2) -> m1.semi(m2))
        .fmap(w -> w.fmap(a -> Unit.unit));
    }

    /**
     *
     *
     * @param a
     * @param w
     * @param m
     * @return
     */
    public static <W,A> Writer<W,A> writer(A a, W w, Monoid<W> m) {
      return new Writer<W,A>(() -> Identity.id(Pair.of(a,w)), m);
    }

    /**
     *
     *
     * @param a
     * @param m
     * @return
     */
    public static <W,A> Writer<W,A> writer(A a, Monoid<W> m) {
      return new Writer<W,A>(() -> Identity.id(Pair.of(a,m.mempty())), m);
    }

    /**
     *
     *
     * @param w
     * @return
     */
    public static <A,W> F1<A,Writer<W,A>> writer(Monoid<W> w) {
      return a -> writer(a,w);
    }

    /**
     *
     *
     * @param mb
     * @return
     */
    public static <W extends Witness,A> Writer<W,A> asWriter(Monad<Hkt2<Writer.μ, Identity.μ, W>, A> mb) {
      if(mb instanceof WriterT)
        return new Writer<W,A>(asWriterT(mb).runWriterT, asWriterT(mb).monoid);
      else
        return (Writer<W,A>) mb;
    }
  }
}
