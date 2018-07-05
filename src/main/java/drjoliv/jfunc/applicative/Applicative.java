package drjoliv.jfunc.applicative;

import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.function.F2;
import drjoliv.jfunc.function.F3;
import drjoliv.jfunc.function.F4;
import drjoliv.jfunc.function.F5;
import drjoliv.jfunc.functor.Functor;

/**
 * An applicative lies between a functor and monad, by using an applicative you are able to map a function that's inside a functor over another functor.
 * @author Desont 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public interface Applicative<M, A> extends Functor<M,A> {

  /**
   * Returns a strategy for creating an applicative of this type.
   * @return a strategy for creating an applicative of this type.
   */
  public ApplicativePure<M> pure();

  /**
   * Apply a function contained within an applicative to the contents of this applicative.
   * @param f an applicative containing a function.
   * @return an applicative who contains the result of applying the argument to this applicative.
   */
  public <B> Applicative<M,B> apply(Applicative<M, ? extends F1<? super A, ? extends B>> f);

  @Override
  public <B> Applicative<M,B> map(F1<? super A, ? extends B> fn);

  @Override
  public default <B,C> Applicative<M,F1<B,C>> map(F2<? super A, B, C> fn) {
    return (Applicative<M,F1<B,C>>)Functor.super.map(fn);
  }

  @Override
  public default <B,C,D> Applicative<M, F1<B, F1<C,D>>>  map(F3<? super A, B, C, D> fn) {
    return (Applicative<M, F1<B, F1<C,D>>>)Functor.super.map(fn);
  }

  @Override
  public default <B,C,D,E> Applicative<M,F1<B, F1<C, F1<D,E>>>>  map(F4<? super A, B, C, D, E> fn) {
    return (Applicative<M,F1<B, F1<C, F1<D,E>>>>)Functor.super.map(fn);
  }

  @Override
  public default <B,C,D,E,G> Applicative<M,F1<B,F1<C,F1<D,F1<E,G>>>>>  map(F5<? super A, B, C, D, E, G> fn) {
    return (Applicative<M,F1<B,F1<C,F1<D,F1<E,G>>>>>)Functor.super.map(fn);
  }
}
