package drjoliv.fjava.control.bind;

import static drjoliv.fjava.data.FList.flist;

import java.util.function.Function;

import drjoliv.fjava.control.Bind;
import drjoliv.fjava.control.BindUnit;
import drjoliv.fjava.data.FList;
import drjoliv.fjava.data.Maybe;
import drjoliv.fjava.data.Unit;
import drjoliv.fjava.functions.F1;
import drjoliv.fjava.functions.F2;
import drjoliv.fjava.hkt.Hkt2;
import drjoliv.fjava.hkt.Hkt3;
import drjoliv.fjava.hkt.Witness;

/**
 *
 * @author Desonte 'drjoliv' Jolivet
 */
public class ReaderT <M extends Witness,R,A> implements Bind<Hkt2<ReaderT.μ,M,R>,A>, Hkt3<ReaderT.μ,M,R,A> {

  public static interface μ extends Witness {}

  private final F1<R,Bind<M,A>> runReaderT;
  private final BindUnit<M> mUnit;

  private ReaderT(F1<R,Bind<M,A>> runReaderT, BindUnit<M> mUnit) {
    this.runReaderT = runReaderT;
    this.mUnit = mUnit;
  }

  @Override
  public BindUnit<Hkt2<drjoliv.fjava.control.bind.ReaderT.μ, M, R>> yield() {
    return new BindUnit<Hkt2<drjoliv.fjava.control.bind.ReaderT.μ, M, R>>() {
      @Override
      public <B> Bind<Hkt2<μ, M, R>, B> unit(B b) {
        return ReaderT.<M,R,B>unit().call(mUnit,b);
      }
    };
  }

  @Override
  public <B> ReaderT<M,R,B> bind(F1<? super A, ? extends Bind<Hkt2<ReaderT.μ, M, R>, B>> fn) {
    return readerT(r -> {
      Bind<M,A> inner = runReaderT.call(r);
      Bind<M,B> next  = inner.bind(a ->  asReaderT(fn.call(a)).runReaderT.call(r));
      return next; 
    }, mUnit);
  }

  @Override
  public <B> ReaderT<M,R,B> semi(Bind<Hkt2<μ, M, R>, B> mb) {
    return bind(a -> mb);
  }

  @Override
  public <B> ReaderT<M,R,B> map(F1<? super A, B> fn) {
      return readerT((R r) -> {
        Bind<M,A> ma = runReaderT.call(r);
        Bind<M,B> mb = ma.map(fn);
       return mb;
    }, mUnit);
  }

  /**
  * Lifts the given computation into a readerT.
  * @param ma A computation that will be lifted(wrapped) into the context of a readerT
  * @param <M> the witness type of the contained compuation or context.
  * @param <R> the type of the enviroment.
  * @param <A> the type of the value obtained after running this readerT.
  * @return a ReaderT whose emmbed with the given computation.
  */
  public static <M extends Witness,R,A> ReaderT<M,R,A> lift(Bind<M,A> ma) {
    return ReaderT.readerT(r -> ma, ma.yield());
  }

  /**
  * Computes each readerT in a list, and returns a readerT containing the list of the results.
  *
  * @param readers the list of readerTs, that will be transformed into a readerT of list.
  * @param <M> the witness type of the contained compuation or context.
  * @param <R> the type of the enviroment.
  * @param <A> the type of the value obtained after running this readerT.
  * @return a readerT that computes a list of values
  */
  public static <M extends Witness,R,A> ReaderT<FList.μ,R,Bind<M,A>> compose(FList<ReaderT<M,R,A>> readers) {
    return ReaderT.readerT((R r) -> {
      return readers.map(reader -> reader.runReader(r));
    },FList.unit );
  }

  /**
   * Creates a monadic like context from the given enviroment.
   * @param r the envirement needed to to compute a bind value.
   * @return a bind value.
   */
  public Bind<M,A> runReader(R r) {
   return runReaderT.call(r); 
  }

  /**
   * @param <M> the witness type of the contained compuation or context.
   * @param <R> the type of the enviroment.
   * @param <M>
   * @param <R>
   * @return
   */
  public static <M extends Witness, R> ReaderT<M,R,R> ask(BindUnit<M> mUnit) {
    return ReaderT.readerT(r -> mUnit.unit(r), mUnit);
  }

  /**
   * @param <M> the witness type of the contained compuation or context.
   * @param <R> the type of the enviroment.
   *
   * @return
   */
  public static <M extends Witness, R> F1<BindUnit<M>, ReaderT<M,R,R>> ask() {
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
   * @param <M> the witness type of the contained compuation or context.
   * @param <R> the type of the enviroment.
   * @param <A> the type of the value obtained after running this readerT.
   * @return
   */
  public static <M extends Witness, R, A> ReaderT<M, R, A> readerT(F1<R, Bind<M, A>> runReaderT, BindUnit<M> mUnit) {
    return new ReaderT<M, R, A>(runReaderT, mUnit);
  }

  /**
   *
   *
   * @param wider
   * @param <M> the witness type of the contained compuation or context.
   * @param <R> the type of the enviroment.
   * @param <A> the type of the value obtained after running this readerT.
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <M extends Witness, R, A> ReaderT<M, R, A> asReaderT(Bind<Hkt2<ReaderT.μ, M, R>, A> wider) {
    return (ReaderT<M, R, A>) wider;
  }

  /**
   * @param <M> the witness type of the contained compuation or context.
   * @param <R> the type of the enviroment.
   * @param <A> the type of the value obtained after running this readerT.
   * @return
   */
  public static <M extends Witness, R, A> F2<BindUnit<M>, A, ReaderT<M, R, A>> unit() {
    return (mUnit, a) -> new ReaderT<M, R, A>(r -> mUnit.unit(a), mUnit);
  }

  /**
   *
   * @param <M> the witness type of the contained compuation or context.
   * @param <R> the type of the enviroment.
   * @param <A> the type of the value obtained after running this readerT.
   *
   * @param flist
   * @return
   */
  public static <M extends Witness, R, A> Maybe<ReaderT<M,R,FList<A>>> sequence(FList<ReaderT<M,R,A>> flist) {
    return flist
      .map(ma -> ma.map(a -> flist(a)))
      .reduce((m1, m2) -> asReaderT(Bind.liftM2(m1, m2, (a1, a2) -> a1.concat(a2))));
  }

  /**
   *
   *
   * @param flist
   * @return
   */
  public static <M extends Witness, R, A> Maybe<ReaderT<M,R,Unit>> sequence_(FList<ReaderT<M,R,A>> flist) {
    return flist
      .reduce((m1, m2) -> m1.semi(m2))
      .map(r -> r.map(a -> Unit.unit));
  }

  /**
  *
  */
  public final static class Reader<R,A> extends ReaderT<Identity.μ,R,A>{

    private Reader(F1<R,Bind<Identity.μ,A>> runReaderT) {
      super(runReaderT, Identity::id);
    }

    @Override
    public <B> Reader<R,B> semi(
        Bind<Hkt2<μ, drjoliv.fjava.control.bind.Identity.μ, R>, B> mb) {
      return new Reader<R,B>(super.semi(mb).runReaderT);
    }

    @Override
    public <B> Reader<R,B> bind(
        F1<? super A, ? extends Bind<Hkt2<μ, drjoliv.fjava.control.bind.Identity.μ, R>, B>> fn) {
      return new Reader<R,B>(super.bind(fn).runReaderT);
    }

    @Override
    public <B> Reader<R,B> map(F1<? super A, B> fn) {
      return new Reader<R,B>(super.map(fn).runReaderT);
    }

    @Override
    public Identity<A> runReader(R r) {
      return (Identity<A>)super.runReader(r);
    }

    /**
     *
     * @param r
     * @return
     */
    public A run(R r) {
      return ((Identity<A>)super.runReader(r)).value();
    }

    /**
     *
     * @param wider
     * @return
     */
    public static <R, A> Reader<R, A> asReader(Bind<Hkt2<ReaderT.μ, Identity.μ, R>, A> wider) {
      if(wider instanceof ReaderT)
        return new Reader<R,A>(asReaderT(wider).runReaderT);
      else
        return (Reader<R, A>) wider;
    }

    /**
     *
     * @param fn
     * @return
     */
    public static <R, A> Reader<R, A> reader(Function<R,A> fn) {
      return new Reader<R,A>(r -> Identity.id(fn.apply(r)));
    }

    /**
     * A Reader
     * @param a
     * @return
     */
    public static <R, A> Reader<R, A> reader(A a) {
      return reader(r -> a);
    }

    /**
     *
     *
     * @return
     */
    public static <R> Reader<R, R> readerAsk() {
      return reader(r -> r);
    }

    /**
     *
     *
     * @param fn
     * @param ma
     * @return
     */
    public static <R,A> Reader<R, A> readerLocal(F1<R,R> fn, Reader<R,A> ma) {
      return asReader(local(fn,ma));
    }
  }
}
