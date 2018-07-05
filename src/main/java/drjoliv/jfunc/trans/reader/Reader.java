package drjoliv.jfunc.trans.reader;

import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.hkt.Hkt2;
import drjoliv.jfunc.monad.Identity;
import drjoliv.jfunc.monad.Monad;

public class Reader<R,A> extends ReaderT<Identity.μ,R,A> {

  private final ReaderT<Identity.μ,R,A> readerT;

  private Reader(F1<R,A> fn) {
    super(Identity.MONAD_UNIT);
    this.readerT = ReaderT.reader(fn.then(Identity::id), Identity.MONAD_UNIT);
  }

  private Reader(ReaderT<Identity.μ,R,A> readerT) {
    super(readerT.mUnit());
    this.readerT = readerT;
  }

  @Override
  Monad<drjoliv.jfunc.monad.Identity.μ, A> call(R r) {
    return readerT.call(r);
  }

  @Override
  public <B> Reader<R,B> bind(
      F1<? super A, ? extends Monad<Hkt2<μ, drjoliv.jfunc.monad.Identity.μ, R>, B>> fn) {
    return new Reader<>(readerT.bind(fn));
  }

  @Override
  ReaderT<drjoliv.jfunc.monad.Identity.μ, R, A> step(R r) {
    return readerT.step(r);
  }

  @Override
  <B> ReaderT<drjoliv.jfunc.monad.Identity.μ, R, B> doBind(R r,
      F1<? super A, ? extends Monad<Hkt2<μ, drjoliv.jfunc.monad.Identity.μ, R>, B>> fn) {
    return readerT.doBind(r, fn);
  }

  @Override
  public Identity<A> runReader(R r) {
    return Identity.monad(readerT.runReader(r));
  }

  public A run(R r) {
    return runReader(r).value();
  }

  public static <R,A> Reader<R,A> reader(F1<R,A> fn) {
    return new Reader<>(fn);
  }

  public static <R> Reader<R,R> ask() {
    return new Reader<>(r -> r);
  }

  @Override
  public <B> Reader<R,B> map(F1<? super A, ? extends B> fn) {
    return new Reader<>(readerT.map(fn));
  }

  @Override
  public <B> Reader<R,B> semi(
      Monad<Hkt2<μ, drjoliv.jfunc.monad.Identity.μ, R>, B> mb) {
    return new Reader<>(readerT.semi(mb));
  }

  public static <R,B> Reader<R,B> asReader(Monad<Hkt2<μ, drjoliv.jfunc.monad.Identity.μ, R>, B> mb) {
    if(mb instanceof Reader)
      return (Reader<R,B>) mb;
    else
      return new Reader<R,B>(ReaderT.monad(mb));
  }
}
