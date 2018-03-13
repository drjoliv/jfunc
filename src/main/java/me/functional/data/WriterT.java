package me.functional.data;

import java.util.function.Function;

import me.functional.data.WriterT.μ;
import me.functional.hkt.Hkt;
import me.functional.hkt.Hkt2;
import me.functional.hkt.Hkt3;
import me.functional.hkt.Witness;

public class WriterT <M extends Witness,W,A> implements Monad<Hkt2<WriterT.μ,M,W>,A>, Hkt3<WriterT.μ,M,W,A>{

  private final Monad<M,Pair<A,W>> runWriterT;
  private final Monoid<W> monoid;

  private WriterT(Monad<M,Pair<A,W>> runWriterT, Monoid<W> monoid) {
    this.runWriterT = runWriterT;
    this.monoid     = monoid;
  }

  @Override
  public <B> WriterT<M,W,B> fmap(Function<? super A, B> fn) {
    return new WriterT<M,W,B>(runWriterT.fmap(p -> Pair.of(fn.apply(p.fst),p.snd))
        ,monoid);
  }

  @Override
  public <B> WriterT<M,W,B> mBind(Function<? super A, ? extends Monad<Hkt2<μ, M, W>, B>> fn) {
    return new WriterT<M,W,B>(Monad.For(runWriterT
        , p     -> asWriterT(fn.apply(p.fst)).runWriterT
        ,(p,p2) -> runWriterT.mUnit(Pair.of(p2.fst,monoid.mappend(p.snd,p2.snd))))
    , monoid);
  }

  @Override
  public <B> WriterT<M,W,B> mUnit(B b) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <B> WriterT<M,W,B> semi(Monad<Hkt2<μ, M, W>, B> mb) {
    return mBind(a -> mb);
  }

  public Monad<M,Pair<A,W>> runWriter(){
    return runWriterT;
  }

  public static class μ implements Witness{}

  public static final class Writer<W,A> extends WriterT<Identity.μ,W,A> {
    private Writer(Monad<Identity.μ,Pair<A,W>> runWriterT, Monoid<W> monoid) {
      super(runWriterT, monoid);
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
    public <B> Writer<W,B> mUnit(B b) {
      final WriterT<Identity.μ,W,B> w = super.mUnit(b);
      return new Writer<W,B>(w.runWriterT,w.monoid);
    }

    @Override
    public <B> Writer<W,B> semi(
      final Monad<Hkt2<μ, me.functional.data.Identity.μ, W>, B> mb) {
      final WriterT<Identity.μ,W,B> w = super.semi(mb);
      return new Writer<W,B>(w.runWriterT,w.monoid);
    }

    @Override
    public Identity<Pair<A, W>> runWriter() {
      return (Identity<Pair<A, W>>)super.runWriter();
    }

    public Pair<A,W> run() {
      return runWriter().value();
    }
  }

  public static <M extends Witness,W,A> WriterT<M,W,A> asWriterT(Monad<Hkt2<Writer.μ, M, W>, A> mb) {
    return (WriterT<M,W,A>) mb;
  }

  public static <W extends Witness,A> Writer<W,A> asWriter(Monad<Hkt2<Writer.μ, Identity.μ, W>, A> mb) {
    if(mb instanceof WriterT)
      return new Writer<W,A>(asWriterT(mb).runWriterT, asWriterT(mb).monoid);
    else
      return (Writer<W,A>) mb;
  }

  public static <W,A> Writer<W,A> writer(A a,W w, Monoid<W> m) {
    return new Writer<W,A>(Identity.of(Pair.of(a,w)), m);
  }

  public static <W,A> Writer<W,A> writer(A a,Monoid<W> m) {
    return new Writer<W,A>(Identity.of(Pair.of(a,m.mempty())), m);
  }

  public static <A,W> Function<A,Writer<W,A>> unit(Monoid<W> w) {
    return a -> writer(a,w);
  }
}
