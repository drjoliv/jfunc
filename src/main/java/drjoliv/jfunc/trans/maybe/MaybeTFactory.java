package drjoliv.jfunc.trans.maybe;

import drjoliv.jfunc.contorl.maybe.Maybe;
import drjoliv.jfunc.hkt.Hkt;
import drjoliv.jfunc.monad.Monad;
import drjoliv.jfunc.monad.MonadFactory;

public class MaybeTFactory<M> implements MonadFactory<Hkt<maybet,M>> {

  private final MonadFactory<M> underlyingFactory;

  public MaybeTFactory(MonadFactory<M> underlyingFactory) {
    this.underlyingFactory = underlyingFactory;
  }

  public <A> MaybeT<M,A> lift(Monad<M,A> ma) {
    return of(ma.map(Maybe::maybe));
  }

  public <A> MaybeT<M,A> of(Monad<M,Maybe<A>> ma) {
    return new MaybeT<M,A>(ma, underlyingFactory, this);
  }

  @Override
  public <A> MaybeT<M,A> unit(A a) {
    return new MaybeT<M,A>(underlyingFactory.unit(Maybe.maybe(a)), underlyingFactory, this);
  }

  @Override
  public <A> MaybeT<M,A> pure(A a) {
    return new MaybeT<M,A>(underlyingFactory.unit(Maybe.maybe(a)), underlyingFactory, this);
  }
}
