package drjoliv.jfunc.monad;

import drjoliv.jfunc.applicative.Applicative;
import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.function.F2;
import drjoliv.jfunc.function.F3;
import drjoliv.jfunc.function.F4;
import drjoliv.jfunc.function.F5;

/**
 * A computation that can be chain with other computations.
 * Each monad determins how computaions are chained allowing for unique sidee effects for different monads when chaining computaitons.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public interface Monad<M, A> extends Applicative<M, A> {

  @Override
  public <B> Monad<M, B> map(F1<? super A, ? extends B> fn);

  @Override
  public <B> Monad<M, B> apply(Applicative<M,? extends F1<? super A, ? extends B>> f);

  /**
   * Chains together this computation with a function that produces computations of this type.
   * @param fn a function that produces monadic computations.
   * @return A computation created from chaining together this computation with a function that produces computations.
   */
  public <B> Monad<M,B> bind(final F1<? super A,? extends Monad<M,B>> fn);

  /**
   * Chains together this computation with the given argument, discarding the value within this monad returning a new monad.
   * @param mb a computation that wil be chained.
   * @return a monad obtained from chaining this monad with the argument.
   */
  public <B> Monad<M,B> semi(Monad<M,B> mb);

  /**
   * A strategy for creating a monad of this type.
   * @return a strategy for creating a monad of this type.
   */
  public MonadFactory<M> yield();

  /**
   * Returns a monad of this type containing the given argument.
   * @param b an argument to be lifted into a monad.
   * @return a monad of this type containing the given argument.
   */
  public default <B> Monad<M,B> unit(B b) {
    return yield().unit(b);
  }

  /**
   * Flattens a monad containing another monad, returning the innner monad.
   * @param monad a monad containing a monad.
   * @return a flattened monad.
   */
  public static <M, A> Monad<M,A> join(Monad<M,? extends Monad<M,A>> monad) {
    return monad.bind(m -> m);
  }

  /**
   * First class function of bind.
   * @return a first class version of bind.
   */
  public static <M, A, B> F2<Monad<M,A>, F1<? super A,? extends Monad<M,B>>, Monad<M,B>> bind() {
    return (m,f) -> m.bind(f);
  }

  /**
   * A first class function of join. 
   * @return a first class function of join.
   */
  public static <M, A> F1<Monad<M,? extends Monad<M,A>>, Monad<M,A>> join() {
    return Monad::join;
  }

  /**
   * Sequences bind operation, each function captures the value of previous compuations.
   * @param monad a monad
   * @param fn a function that produces a monad computation.
   * @return a monad.
   */
  public static <M, A, B> Monad<M,B> For(Monad<M,A> monad, F1<? super A,  ? extends Monad<M,B>> fn) {
    return monad.bind(fn);
  } 

  /**
   * Sequences bind operation starting with this monad.
   * @param fn a function that produces a monad computation.
   * @param fn2 a function of arity two that producues a monad computation.
   * @return a monad.
   */
  public default <B, C> Monad<M,C> For(F1<? super A,  ? extends Monad<M,B>> fn,
      F2<? super A, ? super B, ? extends Monad<M,C>> fn2) {
    return Monad.For(this, fn, fn2);
  } 

  /**
  * Sequences bind operation starting with this monad.
  * @param fn a function that produces a monad computation.
  * @param fn2 a function of arity two that producues a monad computation.
  * @param fn3 a function of arity three that produces a monad computation
  * @return a monad.
  */
  public default <B, C, D> Monad<M,D> For(F1<? super A,  ? extends Monad<M,B>> fn,
        F2<? super A,? super  B, ? extends Monad<M,C>> fn2, F3<? super A,? super B,? super C, ? extends Monad<M,D>> fn3) {
    return Monad.For(this, fn, fn2, fn3);
  }

 /**
  * Sequences bind operation starting with this monad.
  * @param fn a function that produces a monad computation.
  * @param fn2 a function of arity two that producues a monad computation.
  * @param fn3 a function of arity three that produces a monad computation
  * @param fn4 a function of arity four that produces a monad computation
  * @return a monad.
  */
  public default <B, C, D, E> Monad<M,E> For(F1<? super A,  ? extends Monad<M,B>> fn,
        F2<? super A,? super  B, ? extends Monad<M,C>> fn2, F3<? super A,? super B,? super C, ? extends Monad<M,D>> fn3,
        F4<? super A,? super B,? super C,? super D, ? extends Monad<M,E>> fn4) {
    return Monad.For(this, fn, fn2, fn3, fn4);
  }

  /**
   * Sequences bind operation, each function captures the value of previous compuations.
   * @param monad initial monad.
   * @param fn a function that produces a monad computation.
   * @param fn2 a function of arity two that producues a monad computation.
   * @return a monad.
   */
    public static <M, A, B, C> Monad<M,C> For(Monad<M,A> monad, F1<? super A,  ? extends Monad<M,B>> fn,
        F2<? super A, ? super B, ? extends Monad<M,C>> fn2) {
    return monad.bind(a -> {
      Monad<M,B> mb = fn.call(a);
      return mb.bind(b -> {
        Monad<M,C> mc = fn2.call(a,b);
        return mc; 
      });
    });
  } 

  /**
   * Sequences bind operation, each function captures the value of previous compuations.
   * @param monad initial monad.
   * @param fn a function that produces a monad computation.
   * @param fn2 a function of arity two that producues a monad computation.
   * @param fn3 a function of arity three that produces a monad computation
   * @return a monad.
   */
    public static <M, A, B, C, D> Monad<M,D> For(Monad<M,A> monad, F1<? super A,  ? extends Monad<M,B>> fn,
        F2<? super A,? super  B, ? extends Monad<M,C>> fn2, F3<? super A,? super B,? super C, ? extends Monad<M,D>> fn3) {
    return monad.bind(a -> {
      Monad<M,B> mb = fn.call(a);
      return mb.bind(b -> {
        Monad<M,C> mc = fn2.call(a,b);
        return mc.bind( c -> {
          Monad<M,D> md = fn3.call(a,b,c);
         return md;
        }); 
      });
    });
  }

  /**
   * Sequences bind operation, each function captures the value of previous compuations.
   * @param monad initial monad.
   * @param fn a function that produces a monad computation.
   * @param fn2 a function of arity two that producues a monad computation.
   * @param fn3 a function of arity three that produces a monad computation.
   * @param fn4 a function of arity four that produces a monad computation.
   * @return a monad.
   */
    public static <M, A, B, C, D, E> Monad<M,E> For(Monad<M,A> monad, F1<? super A,  ? extends Monad<M,B>> fn,
        F2<? super A,? super  B, ? extends Monad<M,C>> fn2, F3<? super A,? super B,? super C, ? extends Monad<M,D>> fn3,
        F4<? super A,? super B,? super C,? super D, ? extends Monad<M,E>> fn4) {
    return monad.bind(a -> {
      Monad<M,B> mb = fn.call(a);
      return mb.bind(b -> {
        Monad<M,C> mc = fn2.call(a,b);
        return mc.bind( c -> {
          Monad<M,D> md = fn3.call(a,b,c);
         return md.bind( d -> {
          Monad<M,E> me = fn4.call(a,b,c,d);
          return me; 
         }); 
        }); 
      });
    });
  }

  /**
   * Promotes a function to a monad then applies each monad to the function.
   * @param m a monad.
   * @param m1 a monad.
   * @param fn the function to be promoted to a monad.
   * @return a monad created by apply the arguments({@code m, m1}) to the promoted function {@code fn}.
   */
  public static <M, A, B, C> Monad<M,C> liftM2(Monad<M,A> m, Monad<M,B> m1, F2<? super A,? super B,C> fn) {
    return Monad.For( m
                    , a    -> m1
                    ,(a,b) -> m.yield().unit((fn.apply(a,b))));
  }

  /**
   * Promotes a function to a monad then applies each monad to the function.
   * @param m a monad.
   * @param m1 a monad.
   * @param m2 a monad.
   * @param fn the function to be promoted to a monad.
   * @return a monad created by apply the arguments({@code m, m1, m2}) to the promoted function {@code fn}.
   */
  public static <M, A, B, C, D> Monad<M,D> liftM3(Monad<M,A> m, Monad<M,B> m1, Monad<M,C> m2,
      F3<? super A,? super B, ? super C, D> fn) {
    return Monad.For( m
                    , a      -> m1
                    ,(a,b)   -> m2
                    ,(a,b,c) -> m.yield().unit((fn.call(a,b,c))));
  }

  /**
   * Promotes a function to a monad then applies each monad to the function.
   * @param m a monad.
   * @param m1 a monad.
   * @param m2 a monad.
   * @param m3 a monad.
   * @param fn the function to be promoted to a monad.
   * @return a monad created by apply the arguments({@code m, m1, m2, m3}) to the promoted function {@code fn}.
   */
  public static <M, A, B, C, D, E> Monad<M,E> liftM4(Monad<M,A> m, Monad<M,B> m1, Monad<M,C> m2, Monad<M,D> m3,
      F4<? super A,? super B, ? super C, ? super D,E> fn) {
    return Monad.For( m
                    , a        -> m1
                    ,(a,b)     -> m2
                    ,(a,b,c)   -> m3
                    ,(a,b,c,d) -> m.yield().unit((fn.call(a,b,c,d))));
  }

  @Override
  public default <B,C> Monad<M,F1<B,C>> map(F2<? super A, B, C> fn) {
    return (Monad<M,F1<B,C>>)Applicative.super.map(fn);
  }

  @Override
  public default <B,C,D> Monad<M, F1<B, F1<C,D>>>  map(F3<? super A, B, C, D> fn) {
    return (Monad<M, F1<B, F1<C,D>>>)Applicative.super.map(fn);
  }

  @Override
  public default <B,C,D,E> Monad<M,F1<B, F1<C, F1<D,E>>>>  map(F4<? super A, B, C, D, E> fn) {
    return (Monad<M,F1<B, F1<C, F1<D,E>>>>)Applicative.super.map(fn);
  }

  @Override
  public default <B,C,D,E,G> Monad<M,F1<B,F1<C,F1<D,F1<E,G>>>>>  map(F5<? super A, B, C, D, E, G> fn) {
    return (Monad<M,F1<B,F1<C,F1<D,F1<E,G>>>>>)Applicative.super.map(fn);
  }

  /**
   * Reapeats the monad forever.
   * @param ma a monad.
   * @return a monad whose action will repeat forever.
   */
  public static <M, A> Monad<M,A> forever(Monad<M,A> ma) {
    F2<Monad<M,A>, A, Monad<M,A>> f = Forever.<M,A,A>forever();
    return ma.bind(f.call(ma));
  }
}
