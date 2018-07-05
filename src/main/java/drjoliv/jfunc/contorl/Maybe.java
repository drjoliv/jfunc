package drjoliv.jfunc.contorl;

import static drjoliv.jfunc.contorl.Eval.later;

import drjoliv.jfunc.applicative.Applicative;
import drjoliv.jfunc.applicative.ApplicativePure;
import drjoliv.jfunc.collection.Unit;
import drjoliv.jfunc.function.F0;
import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.function.F2;
import drjoliv.jfunc.hkt.Hkt;
import drjoliv.jfunc.monad.Monad;
import drjoliv.jfunc.monad.MonadUnit;

/**
 * Represents a value of something(Just) or a value of Nothing.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public abstract class Maybe<A> implements Hkt<Maybe.μ, A>, Monad<Maybe.μ, A>, Case2<Maybe<A>,Maybe.None<A>,Maybe.Just<A>>{

  /**
  * The witness type of Maybe.
  */
  public static class μ {private μ(){}}

  private Maybe() {}

  @Override
  public abstract <B> Maybe<B> map(F1<? super A, ? extends B> fn);

  @Override
  public <B> Maybe<B> apply(Applicative<μ, ? extends F1<? super A, ? extends B>> f) {
    return monad(Monad.liftM2(this, applicative(f), (a,fn) -> fn.call(a)));
  }

  @Override
  public ApplicativePure<μ> pure() {
    return Maybe::maybe;
  }

  @Override
  public abstract <B> Maybe<B> bind(F1<? super A, ? extends Monad<μ, B>> fn);

  @Override
  public final <B> Maybe<B> semi(Monad<μ, B> mb) {
    return bind(a -> mb);
  }

  @Override
  public MonadUnit<μ> yield() {
    return Maybe::maybe;
  }

  /**
   * Pattern match against this maybe, returning the reuslt of function {@code none} if this is a none or the result of function {@code some) if this is a some.
   * @param none a function.
   * @param some a function.
   * @return the result of either one the argument functions.
   */
  public final <B> B visit(F1<Unit,B> none, F1<A,B> some) {
    return match(n -> none.call(Unit.unit)
                ,s -> some.call(s.value()));
  }

 /**
  * Returns true if this maybe contains a value and false otherwise.
  * @return true if this  Maybe contains a value and false otherwise.
  */
  public abstract boolean isSome();

  /**
   * Returns the value of this maybe if there is one otherwise its returns null..
   * @return Returns the value of this maybe if there is one or null otherwise.
   */
  public abstract A toNull();

 /**
  * Returns a if this  maybe is nothing otherwise it returns the value contained within just.
  * @param a the value return if this is nothing.
  * @return the supplied value if this maybe is nothing.
  */
  public A orSome(A a) {
    return isSome() ? toNull() : a;
  }

 /**
  * Returns the given argument if this is nothing, or this if this is just.
  * @param maybe the value returned if this is nothing.
  * @return the supplied Maybe of this if this is none, or this if this is just.
  */
  public Maybe<A> orElse(Maybe<A> maybe) {
    return isSome() ? this : maybe;
  }

 /**
  * Constructs a maybe from the given argument.
  *
  * @param a the value to be wrappped into a myabe.
  * @return the maybe value containing gthe argument.
  */
  public static <A> Maybe<A> maybe(A a) {
      return maybe$(() -> a);
  }

 /**
  * Constructs a maybe from the value returned by the supplier.
  *
  * @param supplier a supplier that returns a value that is used to construct a maybe.
  * @return the maybe created from the argument.
  */
  public static <A> Maybe<A> maybe$(F0<A> supplier) {
      return new Just<A>(later(supplier));
  }

 /**
  * Constructs a maybe from an eval.
  * @param eval the comutation whose result will be inserted in a maybe.
  * @return a maybe containing the result of the argument.
  */
  public static <A> Maybe<A> maybe$(Eval<A> eval) {
      return new Just<A>(eval);
  }

  /**
   * The instance of maybe that does not contain a value.
   */
  public static class None<A> extends Maybe<A> {
    private static None<?> instance = new None<>();

    private None() {}

    @Override
    public boolean isSome() {
      return false;
    }

    @Override
    public <B> Maybe<B> map(F1<? super A, ? extends B> fn) {
      return nothing();
    }

    @Override
    public <B> Maybe<B> bind(F1<? super A, ? extends Monad<μ, B>> fn) {
      return nothing();
    }

    @Override
    public A toNull() {
      return null;
    }

    @Override
    public <B> B match(F1<None<A>, B> left, F1<Just<A>, B> right) {
      return left.call(this);
    }
  }

  /**
   * The instance of maybe that contains a value.
   */
  public static class Just<A> extends Maybe<A> {

    private Eval<A> value;

    private Just(Eval<A> value) {
      this.value = value;
    }

    @Override
    public boolean isSome() {
      return true;
    }

    @Override
    public A toNull() {
      return value.value();
    }

    @Override
    public <B> Maybe<B> map(F1<? super A, ? extends B> fn) {
      return new Just<B>(value.map(fn));
    }

    @Override
    public <B> Maybe<B> bind(F1<? super A, ? extends Monad<μ, B>> fn) {
      return new MaybeBind<>(value.map(a -> monad(fn.call(a))));
    }

    @Override
    public <B> B match(F1<None<A>, B> left, F1<Just<A>, B> right) {
      return right.call(this);
    }

    /**
     * Returns the value contained within this maybe.
     * @return the value contained within this maybe.
     */
    public A value() {
      return value.value();
    }
  }

  private static class MaybeBind<A> extends Maybe<A> {

    private final Eval<Maybe<A>> maybe;

    private MaybeBind(Eval<Maybe<A>> maybe) {
      this.maybe = maybe;
    }

    @Override
    public <B> Maybe<B> map(F1<? super A, ? extends B> fn) {
      return bind(a -> maybe(fn.call(a)));
    }

    @Override
    public <B> Maybe<B> apply(Applicative<μ, ? extends F1<? super A, ? extends B>> f) {
      return super.apply(f);
    }

    @Override
    public <B> Maybe<B> bind(F1<? super A, ? extends Monad<μ, B>> fn) {
      return new MaybeBind<>(maybe.map(m -> m.bind(fn)));
    }

    @Override
    public boolean isSome() {
      return unWind(this).isSome();
    }
  
    @Override
    public Maybe<A> orElse(Maybe<A> maybe) {
      return unWind(this).orElse(maybe);
    }

    @Override
    public A orSome(A a) {
      return unWind(this).orSome(a);
    }

    @Override
    public A toNull() {
      return unWind(this).toNull();
    }

    @Override
    public <C> C match(F1<None<A>, C> f1, F1<Just<A>, C> f2) {
      return unWind(this).match(f1, f2);
    }
  }

  /**
   * @see drjoliv.jfunc.monad.Monad#liftM2(Monad, Monad, F2)
   */
  public static <A,B,C> Maybe<C> liftM2(Monad<Maybe.μ,A> m, Monad<Maybe.μ,B> m1, F2<? super A,? super B,C> fn) {
    return monad(Monad.liftM2(m, m1, fn));
  }

 /**
  * Returns an instance of Maybe that contains nothing.
  * @return an intstance of Maybe that contains Nothing.
  */
  @SuppressWarnings("unchecked")
  public static <B> Maybe<B> nothing() {
    return (Maybe<B>) None.instance;
  }

  /**
   * A helper function to convert/narrow a reference from an applicative to its underlying type.
   * @param monad the monad to be casted to its orginal type.
   * @return a maybe.
   */
  public static <B> Maybe<B> monad(Monad<Maybe.μ, B> monad) {
    return (Maybe<B>) monad;
  }

  /**
   * A helper function to convert/narrow a reference from an applicative to its underlying type.
   * @param applicative the applicative to be casted to its original type.
   * @return a maybe.
   */
  public static <B> Maybe<B> applicative(Applicative<μ, B> applicative) {
    return (Maybe<B>) applicative;
  }

  /**
   * Returns a maybe that has been evaluted.
   * @param maybe
   * @return a maybe whose internal data has been evaluated.
   */
  public static <A> Maybe<A> force(Maybe<A> maybe) {
    return unWind(maybe);
  }

  private static <A> Maybe<A> unWind(Maybe<A> maybe) {
    Maybe<A> ret = maybe;
    while (ret instanceof MaybeBind)
      ret = ((MaybeBind<A>)ret).maybe.value();
    return ret;
  }
}
