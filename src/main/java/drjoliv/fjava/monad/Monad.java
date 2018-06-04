package drjoliv.fjava.monad;


import drjoliv.fjava.adt.FList;
import drjoliv.fjava.functions.F1;
import drjoliv.fjava.functions.F2;
import drjoliv.fjava.functions.F3;
import drjoliv.fjava.functions.F4;
import drjoliv.fjava.functor.Functor;
import drjoliv.fjava.hkt.Witness;

public interface Monad<M extends Witness,A> extends Functor<M,A> {

  //m >>= return == m
  //(return x) >>= f == f x
  //(m >>= f) >>= g == m >>= (\x -> f x >>= g)
  public default <B> Monad<M,B> bind(final F1<? super A,? extends Monad<M,B>> fn) {
    return join(map(fn));
  }

  public <B> Monad<M,B> semi(Monad<M,B> mb);

  public static <M extends Witness, A, B> F2<Monad<M,A>, Monad<M,B>, Monad<M,B>> semi() {
    return (m1, m2) -> m1.semi(m2);
  }

  public MonadUnit<M> yield();

  @Override
  public <B> Monad<M,B> map(F1<? super A, B> fn);

  public static <M extends Witness, A> Monad<M,A> join(Monad<M,? extends Monad<M,A>> monad) {
    return monad.bind(m -> m);
  }

  public static <M extends Witness, A, B> F2<Monad<M,A>, F1<? super A,? extends Monad<M,B>>, Monad<M,B>> bind() {
    return (m,f) -> m.bind(f);
  }

  public static <M extends Witness, A> F1<Monad<M,? extends Monad<M,A>>, Monad<M,A>> join() {
    return Monad::join;
  }

  public static <M extends Witness,A,B> Monad<M,B> For(Monad<M,A> monad, F1<? super A,  ? extends Monad<M,B>> fn) {
    return monad.bind(fn);
  } 

    public static <M extends Witness,A,B,C> Monad<M,C> For(Monad<M,A> monad, F1<? super A,  ? extends Monad<M,B>> fn,
        F2<? super A, ? super B, ? extends Monad<M,C>> fn2) {
    return monad.bind(a -> {
      Monad<M,B> mb = fn.call(a);
      return mb.bind(b -> {
        Monad<M,C> mc = fn2.call(a,b);
        return mc; 
      });
    });
  } 

    public static <M extends Witness,A,B,C,D> Monad<M,D> For(Monad<M,A> monad, F1<? super A,  ? extends Monad<M,B>> fn,
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

    public static <M extends Witness,A,B,C,D,E> Monad<M,E> For(Monad<M,A> monad, F1<? super A,  ? extends Monad<M,B>> fn,
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

  public static <M extends Witness,A,B,C> Monad<M,C> liftM2(Monad<M,A> m, Monad<M,B> m1, F2<? super A,? super B,C> fn) {
    return Monad.For( m
                    , a    -> m1
                    ,(a,b) -> m.yield().unit((fn.apply(a,b))));
  }

  public static <M extends Witness,A,B,C,D> Monad<M,D> liftM3(Monad<M,A> m, Monad<M,B> m1, Monad<M,C> m2,
      F3<? super A,? super B, ? super C, D> fn) {
    return Monad.For( m
                    , a      -> m1
                    ,(a,b)   -> m2
                    ,(a,b,c) -> m.yield().unit((fn.call(a,b,c))));
  }

  public static <M extends Witness,A,B,C,D,E> Monad<M,E> liftM4(Monad<M,A> m, Monad<M,B> m1, Monad<M,C> m2, Monad<M,D> m3,
      F4<? super A,? super B, ? super C, ? super D,E> fn) {
    return Monad.For( m
                    , a        -> m1
                    ,(a,b)     -> m2
                    ,(a,b,c)   -> m3
                    ,(a,b,c,d) -> m.yield().unit((fn.call(a,b,c,d))));
  }
}
