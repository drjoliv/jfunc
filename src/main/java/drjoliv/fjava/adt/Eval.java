package drjoliv.fjava.adt;

import static drjoliv.fjava.adt.Trampoline.done$;
import static drjoliv.fjava.adt.Trampoline.done;

import drjoliv.fjava.adt.Eval.μ;
import drjoliv.fjava.applicative.Applicative;
import drjoliv.fjava.applicative.ApplicativePure;
import drjoliv.fjava.functions.F0;
import drjoliv.fjava.functions.F1;
import drjoliv.fjava.functions.F2;
import drjoliv.fjava.hkt.Witness;
import drjoliv.fjava.monad.Monad;
import drjoliv.fjava.monad.MonadUnit;

/**
 * Delays the evaluation of an expression, caching the value the first time the expression is evaluated.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public abstract class Eval<A> implements Monad<Eval.μ,A> {

  /**
  * The witness type of {@code Eval}.
  */
  public static class μ implements drjoliv.fjava.hkt.Witness{private μ(){}}

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
  public abstract <B> Eval<B> bind(F1<? super A, ? extends Monad<μ, B>> fn);

  @Override
  public abstract <B> Eval<B> semi(Monad<μ, B> mb);

  /**
   * Returns the value witin this eval.
   * @return the value within this eval.
   */
  abstract public A value();

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
    public A value() {
      return value;
    }

    @Override
    public <B> Eval<B> map(F1<? super A, ? extends B> fn) {
      return later(f0(() -> value).map(fn));
    }

    @Override
    public <B> Eval<B> bind(F1<? super A, ? extends Monad<μ, B>> fn) {
      return new Later<>(f0(() -> monad(fn.call(value)).value()));
    }

    @Override
    public <B> Eval<B> semi(Monad<μ, B> mb) {
      return bind(a -> mb);
    }
  }

  private static class Later<A> extends Eval<A> {

    private volatile F0<A> supplier;
    private A value;

    private Later(F0<A> fn) {
      this.supplier = fn;
    }

    @Override
    public A value() {
      return value == null ? computeValue() : value;
    }

    private synchronized A computeValue() {
      if(value == null) {
        value = supplier.call();
        supplier = null;
        return value;
      } else {
        return value;
      }
    }

    @Override
    public <B> Eval<B> map(F1<? super A, ? extends B> fn) {
      return (supplier == null)
        ? new Later<B>(f0(() -> value).map(fn))
        : new Later<B>(supplier.map(fn));
    }

    @Override
    public <B> Eval<B> bind(F1<? super A, ? extends Monad<μ, B>> fn) {
      if(supplier == null) {
        return new Later<>(f0(() -> value))
            .map(v -> monad(fn.call(v)).value());
      } else {
         F0<B> ret = supplier
          .map(v -> monad(fn.call(v)).value());
         return new Later<>(ret);
      }
    }
    
    @Override
    public <B> Eval<B> semi(Monad<μ, B> mb) {
      return bind(a -> mb);
    }
  }

 private static <A> F0<A> f0(F0<A> supplier) {
   return supplier;
 }

  /**
   * @see drjoliv.fjava.monad.Monad#liftM2(Monad,Monad,F2)
   */
  public static <A,B,C> Eval<C> liftM2(Monad<Eval.μ,A> m, Monad<Eval.μ,B> m1, F2<? super A,? super B, C> fn) {
    return monad(Monad.liftM2(m,m1,fn));
  }

}
