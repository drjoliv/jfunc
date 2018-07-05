package drjoliv.jfunc.contorl;

import java.util.Objects;

import drjoliv.jfunc.applicative.Applicative;
import drjoliv.jfunc.applicative.ApplicativePure;
import drjoliv.jfunc.function.F0;
import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.function.F2;
import drjoliv.jfunc.monad.Monad;
import drjoliv.jfunc.monad.MonadUnit;

/**
 * Delays the evaluation of an expression, caching the value the first time the expression is evaluated.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public abstract class Eval<A> implements Monad<Eval.μ,A> {

  /**
  * The witness type of {@code Eval}.
  */
  public static class μ {private μ(){}}

  abstract <B> Eval<B> doBind(F1<? super A, ? extends Monad<μ, B>> fn);

  abstract Eval<A> step();

  @Override
  public <B> Eval<B> apply(Applicative<μ, ? extends F1<? super A, ? extends B>> f) {
    return monad(Monad.liftM2(this, (Eval<F1<? super A, B>>)f, (a,fn) -> fn.call(a)));
  }

  @Override
  public ApplicativePure<μ> pure() {
    return Eval::now;
  }

  @Override
  public MonadUnit<μ> yield() {
    return Eval::now;
  }

  @Override
  public abstract <B> Eval<B> map(F1<? super A, ? extends B> fn);

  @Override
  public <B> Eval<B> bind(F1<? super A, ? extends Monad<μ, B>> fn) {
    return new EvalBind<A,B>(this, fn);
  }
  @Override
  public <B> Eval<B> semi(Monad<μ, B> mb) {
    return bind(a -> mb);
  }

  /**
   * Returns the value witin this eval.
   * @return the value within this eval.
   */
  public abstract A value();

  /**
   * Constructs an eval from a value.
   * @param a a value to be lifted into an eval.
   * @return an eval.
   */
  public static <A> Eval<A> now(A a) {
    return new Now<>(a);
  }

  /**
   * Constructs an eval from a supplier of a value.
   * @param fn a supplier.
   * @return an eval.
   */
  public static <A> Eval<A> later(F0<A> fn) {
    return new Later<>(fn);
  }

  /**
   * A helper function to convert/narrow a reference from a monad to its underlying type.
   * @param monad the monad to be casted to its original type.
   * @return a eval.
   */
  public static <B>  Eval<B> monad(Monad<μ, B> monad) {
    return (Eval<B>)monad; 
  }

  private static class Now<A> extends Eval<A> {

    private final A value;

    private Now(A value) {
      this.value = value;
    }

    @Override
    public <B> Eval<B> map(F1<? super A, ? extends B> fn) {
      return later(f0(value).map(fn));
    }

    @Override
    Eval<A> step() {
      return this;
    }

    @Override
    <B> Eval<B> doBind(F1<? super A, ? extends Monad<μ, B>> fn) {
      Objects.requireNonNull(value); 
      return monad(fn.call(value));
    }

    @Override
    public A value() {
      return value;
    }
  }

  private static class EvalBind<A,B> extends Eval<B> {
    private Eval<A> eval;
    private F1<? super A, ? extends Monad<μ, B>> binder;
    private B cached   = null;

    public EvalBind(Eval<A> eval, F1<? super A, ? extends Monad<μ, B>> binder) {
      this.eval = eval;
      this.binder = binder;
    }

    public Eval<B> step() {
      //System.out.println("infinte step");
      return eval.doBind(binder);
    }

    public <C> Eval<C> doBind(F1<? super B, ? extends Monad<μ, C>> fn) {
      //System.out.println("infinte dobind");
      return eval.bind(a -> ((Eval<B>)binder.call(a)).bind(fn));
    }

    @Override
    public B value() {
      if(cached == null) {
        synchronized(this) {
          Eval<B> eval = this;
          while(eval instanceof EvalBind)
            eval = eval.step();
          //cached = eval.value();
          //eval   = null;
          //binder = null;
          return eval.value();
        }
      } else {
        return cached;
      }
    }

    @Override
    public <C> Eval<C> map(F1<? super B, ? extends C> fn) {
        return bind(a -> now(fn.call(a)));
      //System.out.println("infinte map");
      //if(cached != null) {
      //  return later(f0(cached).map(fn));
      //} else {
      //  return bind(a -> now(fn.call(a)));
      //}
    }

    //@Override
    //public <C> Eval<C> bind(F1<? super B, ? extends Monad<μ, C>> fn) {
    //   if(cached == null) {
    //  System.out.println("infinte bind not cached");
    //   return super.bind(fn);
    //  } else {
    //  System.out.println("infinte bind cached");
    //    Objects.requireNonNull(cached);
    //    return now(cached).bind(fn);
    //  }
    //}
  }

  private static class Later<A> extends Eval<A> {

    private volatile F0<A> supplier;
    private A cache;

    private Later(F0<A> fn) {
      this.supplier = fn;
    }

    private synchronized A computeValue() {
      if(cache == null) {
        cache = supplier.call();
        supplier = null;
        return cache;
      } else {
        return cache;
      }
    }

    @Override
    public A value() {
      return supplier == null ? cache : computeValue();
    }

    @Override
    <B> Eval<B> doBind(F1<? super A, ? extends Monad<μ, B>> fn) {
      return monad(fn.call(value()));
    }

    @Override
    public <B> Eval<B> map(F1<? super A, ? extends B> fn) {
      if(supplier == null)
        return later(f0(cache).map(fn));
      else
        return later(supplier.map(fn));
    }

    @Override
    Eval<A> step() {
      return this;
    }
  }

 private static <A> F0<A> f0(A a) {
   return () -> a;
 }

  /**
   * @see drjoliv.jfunc.monad.Monad#liftM2(Monad,Monad,F2)
   */
  public static <A, B, C> Eval<C> liftM2(Monad<Eval.μ, A> m, Monad<Eval.μ, B> m1, F2<? super A, ? super B, C> fn) {
    return monad(Monad.liftM2(m, m1, fn));
  }
  
  //public static void main(String[] args) {
  //  
  //    F1<Integer,Eval<Integer>> f = i -> liftM2(now(i), now(i), (b,c) -> b + c);
  //    Eval<Integer> m = now(1);
  //    
  //    for(int i = 0; i < 3; i++) {
  //        m = m.bind(f);
  //      }
  // //Eval<Integer> one = liftM2(now(1), now(1), (a,b) -> a + b);
  //  
  //  System.out.println(m.value());

  //  
  //}
}
