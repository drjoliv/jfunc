package drjoliv.fjava.trans.maybe;

import drjoliv.fjava.adt.Maybe;
import drjoliv.fjava.applicative.Applicative;
import drjoliv.fjava.applicative.ApplicativePure;
import drjoliv.fjava.functions.F1;
import drjoliv.fjava.functions.F2;
import drjoliv.fjava.hkt.Hkt;
import drjoliv.fjava.hkt.Hkt2;
import drjoliv.fjava.hkt.Witness;
import drjoliv.fjava.monad.Monad;
import drjoliv.fjava.monad.MonadUnit;

/**
 * Transformer of Maybe.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public class MaybeT<M extends Witness,A> implements Monad<Hkt<MaybeT.μ,M>,A>, Hkt2<MaybeT.μ,M,A> {

  public static class μ implements Witness{private μ(){}}

  private final Monad<M,Maybe<A>> runMaybeT;
  private final MonadUnit<M> mUnit;

  @Override
  public <B> MaybeT<M, B> apply(
    Applicative<Hkt<μ, M>, ? extends F1<? super A, ? extends B>> f) {
      Monad<M, Maybe<F1<? super A, B>>> runMaybeTF = ((MaybeT<M, F1<? super A, B>>)f).runMaybeT;
      Monad<M,Maybe<B>> runMaybeTB =
        Monad.liftM2(runMaybeT, runMaybeTF, (mv, mf) ->  mv.apply(mf));
      return new MaybeT<M,B>(runMaybeTB);
  }

  @Override
  public ApplicativePure<Hkt<μ, M>> pure() {
    return new ApplicativePure<Hkt<μ, M>>() {
      @Override
      public <B> Applicative<Hkt<μ, M>, B> pure(B b) {
        return MaybeT.<M,B>maybeT().call(mUnit,b);
      }
    };
  }

  @Override
  public <B> MaybeT<M,B> map(F1<? super A, ? extends B> fn){
    return maybeT(runMaybeT.map(maybe_value -> maybe_value.map(fn)));
  }

  @Override
  public <B> MaybeT<M,B> semi(Monad<Hkt<μ, M>, B> mb) {
    return bind(a -> mb);
  }

  @Override
  public <B> MaybeT<M,B> bind(F1<? super A, ? extends Monad<Hkt<μ, M>, B>> fn) {
    return maybeT(runMaybeT.bind(maybe_value -> {
      return maybe_value.match(
          n -> mUnit.unit(Maybe.nothing())
        , j -> monad(fn.call(j.value())).runMaybeT);
   }));
  }

  @Override
  public MonadUnit<Hkt<μ, M>> yield() {
    return new MonadUnit<Hkt<μ, M>>() {
      @Override
      public <B> Monad<Hkt<μ, M>, B> unit(B b) {
        return MaybeT.<M,B>maybeT().call(mUnit,b);
      }
    };
  }

  private MaybeT(Monad<M,Maybe<A>> runMaybeT) {
    this.runMaybeT = runMaybeT;  
    this.mUnit = runMaybeT.yield();
  }

  public static <M extends Witness,A> MaybeT<M,A> lift(Monad<M,A> m) {
    return new MaybeT<M,A>(m.map(a -> Maybe.maybe(a)));
  }

  public static <M extends Witness, A> F2<MonadUnit<M>,A,MaybeT<M,A>> maybeT() {
    return (mUnit, a) -> new MaybeT<M, A>(mUnit.unit(Maybe.maybe(a)));
  }

  public static <M extends Witness,A> MaybeT<M,A> maybeT(Monad<M,Maybe<A>> runMaybeT) {
    return new MaybeT<M,A>(runMaybeT);
  }

  public static <M extends Witness,A> MaybeT<M,A> maybeT(A a, MonadUnit<M> mUnit) {
    return lift(mUnit.unit(a));
  }

  public Monad<M,Maybe<A>> runMaybeT() {
    return runMaybeT;
  }

  public static <M extends Witness, A> MaybeT<M, A> monad(Monad<Hkt<MaybeT.μ, M>, A> wider) {
    return (MaybeT<M, A>) wider;
  }
}
