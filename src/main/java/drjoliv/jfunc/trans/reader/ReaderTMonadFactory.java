package drjoliv.jfunc.trans.reader;

import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.hkt.Hkt2;
import drjoliv.jfunc.monad.Monad;
import drjoliv.jfunc.monad.MonadFactory;
import drjoliv.jfunc.trans.reader.ReaderT.ReaderTImpl;

public class ReaderTMonadFactory<M,R> implements MonadFactory<Hkt2<readert,M,R>> {

  private final MonadFactory<M> innerMonadFactory;

  ReaderTMonadFactory(MonadFactory<M> innerMonadFactory) {
    this.innerMonadFactory = innerMonadFactory;
  }

  public static <M, R> ReaderTMonadFactory<M,R> readerTFactory(MonadFactory<M> innerMonadFactory) {
    return new ReaderTMonadFactory(innerMonadFactory);
  }

  @Override
  public <A> ReaderT<M,R,A> unit(A a) {
    return new ReaderTImpl<>(r -> innerMonadFactory.unit(a), innerMonadFactory, this);
  }

  @Override
  public <A> ReaderT<M,R,A> pure(A a) {
    return new ReaderTImpl<>(r -> innerMonadFactory.unit(a), innerMonadFactory, this);
  }

  public <A> ReaderT<M,R,A> lift(Monad<M,A> ma) {
    return new ReaderTImpl<>(r -> ma, innerMonadFactory, this);
  }

  public ReaderT<M,R,R> ask() {
    return new ReaderTImpl<>(r -> innerMonadFactory.unit(r), innerMonadFactory, this);
  }

  public <A> ReaderT<M,R,A> of(F1<R,Monad<M,A>> fn) {
    return new ReaderTImpl<>(fn, innerMonadFactory, this);
  }
}
