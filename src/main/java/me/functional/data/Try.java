package me.functional.data;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Function;
import java.util.function.Supplier;

import me.functional.data.Try.μ;
import static me.functional.data.Either.*;
import me.functional.hkt.Witness;
import me.functional.type.Monad;
import me.functional.type.MonadUnit;

public abstract class Try<A> implements Monad<Try.μ,A>{

  @Override
  public abstract <B> Try<B> fmap(Function<? super A, B> fn);

  @Override
  public abstract <B> Try<B> mBind(Function<? super A, ? extends Monad<μ, B>> fn);

  @Override
  public abstract <B> Try<B> semi(Monad<μ, B> mb);

  public abstract boolean isUnknown();
  public abstract boolean isSuccess();
  public abstract boolean isFailure();

  public static class μ implements Witness{}

  public static <A,E extends Exception> Try<A> with(SupplierWithCheckedException<A,E> runTry) {
    return new Unkown<>(() -> {
      try {
        return new Success<A>(runTry.get());
      } catch(Exception ex) {
        return new Failure<A>(ex);
      }
    });
  }

  public static MonadUnit<Try.μ> monadUnit = new MonadUnit<Try.μ>() {
    @Override
    public <A> Monad<μ, A> unit(A a) {
      return null;//tryWith(() -> a);
    }
  };

  @Override
  public MonadUnit<μ> yield() {
    return monadUnit;
  }

  public abstract Either<Exception,A> run();

  public abstract Try<A> recoverWith(Function<Exception, Try<A>> fn);

  public abstract Try<A> recover(Class<? extends Exception> cls, A a);

  private Try() {}

  private static class Unkown<A> extends Try<A> {

    private final Supplier<Try<A>> runTry;

    private Unkown(Supplier<Try<A>> runTry) {
      this.runTry = runTry;
    }

    @Override
    public <B> Try<B>  fmap(Function<? super A, B> fn) {
      return new Unkown<B>(() -> asTry(runTry.get().fmap(fn)));
    }

    @Override
    public <B> Try<B> mBind(Function<? super A, ? extends Monad<μ, B>> fn) {
      return new Unkown<B>(() -> asTry(runTry.get().mBind(fn)));
    }

    @Override
    public <B> Try<B> semi(Monad<μ, B> mb) {
      return mBind(a -> mb);
    }

    @Override
    public Either<Exception, A> run() {
      return runTry.get().run();
    }

    @Override
    public Try<A> recover(Class<? extends Exception> cls, A a) {
      return new Unkown<>(() -> runTry.get().recover(cls,a));
    }

    @Override
    public Try<A> recoverWith(Function<Exception, Try<A>> fn) {
      return new Unkown<>(() -> runTry.get().recoverWith(fn));
    }

    @Override
    public boolean isFailure() {
      return false;
    }

    @Override
    public boolean isSuccess() {
      return false;
    }

    @Override
    public boolean isUnknown() {
      return true;
    }
  }

  private static class Success<A> extends Try<A>  {

    A value;

    private Success(A value) {
      this.value = value;
    }

    @Override
    public <B> Try<B> fmap(Function<? super A, B> fn) {
      return new Success<>(fn.apply(value));
    }

    @Override
    public <B> Try<B> mBind(Function<? super A, ? extends Monad<μ, B>> fn) {
      return asTry(fn.apply(value));
    }

    @Override
    public <B> Try<B> semi(Monad<μ, B> mb) {
      return mBind(a -> mb);
    }

    @Override
    public Either<Exception, A> run() {
      return right(value);
    }

    @Override
    public Try<A> recover(Class<? extends Exception> cls, A a) {
      return this;
    }

    @Override
    public Try<A> recoverWith(Function<Exception, Try<A>> fn) {
      return this;
    }

    @Override
    public boolean isFailure() {
      return false;
    }

    @Override
    public boolean isSuccess() {
      return true;
    }
    
    @Override
    public boolean isUnknown() {
      return false;
    }
  }

  private static class Failure<A> extends Try<A> {

    private final Exception exception;

    public Failure(Exception exception) {
      this.exception = exception;
    }

    @Override
    public <B> Try<B> fmap(Function<? super A, B> fn) {
      return new Failure<B>(exception);
    }

    @Override
    public <B> Try<B> mBind(Function<? super A, ? extends Monad<μ, B>> fn) {
      return new Failure<B>(exception);
    }

    @Override
    public <B> Try<B> semi(Monad<μ, B> mb) {
      return mBind(a -> mb);
    }

    @Override
    public Either<Exception, A> run() {
      return left(exception);
    }

    @Override
    public Try<A> recover(Class<? extends Exception> cls, A a) {
      if (exception.getClass().equals(cls)) {
       return new Success<>(a); 
      }
      else {
        return this;
      }
    }

    @Override
    public Try<A> recoverWith(Function<Exception, Try<A>> fn) {
      return fn.apply(exception);
    }

    @Override
    public boolean isFailure() {
      return true;
    }
  
    @Override
    public boolean isSuccess() {
      return false;
    }
    
    @Override
    public boolean isUnknown() {
      return false;
    }
  }

  public static <B> Try<B> success(B b) {
    return new Success<>(b);
  }

  public static <B> Try<B> failure(Exception ex) {
    return new Failure<>(ex);
  }

  public static <A> Try<A> asTry(Monad<Try.μ,A> monad) {
    return (Try<A>) monad;
  }

  public interface SupplierWithCheckedException<T, E extends Exception> {
    T get() throws E;

    public static <T, E extends Exception> SupplierWithCheckedException<T, E> fromSupplier(Supplier<T> supplier) {
      return () -> {
        try {
          return supplier.get();
        } catch (Exception ex) {
          throw ex;
        }
      };
    }
  }

}
