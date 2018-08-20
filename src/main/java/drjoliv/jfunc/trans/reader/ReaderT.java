package drjoliv.jfunc.trans.reader;

import drjoliv.jfunc.applicative.Applicative;
import drjoliv.jfunc.applicative.ApplicativeFactory;
import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.function.F2;
import drjoliv.jfunc.function.F3;
import drjoliv.jfunc.function.F4;
import drjoliv.jfunc.function.F5;
import drjoliv.jfunc.hkt.Hkt2;
import drjoliv.jfunc.hkt.Hkt3;
import drjoliv.jfunc.monad.Monad;
import drjoliv.jfunc.monad.MonadFactory;

public abstract class ReaderT<M,R,A> implements Monad<Hkt2<readert,M,R>,A>, Hkt3<readert,M,R,A> {

  private final MonadFactory<M> innerMonadFactory;
  private final MonadFactory<Hkt2<readert,M,R>> readerTFactory;

  ReaderT(MonadFactory<M> innerMonadFactory, MonadFactory<Hkt2<readert,M,R>> readerTFactory) {
    this.innerMonadFactory = innerMonadFactory;
    this.readerTFactory = readerTFactory;
  }

  public abstract Monad<M,A> call(R r);

  public F1<R,Monad<M,A>> runReader() {
    return this::call;
  }

  public <B> ReaderT<M,R,B> apply(Applicative<Hkt2<readert,M,R>, ? extends F1<? super A, ? extends B>> app) {
    return ((ReaderT<M,R,F1<? super A, ? extends B>>)app).bind(f -> map(f));
  }


  @Override
  public <B> ReaderT<M,R,B> bind(F1<? super A, ? extends Monad<Hkt2<readert, M, R>, B>> fn) {
    return new ReaderTBind<M,R,A,B>(this, fn, getInnerMonadFactory() , yield());
  }

  @Override
  public <B> ReaderT<M,R,B> map(F1<? super A, ? extends B> fn) {
    return bind(a -> new ReaderTImpl<>(env -> getInnerMonadFactory().unit(fn.call(a))
          , getInnerMonadFactory(), yield()));
  }

  @Override
  public <B> ReaderT<M,R,B> semi(Monad<Hkt2<readert, M, R>, B> mb) {
    return bind(a -> mb);
  }

  @Override
  public MonadFactory<Hkt2<readert, M, R>> yield() {
    return readerTFactory;
  }

  @Override
  public ApplicativeFactory<Hkt2<readert, M, R>> pure() {
    return readerTFactory;
  }

  final Monad<M,A> runReader(R r) {
   ReaderT reader = this;
    while(reader instanceof ReaderTBind) {
      reader = ((ReaderTBind)reader).step(r);
    }
    return (Monad<M,A>)reader.call(r);
  }

  abstract ReaderT<M,R,A> step(R r);

  abstract <B> ReaderT<M,R,B> doBind(R r, F1<? super A, ? extends Monad<Hkt2<readert, M, R>, B>> fn);


  MonadFactory<M> getInnerMonadFactory() {
    return innerMonadFactory;
  }

  @Override
  public <B, C> ReaderT<M,R,C> For(F1<? super A, ? extends Monad<Hkt2<readert, M, R>, B>> fn,
      F2<? super A, ? super B, ? extends Monad<Hkt2<readert, M, R>, C>> fn2) {
    return (ReaderT<M,R,C>)Monad.super.For(fn, fn2);
  }

  @Override
  public <B, C, D> ReaderT<M, R, D> For(F1<? super A, ? extends Monad<Hkt2<readert, M, R>, B>> fn,
      F2<? super A, ? super B, ? extends Monad<Hkt2<readert, M, R>, C>> fn2,
      F3<? super A, ? super B, ? super C, ? extends Monad<Hkt2<readert, M, R>, D>> fn3) {
    return (ReaderT<M, R, D>)Monad.super.For(fn, fn2, fn3);
  }

  @Override
  public <B, C, D, E> ReaderT<M, R, E> For(F1<? super A, ? extends Monad<Hkt2<readert, M, R>, B>> fn,
      F2<? super A, ? super B, ? extends Monad<Hkt2<readert, M, R>, C>> fn2,
      F3<? super A, ? super B, ? super C, ? extends Monad<Hkt2<readert, M, R>, D>> fn3,
      F4<? super A, ? super B, ? super C, ? super D, ? extends Monad<Hkt2<readert, M, R>, E>> fn4) {
    return (ReaderT<M, R, E>)Monad.super.For(fn, fn2, fn3, fn4);
  }

  @Override
  public <B, C> ReaderT<M, R, F1<B, C>> map(F2<? super A, B, C> fn) {
    return (ReaderT<M, R, F1<B, C>>)Monad.super.map(fn);
  }

  @Override
  public <B, C, D> ReaderT<M, R, F1<B, F1<C, D>>> map(F3<? super A, B, C, D> fn) {
    return (ReaderT<M, R, F1<B, F1<C, D>>>)Monad.super.map(fn);
  }

  @Override
  public <B, C, D, E> ReaderT<M, R, F1<B, F1<C, F1<D, E>>>> map(F4<? super A, B, C, D, E> fn) {
    return (ReaderT<M, R, F1<B, F1<C, F1<D, E>>>>)Monad.super.map(fn);
  }

  @Override
  public <B, C, D, E, G> ReaderT<M, R, F1<B, F1<C, F1<D, F1<E, G>>>>> map(
      F5<? super A, B, C, D, E, G> fn) {
    return (ReaderT<M, R, F1<B, F1<C, F1<D, F1<E, G>>>>>)Monad.super.map(fn);
  }

  static class ReaderTImpl<M,R,A> extends ReaderT<M,R,A> {
  
    private final F1<R,Monad<M,A>> fn;
  
    ReaderTImpl(F1<R, Monad<M, A>> fn, MonadFactory<M> mUnit, MonadFactory<Hkt2<readert,M,R>> readerTFactory) {
      super(mUnit, readerTFactory);
      this.fn = fn;
    }
  
    @Override
    public Monad<M,A> call(R r) {
      return fn.call(r);
    }
  
    @Override
    <B> ReaderT<M, R, B> doBind(R r, F1<? super A, ? extends Monad<Hkt2<readert, M, R>, B>> fn) {
        Monad<M,A> ma = this.fn.call(r);
        Monad<M,B> mab = Monad.join(ma.map(a -> ((ReaderT<M,R,B>)fn.call(a)).call(r) ));
        return new ReaderTImpl<M,R,B>(env -> mab, getInnerMonadFactory(), yield());
    }
  
    @Override ReaderT<M, R, A> step(R r) { return this; }
  }

  private static class ReaderTBind<M,R,A,B> extends ReaderT<M,R,B>  {
  
    private final ReaderT<M,R,A> reader;
    private final F1<? super A, ? extends Monad<Hkt2<readert,M, R>, B>> fn;//[A -> R -> B]
  
    ReaderTBind(ReaderT<M,R,A> reader, F1<? super A, ? extends Monad<Hkt2<readert,M, R>, B>> binder
        , MonadFactory<M> mUnit, MonadFactory<Hkt2<readert,M,R>> readerTFactory) {
      super(mUnit, readerTFactory);
      this.reader = reader;
      this.fn = binder;
    }
  
    public ReaderT<M,R,B> step(R r) {
      return reader.doBind(r, fn);
    }
  
    public <C> ReaderT<M,R,C> doBind(R r, F1<? super B, ? extends Monad<Hkt2<readert,M, R>, C>> binder) {
      return reader.bind(a -> {
        ReaderT<M, R, B> mb = (ReaderT<M, R, B>)fn.call(a);
        return mb.bind(binder);
      });
    }
  
    @Override
    public Monad<M,B> call(R r) {
      return runReader(r);
    }
  }
}
