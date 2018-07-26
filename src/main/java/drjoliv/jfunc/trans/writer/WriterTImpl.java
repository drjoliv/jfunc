package drjoliv.jfunc.trans.writer;

import drjoliv.jfunc.applicative.Applicative;
import drjoliv.jfunc.contorl.eval.Eval;
import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.hkt.Hkt2;
import drjoliv.jfunc.hlist.T2;
import drjoliv.jfunc.monad.Monad;
import drjoliv.jfunc.monad.MonadFactory;
import drjoliv.jfunc.monoid.Monoid;

class WriterTImpl<M,W,A> extends WriterT<M,W,A> {

    WriterTImpl(Monad<M,T2<A,W>> runWriterT, Monoid<W> monoid, MonadFactory<M> innerMonadFactory
        ,  MonadFactory<Hkt2<writert, M, W>> writerTFactory) {
      super(runWriterT, monoid, innerMonadFactory, writerTFactory);
    }

    @Override
    public <B> WriterTImpl<M,W,B> map(F1<? super A, ? extends B> fn) {
      Monad<M,T2<B,W>> mb = runWriterT().map(t -> t.map1(fn));
      return new WriterTImpl<M,W,B>(mb, monoid(), getInnerMonadFactory(), yield());
    }

    @Override
    public <B> WriterT <M, W, B> apply(Applicative<Hkt2<writert, M, W>, ? extends F1<? super A, ? extends B>> applicative) {
      Monad<M,T2<F1<? super A, B>,W>> mf = ((WriterT <M, W, F1<? super A, B>>) applicative).runWriterT();
      Monad<M,T2<A,W>>                ma = runWriterT();
      Monad<M,T2<B,W>> mb =
        Monad.For(mf
            , tf      -> ma
            ,(tf,ta)  -> {
              A a = ta._1();
              W w = ta._2();
              return getInnerMonadFactory().unit(tf.bimap(f -> f.call(a) , ww -> monoid().mappend(w,ww)));
          });
      return new WriterTImpl<>(mb, monoid(), getInnerMonadFactory(), yield());
    }

    @Override
    public <B> WriterT<M,W,B> bind(F1<? super A, ? extends Monad<Hkt2<writert, M, W>, B>> fn) {
    Monad<M, T2<B,W>> mb = runWriterT()
      .For(  ta      -> monad(fn.call(ta._1())).runWriterT()
          , (ta, tb) -> {
            Eval<W> w = Eval.liftM2(ta._2, tb._2, monoid()::mappend);
            return getInnerMonadFactory().unit(tb.set2(w));
          });
      return new WriterTImpl<M,W,B>(mb , monoid(), getInnerMonadFactory(), yield());
    }

    @Override
    public <B> WriterT<M,W,B> semi(Monad<Hkt2<writert, M, W>, B> mb) {
      return bind(a -> mb);
    }
}

