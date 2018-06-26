package drjoliv.fjava.trans.reader;

import static drjoliv.fjava.adt.FList.flist;

import java.util.function.Function;

import drjoliv.fjava.adt.FList;
import drjoliv.fjava.adt.Maybe;
import drjoliv.fjava.adt.Unit;
import drjoliv.fjava.applicative.Applicative;
import drjoliv.fjava.applicative.ApplicativePure;
import drjoliv.fjava.functions.F1;
import drjoliv.fjava.functions.F2;
import drjoliv.fjava.hkt.Hkt2;
import drjoliv.fjava.hkt.Hkt3;
import drjoliv.fjava.hkt.Witness;
import drjoliv.fjava.monad.Monad;
import drjoliv.fjava.monad.MonadUnit;
import drjoliv.fjava.trans.reader.ReaderT.μ;
import drjoliv.fjava.monad.Identity;

/**
 * The ReaderT monad.
 * @author Desonte 'drjoliv' Jolivet
 */
public class ReaderT <M extends Witness,R,A> implements Monad<Hkt2<ReaderT.μ,M,R>,A>, Hkt3<ReaderT.μ,M,R,A> {

  public static class μ implements Witness {private μ(){}}

  private final F1<R,Monad<M,A>> runReaderT;
  private final MonadUnit<M> mUnit;


  private ReaderT(F1<R,Monad<M,A>> runReaderT, MonadUnit<M> mUnit) {
    this.runReaderT = runReaderT;
    this.mUnit = mUnit;
  }

  @Override
  public <B> ReaderT<M,R,B> map(F1<? super A, ? extends B> fn) {
      return readerT(runReaderT.map(m -> m.map(fn)), mUnit);
  }

  @Override
  public <B> ReaderT <M,R,B> apply(Applicative<Hkt2<μ, M, R>, ? extends F1<? super A, ? extends B>> applicative) {
    return new ReaderT<M,R,B>(r -> {
      ReaderT<M,R,F1<? super A, B>> f = (ReaderT<M,R,F1<? super A, B>>) applicative;
      return runReader(r).apply(f.runReader(r));
    } , mUnit);
  }

  @Override
  public ApplicativePure<Hkt2<μ, M, R>> pure() {
    return new ApplicativePure<Hkt2<drjoliv.fjava.trans.reader.ReaderT.μ, M, R>>() {
      @Override
      public <B> Monad<Hkt2<μ, M, R>, B> pure(B b) {
        return ReaderT.<M,R,B>unit().call(mUnit,b);
      }
    };
  }

  @Override
  public MonadUnit<Hkt2<drjoliv.fjava.trans.reader.ReaderT.μ, M, R>> yield() {
    return new MonadUnit<Hkt2<drjoliv.fjava.trans.reader.ReaderT.μ, M, R>>() {
      @Override
      public <B> Monad<Hkt2<μ, M, R>, B> unit(B b) {
        return ReaderT.<M,R,B>unit().call(mUnit,b);
      }
    };
  }

  @Override
  public <B> ReaderT<M,R,B> bind(F1<? super A, ? extends Monad<Hkt2<ReaderT.μ, M, R>, B>> fn) {
    return readerT(r -> {
        return Monad.For(runReader(r)
                    , a -> monad(fn.call(a)).runReader(r));
    }, mUnit);
  }

  @Override
  public <B> ReaderT<M,R,B> semi(Monad<Hkt2<μ, M, R>, B> mb) {
    return bind(a -> mb);
  }


  /**
  * Lifts the given computation into a readerT.
  * @param ma A computation that will be lifted(wrapped) into the context of a readerT
  * @param <M> the witness type of the contained compuation or context.
  * @param <R> the type of the enviroment.
  * @param <A> the type of the value obtained after running this readerT.
  * @return a ReaderT whose emmbed with the given computation.
  */
  public static <M extends Witness,R,A> ReaderT<M,R,A> lift(Monad<M,A> ma) {
    return ReaderT.readerT(r -> ma, ma.yield());
  }


  /**
   * Creates a monadic like context from the given enviroment.
   * @param r the envirement needed to to compute a bind value.
   * @return a bind value.
   */
  public Monad<M,A> runReader(R r) {
   return runReaderT.call(r); 
  }

  /**
   * @param <M> the witness type of the contained compuation or context.
   * @param <R> the type of the enviroment.
   * @param <M>
   * @param <R>
   * @return
   */
  public static <M extends Witness, R> ReaderT<M,R,R> ask(MonadUnit<M> mUnit) {
    return ReaderT.readerT(r -> mUnit.unit(r), mUnit);
  }

  /**
   * @param <M> the witness type of the contained compuation or context.
   * @param <R> the type of the enviroment.
   *
   * @return
   */
  public static <M extends Witness, R> F1<MonadUnit<M>, ReaderT<M,R,R>> ask() {
    return (mUnit) -> ReaderT.readerT(r -> mUnit.unit(r), mUnit);
  }

  /**
   *
   * @param <M> the witness type of the contained compuation or context.
   * @param <R> the type of the enviroment.
   * @param <A> the type of the value obtained after running this readerT.
   *
   * @return
   */
  public static <M extends Witness, R, A> F2<F1<R,R>, ReaderT<M,R,A>, ReaderT<M,R,A>> local() {
    return (fn, m) -> {
      return ReaderT.readerT(fn.then(m.runReaderT), m.mUnit);
    };
  }

  /**
   * @param <M> the witness type of the contained compuation or context.
   * @param <R> the type of the enviroment.
   * @param <A> the type of the value obtained after running this readerT.
   *
   * @return
   */
  public static <M extends Witness, R, A> ReaderT<M,R,A> local(F1<R,R> fn, ReaderT<M,R,A> m) {
      return ReaderT.readerT(fn.then(m.runReaderT), m.mUnit);
  }

  /**
   *
   *
   * @param runReaderT
   * @param mUnit
   * @return
   */
  public static <M extends Witness, R, A> ReaderT<M, R, A> readerT(F1<R, Monad<M, A>> runReaderT, MonadUnit<M> mUnit) {
    return new ReaderT<M, R, A>(runReaderT, mUnit);
  }

  @SuppressWarnings("unchecked")
  public static <M extends Witness, R, A> ReaderT<M, R, A> monad(Monad<Hkt2<ReaderT.μ, M, R>, A> wider) {
    return (ReaderT<M, R, A>) wider;
  }

  private static <M extends Witness, R, A> F2<MonadUnit<M>, A, ReaderT<M, R, A>> unit() {
    return (mUnit, a) -> new ReaderT<M, R, A>(r -> mUnit.unit(a), mUnit);
  }


  /**
  * A ReaderT whose inner monad is the Identity monad.
  */
  public final static class Reader<R,A> extends ReaderT<Identity.μ,R,A>{

    private Reader(F1<R,Monad<Identity.μ,A>> runReaderT) {
      super(runReaderT, Identity::id);
    }

    @Override
    public <B> Reader<R,B> semi(
        Monad<Hkt2<μ, drjoliv.fjava.monad.Identity.μ, R>, B> mb) {
      return new Reader<R,B>(super.semi(mb).runReaderT);
    }

    @Override
    public <B> Reader<R,B> bind(
        F1<? super A, ? extends Monad<Hkt2<μ, drjoliv.fjava.monad.Identity.μ, R>, B>> fn) {
      return new Reader<R,B>(super.bind(fn).runReaderT);
    }

    @Override
    public <B> Reader<R,B> map(F1<? super A, ? extends B> fn) {
      return new Reader<R,B>(((ReaderT<Identity.μ,R,B>)super.map(fn)).runReaderT);
    }

    @Override
    public Identity<A> runReader(R r) {
      return (Identity<A>)super.runReader(r);
    }

    public A run(R r) {
      return ((Identity<A>)super.runReader(r)).value();
    }

    public static <R, A> Reader<R, A> asReader(Monad<Hkt2<ReaderT.μ, Identity.μ, R>, A> wider) {
      if(wider instanceof ReaderT)
        return new Reader<R,A>(asReaderT(wider).runReaderT);
      else
        return (Reader<R, A>) wider;
    }

    public static <R, A> Reader<R, A> reader(F1<R,A> fn) {
      return new Reader<R,A>(r -> Identity.id(fn.call(r)));
    }

    public static <R, A> Reader<R, A> reader(A a) {
      return reader(r -> a);
    }

    public static <R> Reader<R, R> readerAsk() {
      return reader(r -> r);
    }

    public static <R,A> Reader<R, A> readerLocal(F1<R,R> fn, Reader<R,A> ma) {
      return asReader(local(fn,ma));
    }
  }
}
