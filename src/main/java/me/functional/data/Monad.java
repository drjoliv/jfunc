package me.functional.data;

import java.util.function.BiFunction;
import java.util.function.Function;

import me.functional.QuadFunction;
import me.functional.TriFunction;
import me.functional.hkt.Witness;
import me.functional.type.Functor;

public interface Monad<M extends Witness,A> extends Functor<M,A> {

  //m >>= return == m
  //(return x) >>= f == f x
  //(m >>= f) >>= g == m >>= (\x -> f x >>= g)
  public <B> Monad<M,B> mBind(final Function<A,? extends Monad<M,B>> fn);

  public <B> Monad<M,B> semi(Monad<M,B> mb);

  public <B> Monad<M,B> mUnit(B b);

  @Override
  public <B> Monad<M,B> fmap(Function<A,B> fn);

  public default <B> Monad<M,B> For(final Function<A,? extends Monad<M,B>> fn) {
    return Monad.For(this,fn);
  }

  public default <B,C> Monad<M,C> For(Function<A, Monad<M,B>> fn, BiFunction<A, B,? extends Monad<M,C>> fn2) {
    return Monad.For(this,fn,fn2);
  } 

  public default <B,C,D> Monad<M,D> For(Function<A, ? extends Monad<M,B>> fn, BiFunction<A, B, ? extends Monad<M,C>> fn2,
      TriFunction<A,B,C, ? extends Monad<M,D>> fn3) {
    return Monad.For(this,fn,fn2,fn3);
  }

    public default <B,C,D,E> Monad<M,E> For(Function<A,  ? extends Monad<M,B>> fn, BiFunction<A, B, ? extends Monad<M,C>> fn2,
      TriFunction<A,B,C, ? extends Monad<M,D>> fn3, QuadFunction<A,B,C,D, ? extends Monad<M,E>> fn4) {
      return Monad.For(this,fn, fn2, fn3, fn4);
  }

  public static <M extends Witness,A,B> Monad<M,B> For(Monad<M,A> monad, Function<A,  ? extends Monad<M,B>> fn) {
    return monad.mBind(fn);
  } 

    public static <M extends Witness,A,B,C> Monad<M,C> For(Monad<M,A> monad, Function<A,  ? extends Monad<M,B>> fn,
        BiFunction<A, B, ? extends Monad<M,C>> fn2) {
    return monad.mBind(a -> {
      return fn.apply(a).mBind(b -> {
        return fn2.apply(a,b); 
      });
    });
  } 

    public static <M extends Witness,A,B,C,D> Monad<M,D> For(Monad<M,A> monad, Function<A,  ? extends Monad<M,B>> fn,
        BiFunction<A, B, ? extends Monad<M,C>> fn2, TriFunction<A,B,C, ? extends Monad<M,D>> fn3) {
    return monad.mBind(a -> {
      return fn.apply(a).mBind(b -> {
        return fn2.apply(a,b).mBind( c -> {
         return fn3.apply(a,b,c); 
        }); 
      });
    });
  }

    public static <M extends Witness,A,B,C,D,E> Monad<M,E> For(Monad<M,A> monad, Function<A,  ? extends Monad<M,B>> fn,
        BiFunction<A, B, ? extends Monad<M,C>> fn2, TriFunction<A,B,C, ? extends Monad<M,D>> fn3,
        QuadFunction<A,B,C,D, ? extends Monad<M,E>> fn4) {
    return monad.mBind(a -> {
      return fn.apply(a).mBind(b -> {
        return fn2.apply(a,b).mBind( c -> {
         return fn3.apply(a,b,c).mBind( d -> {
          return fn4.apply(a,b,c,d); 
         }); 
        }); 
      });
    });
  }

  public static <M extends Witness,A,B,C> Monad<M,C> liftM2(Monad<M,A> m, Monad<M,B> m1, BiFunction<A,B,C> fn) {
    return Monad.For( m
                    , a    -> m1
                    ,(a,b) -> m.mUnit(fn.apply(a,b)));
  }

  public static <M extends Witness,A,B,C,D> Monad<M,D> liftM3(Monad<M,A> m, Monad<M,B> m1, Monad<M,C> m2,
      TriFunction<A,B,C,D> fn) {
    return Monad.For( m
                    , a      -> m1
                    ,(a,b)   -> m2
                    ,(a,b,c) -> m.mUnit(fn.apply(a,b,c)));
  }

  public static <M extends Witness,A,B,C,D,E> Monad<M,E> liftM4(Monad<M,A> m, Monad<M,B> m1, Monad<M,C> m2, Monad<M,D> m3,
      QuadFunction<A,B,C,D,E> fn) {
    return Monad.For( m
                    , a        -> m1
                    ,(a,b)     -> m2
                    ,(a,b,c)   -> m3
                    ,(a,b,c,d) -> m.mUnit(fn.apply(a,b,c,d)));
  }
}
