package drjoliv.jfunc;

import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.monad.Monad;

public class Kleisli {

  public static <M, A, B, C> F1<A,Monad<M,C>> composeK(F1<A,Monad<M,B>> one, F1<B,Monad<M,C>> two) {
    return one.then(mb -> Monad.join(mb.map(two)));

  }

  //@Override
  //public <C> F1<C, Monad<M, B>> before(F1<? super C, ? extends A> fn) {
  //  // TODO Auto-generated method stub
  //  return F1.super.before(fn);
  //}

  ////karrow a -> m b
  ////

  //@Override
  //public Monad<M, B> call(A a) {
  //  // TODO Auto-generated method stub
  //  return null;
  //}

  //public <C> Kleisli<M,A,C> composeK(Kleisli<M,B,C> k) {
  // F1<A,Monad<M,C>> mc = then(mb -> Monad.join(mb.map(k)));
  // return null;
  //}

  //@Override
  //public F1<A, F0<Monad<M, B>>> curry() {
  //  // TODO Auto-generated method stub
  //  return F1.super.curry();
  //}
}
