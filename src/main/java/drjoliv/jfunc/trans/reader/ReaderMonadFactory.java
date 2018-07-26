package drjoliv.jfunc.trans.reader;

import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.monad.Identity;
import drjoliv.jfunc.monad.Identity.μ;
import drjoliv.jfunc.monad.IdentityMonadFactory;
import drjoliv.jfunc.monad.Monad;
import drjoliv.jfunc.monad.MonadFactory;
import drjoliv.jfunc.trans.reader.Reader.ReaderImpl;

public class ReaderMonadFactory<R> extends ReaderTMonadFactory<Identity.μ, R> {

  private static final ReaderMonadFactory INSTANCE = new ReaderMonadFactory();

  public static <R> ReaderMonadFactory<R> instance() {
    return INSTANCE;
  }

  ReaderMonadFactory() {
    super(IdentityMonadFactory.instance());
  }

  @Override
  public <A> Reader<R, A> unit(A a) {
    return new ReaderImpl<>(super.unit(a));
  }

  @Override
  public <A> Reader<R, A> pure(A a) {
    return new ReaderImpl<>(super.pure(a));
  }

  public <A> Reader<R, A> of(F1<R, Monad<Identity.μ,A>> fn) {
    return new ReaderImpl<>(super.of(fn));
  }
}
