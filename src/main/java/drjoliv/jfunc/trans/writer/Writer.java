package drjoliv.jfunc.trans.writer;

import drjoliv.jfunc.applicative.Applicative;
import drjoliv.jfunc.applicative.ApplicativeFactory;
import drjoliv.jfunc.contorl.eval.Eval;
import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.hkt.Hkt2;
import drjoliv.jfunc.hlist.T2;
import drjoliv.jfunc.monad.Identity;
import drjoliv.jfunc.monad.IdentityMonadFactory;
import drjoliv.jfunc.monad.Monad;
import drjoliv.jfunc.monad.MonadFactory;
import drjoliv.jfunc.monoid.Monoid;

/**
* Writer is a WriterT whose inner monad is the identity monad.
*/
public final class Writer<W,A> extends WriterT<Identity.μ,W,A> {

  Writer(Monad<Identity.μ,T2<A,W>> runWriterT, Monoid<W> monoid, MonadFactory<Hkt2<writert, Identity.μ, W>> writerFactory) {
    super(runWriterT, monoid, IdentityMonadFactory.instance(), writerFactory);
  }

  @Override
  public <B> Writer<W,B> map(F1<? super A, ? extends B> fn) {
    return new Writer<W,B>(runWriterT().map(t -> t.map1(fn)), monoid(), yield());
  }

  @Override
  public <B> Writer<W, B> apply(Applicative<Hkt2<writert, drjoliv.jfunc.monad.Identity.μ, W>, ? extends F1<? super A, ? extends B>> applicative) {

      Identity<T2<F1<? super A, B>,W>> wf = Identity.monad(((WriterT<Identity.μ,W,F1<? super A, B>>) applicative).runWriterT());

      Identity<T2<B,W>> mb = wf.bind(tf -> {
        final F1<? super A, B> f = tf._1();
        final W ww = tf._2();

        return runWriterT().map(t ->  t.bimap(a -> f.call(a), w -> monoid().mappend(w,ww))); 
      });

    return new Writer<W,B>(mb, monoid(), yield());
  }

  @Override
  public <B> Writer<W,B> bind(
      F1<? super A, ? extends Monad<Hkt2<writert, drjoliv.jfunc.monad.Identity.μ, W>, B>> fn) {
    Monad<Identity.μ, T2<B,W>> mb = runWriterT()
      .For(  ta      -> ((WriterT<Identity.μ,W,B>)fn.call(ta._1())).runWriterT()
          , (ta, tb) -> {
            Eval<W> w = Eval.liftM2(ta._2, tb._2, monoid()::mappend);
            return getInnerMonadFactory().unit(tb.set2(w));
          });
    return new Writer<W,B>(mb, monoid(), yield());
  }

  @Override
  public <B> Writer<W,B> semi(
    final Monad<Hkt2<writert, drjoliv.jfunc.monad.Identity.μ, W>, B> mb) {
    return bind(a -> mb);
  }

  @Override
  public Identity<T2<A, W>> runWriterT() {
    return (Identity<T2<A, W>>)super.runWriterT();
  }

  /**
   * Returns the unwrapped writer monad, yielding the return value {@code A} and accumulated subcomputations {@code W}.
   * @return the unwrapped writer monad, yielding the return value {@code A} and accumulated subcomputations {@code W}.
   */
  public T2<A,W> run() {
    return runWriterT().value();
  }

  /**
   * Returns the accumulated subcomputations.
   * @return the accumulated subcomputations.
   */
  public W log() {
    return runWriterT().value()._2();
  }

  /**
   * Returns the return value.
   * @return the return value.
   */
  public A exec() {
    return runWriterT().value()._1();
  }
}

