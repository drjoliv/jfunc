package me.functional.transformers;

import static me.functional.data.Try.failure;

import java.util.function.Function;
import java.util.function.Supplier;

import me.functional.data.Either;
import me.functional.data.Try;
import me.functional.hkt.Hkt;
import me.functional.hkt.Hkt2;
import me.functional.hkt.Witness;
import me.functional.type.Monad;
import me.functional.type.MonadUnit;
import static me.functional.data.Try.*;

public class TryT<M extends Witness,A> implements Monad<Hkt<TryT.μ,M>, A>, Hkt2<TryT.μ,M,A> {

  public static class μ implements Witness{}

  private final Supplier<Monad<M,Try<A>>> runTryT;
  private final MonadUnit<M> mUnit;

  public Monad<M,Try<A>> runTryT() {
    return runTryT.get();  
  }

  private TryT(Try<A> t, MonadUnit<M> mUnit) {
    this.runTryT = () -> mUnit.unit(t);
    this.mUnit   = mUnit;
  }

  private TryT(Monad<M,Try<A>> t) {
    this.runTryT = () -> t;
    this.mUnit   = t.yield();
  }

  private TryT(Supplier<Monad<M,Try<A>>> runTryT, MonadUnit<M> mUnit) {
    this.runTryT = runTryT;
    this.mUnit   = mUnit;
  }

  public static <M extends Witness, B> TryT<M,B> tryT(B b, MonadUnit<M> mUnit) {
    return new TryT<>(success(b), mUnit);
  }

  public static <M extends Witness, B> TryT<M,B> tryT(Monad<M,Try<B>> t) {
    return new TryT<>(t);
  }

  public static <M extends Witness, B> TryT<M,B> lift(Monad<M,B> t) {
    return new TryT<M,B>(t.fmap(b -> success(b)));
  }

  @Override
  public <B> TryT<M,B> fmap(Function<? super A, B> fn) {
    return new TryT<>(() -> runTryT.get().fmap(t -> t.fmap(fn)), mUnit); 
  }

  @Override
  public <B> TryT<M,B> mBind(Function<? super A, ? extends Monad<Hkt<μ, M>, B>> fn) {
    return new TryT<M,B>(() -> {
     Monad<M,Try<B>> m = runTryT.get().mBind(t -> {
       Either<Exception, Supplier<Monad<M,Try<B>>>> e =
         t.fmap(fn.andThen(TryT::asTryT))
         .run()
         .right()
         .fmap(tryT -> tryT.runTryT)
         .either();
       if(e.isRight())
         return e.valueR().get();
       else
         return mUnit.unit(failure(e.valueL()));
     });
        return m; 
     },mUnit);
  }

  @Override
  public <B> TryT<M,B> semi(Monad<Hkt<μ, M>, B> mb) {
    return mBind(a -> mb);
  }

  @Override
  public MonadUnit<Hkt<μ, M>> yield() {
    return new MonadUnit<Hkt<μ, M>>() {
      @Override
      public <B> Monad<Hkt<μ, M>, B> unit(B b) {
        return new TryT<M,B>(mUnit.unit(success(b)));
      }
    };
  }

  public static <M extends Witness, B> TryT<M, B> asTryT(Monad<Hkt<μ, M>, B> monad) {
    return (TryT<M, B>) monad;
  }
}
