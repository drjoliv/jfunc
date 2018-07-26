package drjoliv.jfunc.trans.maybe;

import drjoliv.jfunc.applicative.Applicative;
import drjoliv.jfunc.applicative.ApplicativeFactory;
import drjoliv.jfunc.contorl.maybe.Maybe;
import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.function.F2;
import drjoliv.jfunc.hkt.Hkt;
import drjoliv.jfunc.hkt.Hkt2;
import drjoliv.jfunc.monad.Monad;
import drjoliv.jfunc.monad.MonadFactory;

/**
 * Transformer of Maybe.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public class MaybeT<N ,A> implements Monad<Hkt<maybet,N>, A> {

  private final Monad<N,Maybe<A>> runMaybeT;
  private final MonadFactory<N> innerMonadfactory;
  private final MonadFactory<Hkt<maybet,N>> factory;
  
  public MaybeT(Monad<N, Maybe<A>> runMaybeT, MonadFactory<N> innerMonadfactory,  MonadFactory<Hkt<maybet,N>> factory) {
    this.runMaybeT = runMaybeT;
    this.innerMonadfactory = innerMonadfactory;
    this.factory = factory;
  }
  
  @Override
  public <B> MaybeT<N, B> map(F1<? super A, ? extends B> fn) {
    return new MaybeT<>(runMaybeT.map(m -> m.map(fn)), innerMonadfactory, factory);
  }
  
  @Override
  public <B> MaybeT<N, B> apply(
    Applicative<Hkt<maybet, N>, ? extends F1<? super A, ? extends B>> f) {
    Monad<N, Maybe<F1<? super A, B>>> runMaybeTF = ((MaybeT<N, F1<? super A, B>>)f).runMaybeT;
    Monad<N,Maybe<B>> runMaybeTB =
      Monad.liftM2(runMaybeT, runMaybeTF, (mv, mf) ->  mv.apply(mf));
    return new MaybeT<N,B>(runMaybeTB, innerMonadfactory, factory);
  }
  
  @Override
  public <B> MaybeT<N, B> bind(F1<? super A, ? extends Monad<Hkt<maybet, N>, B>> fn) {
    Monad<N,Maybe<B>> mb = runMaybeT.bind(m -> {
     return m.visit( n -> innerMonadfactory.unit(Maybe.nothing())
                   , s -> ((MaybeT<N,B>)fn.call(s)).runMaybeT);
    });
    return new MaybeT<>(mb,innerMonadfactory, factory);
  }
  
  @Override
  public <B> Monad<Hkt<maybet, N>, B> semi(Monad<Hkt<maybet, N>, B> mb) {
    return bind(a -> mb);
  }
  
  @Override
  public ApplicativeFactory<Hkt<maybet, N>> pure() {
    return factory;
  }
  
  @Override
  public MonadFactory<Hkt<maybet, N>> yield() {
    return factory;
  }
}
