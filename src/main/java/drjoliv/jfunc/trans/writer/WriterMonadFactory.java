package drjoliv.jfunc.trans.writer;

import drjoliv.jfunc.hlist.T2;
import drjoliv.jfunc.monad.Identity;
import drjoliv.jfunc.monad.Identity.μ;
import drjoliv.jfunc.monad.IdentityMonadFactory;
import drjoliv.jfunc.monad.Monad;
import drjoliv.jfunc.monoid.Monoid;

public class WriterMonadFactory<W> extends WriterTMonadFactory<Identity.μ, W> {

  WriterMonadFactory(Monoid<W> monoid) {
    super(IdentityMonadFactory.instance(), monoid);
  }

  public static <W> WriterMonadFactory<W> writerFactory(Monoid<W> monoid) {
    return new WriterMonadFactory<>(monoid);
  }

  @Override
  public <A> Writer<W, A> pure(A a) {
    return new Writer<>(Identity.id(T2.t2(a, getMonoid().mempty())), getMonoid(), this);
  }

  @Override
  public <A> Writer<W, A> unit(A a) {
    return new Writer<>(Identity.id(T2.t2(a, getMonoid().mempty())), getMonoid(), this);
  }

  @Override
  public <A> Writer<W, A> lift(Monad<μ, A> innerMonad) {
    return new Writer<>( innerMonad.map(a -> T2.t2(a, getMonoid().mempty())), getMonoid(), this);
  }

  public <A> Writer<W, A> of(A a, W w) {
    return new Writer<>(Identity.id(T2.t2(a, w)), getMonoid(), this);
  }

}
