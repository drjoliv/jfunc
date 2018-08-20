package drjoliv.jfunc.trans.writer;

import drjoliv.jfunc.hkt.Hkt2;
import drjoliv.jfunc.hlist.T2;
import drjoliv.jfunc.monad.Monad;
import drjoliv.jfunc.monad.MonadFactory;
import drjoliv.jfunc.monoid.Monoid;

public class WriterTMonadFactory<M, W> implements MonadFactory<Hkt2<writert, M, W>> {

  private MonadFactory<M> underlyingFactory;
  private Monoid<W> monoid;

  public static <M,W> WriterTMonadFactory<M, W> writerTFactory(MonadFactory<M> underlyingFactory, Monoid<W> monoid) {
    return new WriterTMonadFactory<>(underlyingFactory, monoid);
  }

  WriterTMonadFactory(MonadFactory<M> underlyingFactory, Monoid<W> monoid) {
    this.underlyingFactory = underlyingFactory;
    this.monoid            = monoid;
  }

  @Override
  public <A> WriterT<M, W, A> unit(A a) {
    return new WriterTImpl<M,W,A>(underlyingFactory.unit(T2.t2(a, monoid.mempty()))
        , monoid, underlyingFactory, this);
  }

  @Override
  public <A> WriterT<M, W, A> pure(A a) {
    return new WriterTImpl<M,W,A>(underlyingFactory.unit(T2.t2(a, monoid.mempty()))
        , monoid, underlyingFactory, this);
  }

  public <A> WriterT<M, W, A> lift(Monad<M,A> innerMonad) {
    return new WriterTImpl<M,W,A>(innerMonad.map(a -> T2.t2(a, monoid.mempty()))
        , monoid, underlyingFactory, this);
  }

  MonadFactory<M> getUnderlyingFactory() {
    return underlyingFactory;
  }

  Monoid<W> getMonoid() {
    return monoid;
  }

}
