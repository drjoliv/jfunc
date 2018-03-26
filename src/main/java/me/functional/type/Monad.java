package me.functional.type;

import java.util.function.BiFunction;
import java.util.function.Function;

import me.functional.functions.QuadFunction;
import me.functional.functions.TriFunction;
import me.functional.hkt.Witness;

public interface Monad<M extends Witness,A> extends Functor<M,A> {

  //m >>= return == m
  //(return x) >>= f == f x
  //(m >>= f) >>= g == m >>= (\x -> f x >>= g)
  public <B> Monad<M,B> mBind(final Function<? super A,? extends Monad<M,B>> fn);

  public <B> Monad<M,B> semi(Monad<M,B> mb);

  public MonadUnit<M> yield();

  @Override
  public <B> Monad<M,B> fmap(Function<? super A,B> fn);

  public static <M extends Witness,A,B> Monad<M,B> For(Monad<M,A> monad, Function<? super A,  ? extends Monad<M,B>> fn) {
    return monad.mBind(fn);
  } 

    public static <M extends Witness,A,B,C> Monad<M,C> For(Monad<M,A> monad, Function<? super A,  ? extends Monad<M,B>> fn,
        BiFunction<? super A, ? super B, ? extends Monad<M,C>> fn2) {
    return monad.mBind(a -> {
      Monad<M,B> mb = fn.apply(a);
      return mb.mBind(b -> {
        Monad<M,C> mc = fn2.apply(a,b);
        return mc; 
      });
    });
  } 

    public static <M extends Witness,A,B,C,D> Monad<M,D> For(Monad<M,A> monad, Function<? super A,  ? extends Monad<M,B>> fn,
        BiFunction<? super A,? super  B, ? extends Monad<M,C>> fn2, TriFunction<? super A,? super B,? super C, ? extends Monad<M,D>> fn3) {
    return monad.mBind(a -> {
      Monad<M,B> mb = fn.apply(a);
      return mb.mBind(b -> {
        Monad<M,C> mc = fn2.apply(a,b);
        return mc.mBind( c -> {
          Monad<M,D> md = fn3.apply(a,b,c);
         return md;
        }); 
      });
    });
  }

    public static <M extends Witness,A,B,C,D,E> Monad<M,E> For(Monad<M,A> monad, Function<? super A,  ? extends Monad<M,B>> fn,
        BiFunction<? super A,? super  B, ? extends Monad<M,C>> fn2, TriFunction<? super A,? super B,? super C, ? extends Monad<M,D>> fn3,
        QuadFunction<? super A,? super B,? super C,? super D, ? extends Monad<M,E>> fn4) {
    return monad.mBind(a -> {
      Monad<M,B> mb = fn.apply(a);
      return mb.mBind(b -> {
        Monad<M,C> mc = fn2.apply(a,b);
        return mc.mBind( c -> {
          Monad<M,D> md = fn3.apply(a,b,c);
         return md.mBind( d -> {
          Monad<M,E> me = fn4.apply(a,b,c,d);
          return me; 
         }); 
        }); 
      });
    });
  }

  public static <M extends Witness,A,B,C> Monad<M,C> liftM2(Monad<M,A> m, Monad<M,B> m1, BiFunction<? super A,? super B,C> fn) {
    return Monad.For( m
                    , a    -> m1
                    ,(a,b) -> m.yield().unit((fn.apply(a,b))));
  }

  public static <M extends Witness,A,B,C,D> Monad<M,D> liftM3(Monad<M,A> m, Monad<M,B> m1, Monad<M,C> m2,
      TriFunction<? super A,? super B, ? super C, D> fn) {
    return Monad.For( m
                    , a      -> m1
                    ,(a,b)   -> m2
                    ,(a,b,c) -> m.yield().unit((fn.apply(a,b,c))));
  }

  public static <M extends Witness,A,B,C,D,E> Monad<M,E> liftM4(Monad<M,A> m, Monad<M,B> m1, Monad<M,C> m2, Monad<M,D> m3,
      QuadFunction<? super A,? super B, ? super C, ? super D,E> fn) {
    return Monad.For( m
                    , a        -> m1
                    ,(a,b)     -> m2
                    ,(a,b,c)   -> m3
                    ,(a,b,c,d) -> m.yield().unit((fn.apply(a,b,c,d))));
  }
}
