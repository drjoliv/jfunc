package drjoliv.fjava.control.bind;

import static drjoliv.fjava.control.bind.Identity.id;
import static drjoliv.fjava.data.FList.flist;
import static drjoliv.fjava.data.T2.t2;

import drjoliv.fjava.control.Bind;
import drjoliv.fjava.control.BindUnit;
import drjoliv.fjava.data.FList;
import drjoliv.fjava.data.Maybe;
import drjoliv.fjava.data.Monoid;
import drjoliv.fjava.data.T2;
import drjoliv.fjava.data.Unit;
import drjoliv.fjava.functions.F0;
import drjoliv.fjava.functions.F1;
import drjoliv.fjava.functions.F3;
import drjoliv.fjava.hkt.Hkt2;
import drjoliv.fjava.hkt.Hkt3;
import drjoliv.fjava.hkt.Witness;

/**
 *
 *
 * @author Desonte 'drjoliv' Jolivet
 */
public class WriterT <M extends Witness,W,A> implements Bind<Hkt2<WriterT.μ,M,W>,A>, Hkt3<WriterT.μ,M,W,A>{

  public static class μ implements Witness{}

  private final F0<Bind<M,T2<A,W>>> runWriterT;
  private final Monoid<W> monoid;
  private final BindUnit<M> mUnit;

  private WriterT(F0<Bind<M,T2<A,W>>> runWriterT, Monoid<W> monoid, BindUnit<M> mUnit) {
    this.runWriterT = runWriterT;
    this.monoid     = monoid;
    this.mUnit      = mUnit;
  }

  @Override
  public <B> WriterT<M,W,B> map(F1<? super A, B> fn) {
    return new WriterT<M,W,B>(() -> runWriterT.call().map(p -> t2(fn.call(p._1),p._2)), monoid, mUnit);
  }

  @Override
  public <B> WriterT<M,W,B> bind(F1<? super A, ? extends Bind<Hkt2<μ, M, W>, B>> fn) {
    return new WriterT<M,W,B>(() -> {
      return Bind.For(runWriterT.call()
          , p     -> asWriterT(fn.call(p._1)).runWriterT.call()
          ,(p,p2) -> mUnit.unit(t2(p2._1, monoid.mappend(p._2,p2._2))));
    }
    , monoid, mUnit);
  }

  @Override
  public <B> WriterT<M,W,B> semi(Bind<Hkt2<μ, M, W>, B> mb) {
    return bind(a -> mb);
  }

  @Override
  public BindUnit<Hkt2<μ, M, W>> yield() {
    return new BindUnit<Hkt2<μ, M, W>>(){
      @Override
      public <B> Bind<Hkt2<μ, M, W>, B> unit(B b) {
        return WriterT.<M,W,B>unit().call(mUnit,monoid,b);
      }
    };
  }

  /**
   *
   *
   * @return
   */
  public Monoid<W> monoid() {
    return monoid;
  }

  /**
   *
   *
   * @return
   */
  public Bind<M,T2<A,W>> runWriterT(){
    return runWriterT.call();
  }

  /**
   *
   *
   * @return
   */
  public Bind<M,W> execWriterT(){
    return runWriterT.call().map(p -> p._2);
  }

  /**
   *
   *
   * @return
   */
  public static <M extends Witness,W,B> F3<BindUnit<M>, Monoid<W>, B, WriterT<M,W,B>>unit() {
    return (mUnit, monoid, b) -> new WriterT<M,W,B>(() -> mUnit.unit(t2(b,monoid.mempty())), monoid, mUnit);
  }

  @SuppressWarnings("unchecked")
  public static <M extends Witness, W, A> WriterT<M, W, A> asWriterT(Bind<Hkt2<Writer.μ, M, W>, A> mb) {
    return (WriterT<M, W, A>) mb;
  }

  /**
  *
  */
  public static final class Writer<W,A> extends WriterT<Identity.μ,W,A> {

    private Writer(F0<Bind<Identity.μ,T2<A,W>>> runWriterT, Monoid<W> monoid) {
      super(runWriterT, monoid, Identity::id);
    }

    /**
     *
     *
     * @return
     */
    public T2<A,W> run() {
      return runWriterT().value();
    }

    /**
     *
     *
     * @return
     */
    public W log() {
      return runWriterT().value()._2;
    }

    /**
     *
     *
     * @return
     */
    public A exec() {
      return runWriterT().value()._1;
    }

    @Override
    public <B> Writer<W,B> map(F1<? super A, B> fn) {
      final WriterT<Identity.μ,W,B> w = super.map(fn);
      return new Writer<W,B>(w.runWriterT,w.monoid);
    }

    @Override
    public <B> Writer<W,B> bind(
        F1<? super A, ? extends Bind<Hkt2<μ, drjoliv.fjava.control.bind.Identity.μ, W>, B>> fn) {
      final WriterT<Identity.μ,W,B> w = super.bind(fn);
      return new Writer<W,B>(w.runWriterT,w.monoid);
    }

    @Override
    public <B> Writer<W,B> semi(
      final Bind<Hkt2<μ, drjoliv.fjava.control.bind.Identity.μ, W>, B> mb) {
      final WriterT<Identity.μ,W,B> w = super.semi(mb);
      return new Writer<W,B>(w.runWriterT,w.monoid);
    }

    @Override
    public Identity<T2<A, W>> runWriterT() {
      return (Identity<T2<A, W>>)super.runWriterT();
    }

    /**
     *
     *
     * @param flist
         * @param <M>
        * @param <W>
     * @param <A>
    * @return
     */
    public static <M extends Witness, W, A> Maybe<WriterT<M,W,FList<A>>> sequence(FList<WriterT<M,W,A>> flist) {
        return flist
          .map(ma -> ma.map(a -> flist(a)))
          .reduce((m1, m2) -> asWriterT(Bind.liftM2(m1, m2, (a1, a2) -> a1.concat(a2))));
    }

    /**
     *
     *
     * @param flist
        * @param <M>
        * @param <W>
     * @param <A>
  * @return
     */
    public static <M extends Witness, W, A> Maybe<WriterT<M,W,Unit>> sequence_(FList<WriterT<M,W,A>> flist) {
      return flist
        .reduce((m1,m2) -> m1.semi(m2))
        .map(w -> w.map(a -> Unit.unit));
    }

    /**
     *
     *
     * @param a
     * @param w
     * @param m
      * @param <W>
     * @param <A>
    * @return
     */
    public static <W,A> Writer<W,A> writer(A a, W w, Monoid<W> m) {
      return new Writer<W,A>(() -> id(t2(a,w)), m);
    }

    /**
     *
     *
     * @param a the value contained within the writer.
     * @param m the monoiod operation used when writiing.
     * @param <W> the log type of the created wirter.
     * @param <A> the type of the value contained within the writer.
     * @return creates a writer from the given arguments.
     */
    public static <W,A> Writer<W,A> writer(A a, Monoid<W> m) {
      return new Writer<W,A>(() -> id(t2(a,m.mempty())), m);
    }

    /**
     *
     * Creates a first class function that produces writers.
     * @param w the monoid used within the created function to create writers.
     * @param <W> the log type of the created wirter.
     * @param <A> the type of the value contained within the writer.
     * @return a  first class function that produces writers.
     */
    public static <A,W> F1<A,Writer<W,A>> writer(Monoid<W> w) {
      return a -> writer(a,w);
    }

    /**
     * Converts a instnace of bind whose witness type is {@code Hkt2<Writer.μ, Identity.μ, W>} to a instance of writer.
     * @param mb the argument to convert to an instance of writer.
     * @return a writer.
     */
    public static <W extends Witness,A> Writer<W,A> asWriter(Bind<Hkt2<Writer.μ, Identity.μ, W>, A> mb) {
      if(mb instanceof WriterT)
        return new Writer<W,A>(asWriterT(mb).runWriterT, asWriterT(mb).monoid);
      else
        return (Writer<W,A>) mb;
    }
  }
}
