package drjoliv.jfunc.monad;

import drjoliv.jfunc.applicative.Applicative;
import drjoliv.jfunc.applicative.ApplicativeFactory;
import drjoliv.jfunc.contorl.eval.Eval;
import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.function.F2;
import drjoliv.jfunc.hkt.Hkt;

/**
 * A a contextt that simply applies the binded function with no further effect.
 * Identity is used within monad transforms, to emmbed a monad that has no extra effects.
 *
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public final class Identity<E> implements Monad<Identity.μ,E>, Hkt<Identity.μ,E> {

  /**
  * The witness type of Identity.
  */
  public static class μ {private μ(){}}

  private final Eval<E> e;

  private Identity(Eval<E> e) {
    this.e = e;
  }

  @Override
  public <A> Identity<A> map(F1<? super E, ? extends A> fn) {
    return new Identity<A>(e.map(fn));
  }

  @Override
  public <B> Identity<B> apply(Applicative<μ, ? extends F1<? super E, ? extends B>> f) {
    return monad(Monad.liftM2(this, (Identity<F1<? super E, B>>) f
          , (e, fn) -> fn.call(e)));
  }

  @Override
  public ApplicativeFactory<μ> pure() {
    return Identity::id;
  }

  @Override
  public <B> Identity<B> bind(F1<? super E, ? extends Monad<Identity.μ, B>> fn) {
    return new Identity<>(e.bind(val -> monad(fn.call(val)).e ));
  }

  @Override
  public <B> Identity<B> semi(Monad<Identity.μ, B> mIdentity) {
    return bind(e -> mIdentity);
  }

  @Override
  public MonadFactory<μ> yield() {
    return IdentityMonadFactory.instance();
  }

  /**
  * A strategy used to lift values into an Identity.
  */
  public static final ApplicativeFactory<Identity.μ> PURE = new ApplicativeFactory<Identity.μ>() {
    @Override
    public <A> Applicative<Identity.μ,A> pure(A a) {
      return id(a);
    }
  };

  /**
   * Constructs an identity from a given element.
   *
   * @param e the element to be wrapped into an identity.
   * @return the identity contianing {@code e}.
   */
  public static <E> Identity<E> id(E e) {
    return new Identity<E>(Eval.now(e));
  }

  /**
   * Returns the value contained within this identity.
   * @return the value contained within this identity.
   */
  public E value() {
    return e.value();
  }

  /**
   * Returns the value contained within this identity.
   * @return the value contained within this identity.
   */
  public Eval<E> value$() {
    return e;
  }

  /**
   * A helper function to convert/narrow a reference from a monad to its underlying type.
   * @param monad the monad that will be narrowed into an identity.
   * @return an identity.
   */
  public static <A> Identity<A> monad(Monad<Identity.μ, A> monad) {
    return (Identity<A>) monad;
  }

  /**
   * Transforms a hkt into an identity.
   * @param monad the hkt that will be narrowed into an identity.
   * @return an identity.
   */
  public static <A> Identity<A> asIdentity(Hkt<Identity.μ, A> monad) {
    return (Identity<A>) monad;
  }

  /**
   *@see drjoliv.jfunc.monad.Monad#For(Monad, F1, F2)
   */
  public static <A,B,C> Identity<C> For(Monad<Identity.μ,A> monad, F1<? super A,  ? extends Monad<Identity.μ,B>> fn,
      F2<? super A, ? super B, ? extends Monad<Identity.μ,C>> fn2) {
      return Identity.monad(Monad.For(monad, fn, fn2));
  } 
}
