package me.functional.type;

import java.util.function.BiFunction;
import java.util.function.Function;

import me.functional.functions.F1;
import me.functional.functions.F2;
import me.functional.functions.F3;
import me.functional.functions.F4;
import me.functional.functions.QuadFunction;
import me.functional.functions.TriFunction;
import me.functional.hkt.Witness;

public interface Bind<M extends Witness,A> extends Functor<M,A> {

  //m >>= return == m
  //(return x) >>= f == f x
  //(m >>= f) >>= g == m >>= (\x -> f x >>= g)
  public <B> Bind<M,B> mBind(final F1<? super A,? extends Bind<M,B>> fn);

  public <B> Bind<M,B> semi(Bind<M,B> mb);

  public BindUnit<M> yield();

  @Override
  public <B> Bind<M,B> fmap(F1<? super A, B> fn);

  public static <M extends Witness, A> Bind<M,A> join(Bind<M,? extends Bind<M,A>> monad) {
    return monad.mBind(m -> m);
  }

  public static <M extends Witness,A,B> Bind<M,B> For(Bind<M,A> monad, F1<? super A,  ? extends Bind<M,B>> fn) {
    return monad.mBind(fn);
  } 

    public static <M extends Witness,A,B,C> Bind<M,C> For(Bind<M,A> monad, F1<? super A,  ? extends Bind<M,B>> fn,
        BiFunction<? super A, ? super B, ? extends Bind<M,C>> fn2) {
    return monad.mBind(a -> {
      Bind<M,B> mb = fn.call(a);
      return mb.mBind(b -> {
        Bind<M,C> mc = fn2.apply(a,b);
        return mc; 
      });
    });
  } 

    public static <M extends Witness,A,B,C,D> Bind<M,D> For(Bind<M,A> monad, F1<? super A,  ? extends Bind<M,B>> fn,
        F2<? super A,? super  B, ? extends Bind<M,C>> fn2, F3<? super A,? super B,? super C, ? extends Bind<M,D>> fn3) {
    return monad.mBind(a -> {
      Bind<M,B> mb = fn.call(a);
      return mb.mBind(b -> {
        Bind<M,C> mc = fn2.apply(a,b);
        return mc.mBind( c -> {
          Bind<M,D> md = fn3.apply(a,b,c);
         return md;
        }); 
      });
    });
  }

    public static <M extends Witness,A,B,C,D,E> Bind<M,E> For(Bind<M,A> monad, F1<? super A,  ? extends Bind<M,B>> fn,
        F2<? super A,? super  B, ? extends Bind<M,C>> fn2, F3<? super A,? super B,? super C, ? extends Bind<M,D>> fn3,
        F4<? super A,? super B,? super C,? super D, ? extends Bind<M,E>> fn4) {
    return monad.mBind(a -> {
      Bind<M,B> mb = fn.call(a);
      return mb.mBind(b -> {
        Bind<M,C> mc = fn2.call(a,b);
        return mc.mBind( c -> {
          Bind<M,D> md = fn3.call(a,b,c);
         return md.mBind( d -> {
          Bind<M,E> me = fn4.call(a,b,c,d);
          return me; 
         }); 
        }); 
      });
    });
  }

  public static <M extends Witness,A,B,C> Bind<M,C> liftM2(Bind<M,A> m, Bind<M,B> m1, F2<? super A,? super B,C> fn) {
    return Bind.For( m
                    , a    -> m1
                    ,(a,b) -> m.yield().unit((fn.apply(a,b))));
  }

  public static <M extends Witness,A,B,C,D> Bind<M,D> liftM3(Bind<M,A> m, Bind<M,B> m1, Bind<M,C> m2,
      F3<? super A,? super B, ? super C, D> fn) {
    return Bind.For( m
                    , a      -> m1
                    ,(a,b)   -> m2
                    ,(a,b,c) -> m.yield().unit((fn.apply(a,b,c))));
  }

  public static <M extends Witness,A,B,C,D,E> Bind<M,E> liftM4(Bind<M,A> m, Bind<M,B> m1, Bind<M,C> m2, Bind<M,D> m3,
      F4<? super A,? super B, ? super C, ? super D,E> fn) {
    return Bind.For( m
                    , a        -> m1
                    ,(a,b)     -> m2
                    ,(a,b,c)   -> m3
                    ,(a,b,c,d) -> m.yield().unit((fn.apply(a,b,c,d))));
  }
}
