package drjoliv.jfunc.trans.writer;

import static drjoliv.jfunc.hlist.T2.t2;
import static drjoliv.jfunc.monad.Identity.id;

import drjoliv.jfunc.applicative.Applicative;
import drjoliv.jfunc.applicative.ApplicativeFactory;
import drjoliv.jfunc.contorl.eval.Eval;
import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.hkt.Hkt2;
import drjoliv.jfunc.hkt.Hkt3;
import drjoliv.jfunc.hlist.T2;
import drjoliv.jfunc.monad.Identity;
import drjoliv.jfunc.monad.Monad;
import drjoliv.jfunc.monad.MonadFactory;
import drjoliv.jfunc.monoid.Monoid;


/**
 * The WriterT is a monad that produces a stream of data in addition to a computed value, the stream of subcomputations are collected by combining them via a monoid.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public abstract class WriterT<M,W,A> implements Monad<Hkt2<writert,M,W>,A>, Hkt3<writert,M,W,A> {

  private final Monad<M,T2<A,W>> runWriterT;
  private final Monoid<W> monoid;
  private final MonadFactory<M> innerMonadFactory;
  private final MonadFactory<Hkt2<writert, M, W>> writerTFactory;

  WriterT(Monad<M,T2<A,W>> runWriterT, Monoid<W> monoid, MonadFactory<M> innerMonadFactory, MonadFactory<Hkt2<writert, M, W>> writerTFactory) {
    this.runWriterT          = runWriterT;
    this.monoid              = monoid;
    this.innerMonadFactory   = innerMonadFactory;
    this.writerTFactory      = writerTFactory;
  }

  MonadFactory<M> getInnerMonadFactory() {
    return innerMonadFactory;
  }

  @Override
  public abstract <B> WriterT<M,W,B> map(F1<? super A, ? extends B> fn);

  @Override
  public abstract <B> WriterT <M, W, B> apply(Applicative<Hkt2<writert, M, W>, ? extends F1<? super A, ? extends B>> applicative);

  @Override
  public ApplicativeFactory<Hkt2<writert, M, W>> pure() {
    return writerTFactory;
  }

  @Override
  public abstract <B> WriterT<M,W,B> bind(F1<? super A, ? extends Monad<Hkt2<writert, M, W>, B>> fn);

  @Override
  public abstract <B> WriterT<M,W,B> semi(Monad<Hkt2<writert, M, W>, B> mb);

  @Override
  public MonadFactory<Hkt2<writert, M, W>> yield() {
    return writerTFactory;
  }

  /**
   * Returns the monoid used by this WriterT.
   * @return the monoid used by this WriterT.
   */
  public final Monoid<W> monoid() {
    return monoid;
  }

  /**
   * Returns the inner monad containing two values, the main computaion {@code A } and the accumulated subcomputations {@code W}.
   * @return the inner monad containing two values the main computaion {@code A } and the accumulated subcomputations {@code W}.
   */
  public Monad<M,T2<A,W>> runWriterT(){
    return runWriterT;
  }

  /**
   * Returns the inner monad containing the accumulated subcomputations {@code W}.
   * @return the inner monad containing the accumulated subcomputations {@code W}.
   */
  public Monad<M,W> execWriterT(){
    return runWriterT.map(T2::_2);
  }

  /**
   * A helper function to convert/narrow a reference from a monad to its underlying type.
   * @param monad the writerT to be casted to its original type.
   * @return a writerT.
   */
  public static <M,W,A> WriterT<M,W,A> monad(Monad<Hkt2<writert,M,W>,A> monad) {
    return (WriterT<M,W,A>)monad;
  }
}
