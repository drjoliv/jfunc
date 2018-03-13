package me.functional.transformers;

import java.util.function.Function;

import me.functional.data.Maybe;
import me.functional.hkt.Hkt;
import me.functional.hkt.Hkt2;
import me.functional.hkt.Witness;
import me.functional.transformers.MaybeT.μ;
import me.functional.type.Monad;

public class MaybeT<M extends Witness,A> implements Monad<Hkt<MaybeT.μ,M>,A>, Hkt2<MaybeT.μ,M,A> {

  private final Monad<M,Maybe<A>> runMaybeT;

  @Override
  public <B> MaybeT<M,B> fmap(Function<? super A, B> fn){
    return of(runMaybeT.fmap(maybe_value -> maybe_value.fmap(fn)));
  }

  @Override
  public <B> MaybeT<M,B> semi(Monad<Hkt<μ, M>, B> mb) {
    return mBind(a -> mb);
  }

  @Override
  public <B> MaybeT<M,B> mBind(Function<? super A, ? extends Monad<Hkt<μ, M>, B>> fn) {
    return of(runMaybeT.mBind(maybe_value -> {
     if(maybe_value.isSome()) {
       return narrow(fn.apply(maybe_value.value())).runMaybeT;
     }
     else
       return runMaybeT.mUnit(Maybe.nothing());
   }));
  }

  public static class μ implements Witness{}

  protected MaybeT(Monad<M,Maybe<A>> runMaybeT) {
    this.runMaybeT = runMaybeT;  
  }

  public static <M extends Witness,A> MaybeT<M,A> lift(Monad<M,A> m) {
    return of(m.mBind(a -> m.mUnit(Maybe.of(a))));
  }

  public static <M extends Witness,A> MaybeT<M,A> of(Monad<M,Maybe<A>> innerMonad) {
    return new MaybeT<M,A>(innerMonad);
  }

  public Monad<M,Maybe<A>> inner() {
    return runMaybeT;
  }

  @Override
  public <B> MaybeT<M,B> mUnit(B b) {
    return of(runMaybeT.mUnit(Maybe.of(b)));
  }
  
  public <B> Function<B,MaybeT<M,B>> unit() {
    return b -> of(runMaybeT.mUnit(Maybe.of(b)));
  }

  public static <M extends Witness,A> MaybeT<M,A> narrow(Monad<Hkt<MaybeT.μ, M>, A> wider) {
    return (MaybeT<M,A>) wider;
  }
}
