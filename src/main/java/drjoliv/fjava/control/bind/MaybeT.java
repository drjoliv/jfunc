package drjoliv.fjava.control.bind;

import drjoliv.fjava.control.Bind;
import drjoliv.fjava.control.BindUnit;
import drjoliv.fjava.data.Maybe;
import static drjoliv.fjava.data.Maybe.*;
import drjoliv.fjava.functions.F1;
import drjoliv.fjava.functions.F2;
import drjoliv.fjava.hkt.Hkt;
import drjoliv.fjava.hkt.Hkt2;
import drjoliv.fjava.hkt.Witness;

public class MaybeT<M extends Witness,A> implements Bind<Hkt<MaybeT.μ,M>,A>, Hkt2<MaybeT.μ,M,A> {

  public static class μ implements Witness{}

  private final Bind<M,Maybe<A>> runMaybeT;
  private final BindUnit<M> mUnit;

  @Override
  public <B> MaybeT<M,B> map(F1<? super A, B> fn){
    return maybeT(runMaybeT.map(maybe_value -> maybe_value.map(fn)));
  }

  @Override
  public <B> MaybeT<M,B> semi(Bind<Hkt<μ, M>, B> mb) {
    return bind(a -> mb);
  }

  @Override
  public <B> MaybeT<M,B> bind(F1<? super A, ? extends Bind<Hkt<μ, M>, B>> fn) {
    return maybeT(runMaybeT.bind(maybe_value -> {
      return maybe_value.match(
          n -> mUnit.unit(nothing())
        , j -> asMaybeT(fn.call(j.value())).runMaybeT);
   }));
  }

  @Override
  public BindUnit<Hkt<μ, M>> yield() {
    return new BindUnit<Hkt<μ, M>>() {
      @Override
      public <B> Bind<Hkt<μ, M>, B> unit(B b) {
        return MaybeT.<M,B>maybeT().call(mUnit,b);
      }
    };
  }


  private MaybeT(Bind<M,Maybe<A>> runMaybeT) {
    this.runMaybeT = runMaybeT;  
    this.mUnit = runMaybeT.yield();
  }

  public static <M extends Witness,A> MaybeT<M,A> liftMaybeT(Bind<M,A> m) {
    return new MaybeT<M,A>(m.map(a -> Maybe.maybe(a)));
  }

  public static <M extends Witness, A> F2<BindUnit<M>,A,MaybeT<M,A>> maybeT() {
    return (mUnit, a) -> new MaybeT<M, A>(mUnit.unit(Maybe.maybe(a)));
  }

  public static <M extends Witness,A> MaybeT<M,A> maybeT(Bind<M,Maybe<A>> runMaybeT) {
    return new MaybeT<M,A>(runMaybeT);
  }

  public static <M extends Witness,A> MaybeT<M,A> maybeT(A a, BindUnit<M> mUnit) {
    return liftMaybeT(mUnit.unit(a));
  }

  public Bind<M,Maybe<A>> runMaybeT() {
    return runMaybeT;
  }

  public static <M extends Witness, A> MaybeT<M, A> asMaybeT(Bind<Hkt<MaybeT.μ, M>, A> wider) {
    return (MaybeT<M, A>) wider;
  }
}
