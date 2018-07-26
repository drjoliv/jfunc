package drjoliv.jfunc.contorl.trys;

import drjoliv.jfunc.applicative.Applicative;
import drjoliv.jfunc.applicative.ApplicativeFactory;
import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.function.F2;
import drjoliv.jfunc.hkt.Hkt;
import drjoliv.jfunc.hkt.Hkt2;
import drjoliv.jfunc.monad.Monad;
import drjoliv.jfunc.monad.MonadFactory;

public class TryTMonadFactory<M> implements MonadFactory<Hkt<TryTMonadFactory.μ,M>>{

  private final MonadFactory<M> underlyingFactory;;

  public static class μ {private μ(){}}

  private TryTMonadFactory(MonadFactory<M> underlyingFactory) {
    this.underlyingFactory = underlyingFactory;
  }

  @Override
  public <A> Monad<Hkt<μ, M>, A> unit(A a) {
    return new TryT<>(underlyingFactory.unit(Try.success(a)), underlyingFactory, this);
  }

  @Override
  public <A> Applicative<Hkt<μ, M>, A> pure(A a) {
    return new TryT<>(underlyingFactory.unit(Try.success(a)), underlyingFactory, this);
  }

  public <A> TryT<M,A> lift(Monad<M,A> innerMonad) {
    return new TryT<>(innerMonad.map(Try::success), underlyingFactory, this);
  }

  public <A> TryT<M,A> tryt(Monad<M,Try<A>> innerMonad) {
    return new TryT<>(innerMonad, underlyingFactory, this);
  }

  /**
   * The monad transformer for Try.
   */
  public class TryT<N,A> implements Monad<Hkt<TryTMonadFactory.μ,N>, A>, Hkt2<TryTMonadFactory.μ,N,A> {


    private final Monad<N,Try<A>> runTryT;
    private final MonadFactory<N> innerMonadfactory;
    private final TryTMonadFactory<N> factory;

    private TryT(Monad<N,Try<A>> runTryT, MonadFactory<N> innerMonadfactory, TryTMonadFactory<N> factory) {
      this.runTryT = runTryT;
      this.innerMonadfactory = innerMonadfactory;
      this.factory = factory;
    }

    @Override
    public <B> TryT<N, B> apply(
        Applicative<Hkt<μ, N>, ? extends F1<? super A, ? extends B>> applicative) {
          Monad<N, Try<F1<? super A, B>>> f = ((TryT<N,F1<? super A, B>>)applicative).runTryT();
          Monad<N, Try<A>> v = runTryT();
          F2< Try<F1<? super A, B>>, Try<A>, Try<B>> go = (tf, t) -> t.apply(tf);
          Monad<N,Try<B>> mb = Monad.liftM2(f, v, go);

         return new TryT<>(mb, innerMonadfactory, factory);
    }

    @Override
    public ApplicativeFactory<Hkt<μ, N>> pure() {
      return factory;
    }

    @Override
    public <B> TryT<N,B> map(F1<? super A, ? extends B> fn) {
      Monad<N,Try<B>> fb = runTryT.map(t -> t.map(fn));
      return new TryT<N,B>(fb, innerMonadfactory, factory); 
    }

    @Override
    public <B> TryT<N,B> bind(F1<? super A, ? extends Monad<Hkt<μ, N>, B>> fn) {
      Monad<N,Try<B>> mb = runTryT.bind(ma -> {
        return ma.run()
          .match(l -> innerMonadfactory.unit(Try.failure(l.value()))
               , r -> monad(fn.call(r.value())).runTryT());
      });

      return new TryT<N,B>(mb, innerMonadfactory, factory);
    }

    @Override
    public <B> TryT<N,B> semi(Monad<Hkt<μ, N>, B> mb) {
      return bind(a -> mb);
    }

    @Override
    public MonadFactory<Hkt<μ, N>> yield() {
      return factory;
    }

    /**
    * Returns the inner monad containing a try.
    * @return the inner monad containing a try.
    */
    public Monad<N,Try<A>> runTryT() {
      return runTryT;
    }
  }


    /**
     * A helper function to convert/narrow a reference from an applicative to its underlying type.
     * @param f the applicative to be casted to its original type.
     * @return a tryT.
     */
    public static <N, B> TryTMonadFactory<N>.TryT<N, B> applicative(Applicative<Hkt<μ, N>, B> f) {
      return (TryTMonadFactory<N>.TryT<N, B>) f;
    }

   /**
     * A helper function to convert/narrow a reference from a monad to its underlying type.
    * @param monad the monad to be casted to its original type.
    * @return a tryT.
    */
    public static <N, B> TryTMonadFactory<N>.TryT<N, B> monad(Monad<Hkt<μ, N>, B> monad) {
      return (TryTMonadFactory<N>.TryT<N, B>) monad;
    }
}
