package drjoliv.jfunc.trans.reader;

import drjoliv.jfunc.applicative.Applicative;
import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.function.F2;
import drjoliv.jfunc.function.F3;
import drjoliv.jfunc.function.F4;
import drjoliv.jfunc.function.F5;
import drjoliv.jfunc.hkt.Hkt2;
import drjoliv.jfunc.monad.Identity;
import drjoliv.jfunc.monad.Identity.μ;
import drjoliv.jfunc.monad.Monad;
import drjoliv.jfunc.monad.MonadFactory;

public abstract class Reader<R,A> extends ReaderT<Identity.μ, R, A> {

  Reader(MonadFactory<Identity.μ> innerMonadFactory, MonadFactory<Hkt2<readert, Identity.μ, R>> readerTFactory) {
    super(innerMonadFactory, readerTFactory);
  }

  @Override
  public abstract <B> Reader<R,B> apply(Applicative<Hkt2<readert,Identity.μ,R>, ? extends F1<? super A, ? extends B>> app);

  @Override
  public abstract <B> Reader<R, B> bind(F1<? super A, ? extends Monad<Hkt2<readert, μ, R>, B>> fn);

  @Override
  public abstract Identity<A> call(R r);

  public abstract A run(R r);

  @Override
  public abstract <B> Reader<R, B> map(F1<? super A, ? extends B> fn);

  @Override
  public abstract <B> Reader<R, B> semi(Monad<Hkt2<readert, μ, R>, B> mb);

  public static <R,A> Reader<R,A> reader(F1<R,A> fn) {
    return ReaderMonadFactory.<R>instance().of(fn.then(Identity::id));
  }

  public static <R,A> Reader<R,R> ask() {
    return reader(F1.<R>identity());
  }

  @Override
  public abstract <B, C> Reader<R, C> For(F1<? super A, ? extends Monad<Hkt2<readert, μ, R>, B>> fn,
      F2<? super A, ? super B, ? extends Monad<Hkt2<readert, μ, R>, C>> fn2);

  @Override
  public abstract <B, C, D> Reader<R, D> For(F1<? super A, ? extends Monad<Hkt2<readert, μ, R>, B>> fn,
      F2<? super A, ? super B, ? extends Monad<Hkt2<readert, μ, R>, C>> fn2,
      F3<? super A, ? super B, ? super C, ? extends Monad<Hkt2<readert, μ, R>, D>> fn3);

  @Override
  public abstract <B, C, D, E> Reader<R, E> For(F1<? super A, ? extends Monad<Hkt2<readert, μ, R>, B>> fn,
      F2<? super A, ? super B, ? extends Monad<Hkt2<readert, μ, R>, C>> fn2,
      F3<? super A, ? super B, ? super C, ? extends Monad<Hkt2<readert, μ, R>, D>> fn3,
      F4<? super A, ? super B, ? super C, ? super D, ? extends Monad<Hkt2<readert, μ, R>, E>> fn4);

  @Override
  public abstract <B, C> Reader<R, F1<B, C>> map(F2<? super A, B, C> fn);

  @Override
  public abstract <B, C, D> Reader<R, F1<B, F1<C, D>>> map(F3<? super A, B, C, D> fn);

  @Override
  public abstract <B, C, D, E> Reader<R, F1<B, F1<C, F1<D, E>>>> map(F4<? super A, B, C, D, E> fn);

  @Override
  public abstract <B, C, D, E, G> Reader<R, F1<B, F1<C, F1<D, F1<E, G>>>>> map(F5<? super A, B, C, D, E, G> fn);

  static class ReaderImpl<R,A> extends Reader<R,A> {

    private final ReaderT<Identity.μ, R, A> readert;

    ReaderImpl(ReaderT<Identity.μ, R, A> readert) {
      super(readert.getInnerMonadFactory(), readert.yield());
      this.readert = readert;
    }

    @Override
    public <B> Reader<R,B> apply(Applicative<Hkt2<readert,Identity.μ,R>, ? extends F1<? super A, ? extends B>> app) {
      return new ReaderImpl<>(readert.apply(app));
    }

    @Override
    public <B> Reader<R, B> bind(F1<? super A, ? extends Monad<Hkt2<readert, μ, R>, B>> fn) {
      return new ReaderImpl<>(readert.bind(fn));
    }

    @Override
    public Identity<A> call(R r) {
      return Identity.monad(readert.call(r));
    }

    @Override
    public <B> Reader<R, B> map(F1<? super A, ? extends B> fn) {
      return new ReaderImpl(readert.map(fn));
    }

    @Override
    public <B> Reader<R, B> semi(Monad<Hkt2<readert, μ, R>, B> mb) {
      return bind(a -> mb);
    }

    @Override
    <B> ReaderT<μ, R, B> doBind(R r, F1<? super A, ? extends Monad<Hkt2<readert, μ, R>, B>> fn) {
      throw new UnsupportedOperationException();
    }

    @Override
    ReaderT<μ, R, A> step(R r) {
      throw new UnsupportedOperationException();
    }

    @Override
    public A run(R r) {
      return call(r).value();
    }

    @Override
    public <B, C> Reader<R, C> For(F1<? super A, ? extends Monad<Hkt2<readert, μ, R>, B>> fn,
        F2<? super A, ? super B, ? extends Monad<Hkt2<readert, μ, R>, C>> fn2) {
      return new ReaderImpl<>(readert.For(fn,fn2));
    }

    @Override
    public <B, C, D> Reader<R, D> For(F1<? super A, ? extends Monad<Hkt2<readert, μ, R>, B>> fn,
        F2<? super A, ? super B, ? extends Monad<Hkt2<readert, μ, R>, C>> fn2,
        F3<? super A, ? super B, ? super C, ? extends Monad<Hkt2<readert, μ, R>, D>> fn3) {
      return new ReaderImpl<>(readert.For(fn, fn2, fn3));
    }

    @Override
    public <B, C, D, E> Reader<R, E> For(F1<? super A, ? extends Monad<Hkt2<readert, μ, R>, B>> fn,
        F2<? super A, ? super B, ? extends Monad<Hkt2<readert, μ, R>, C>> fn2,
        F3<? super A, ? super B, ? super C, ? extends Monad<Hkt2<readert, μ, R>, D>> fn3,
        F4<? super A, ? super B, ? super C, ? super D, ? extends Monad<Hkt2<readert, μ, R>, E>> fn4) {
      return new ReaderImpl<>(readert.For(fn, fn2, fn3, fn4));
    }

    @Override
    public <B, C> Reader<R, F1<B, C>> map(F2<? super A, B, C> fn) {
      return new ReaderImpl<>(readert.map(fn));
    }

    @Override
    public <B, C, D> Reader<R, F1<B, F1<C, D>>> map(F3<? super A, B, C, D> fn) {
      return new ReaderImpl<>(readert.map(fn));
    }

    @Override
    public <B, C, D, E> Reader<R, F1<B, F1<C, F1<D, E>>>> map(F4<? super A, B, C, D, E> fn) {
      return new ReaderImpl<>(readert.map(fn));
    }

    @Override
    public <B, C, D, E, G> Reader<R, F1<B, F1<C, F1<D, F1<E, G>>>>> map(F5<? super A, B, C, D, E, G> fn) {
      return new ReaderImpl<>(readert.map(fn));
    }
  }
}
