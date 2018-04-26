package me.functional.data;

import static me.functional.data.Either.left;
import static me.functional.data.Either.right;
import static me.functional.functions.Eval.later;

import me.functional.functions.Eval;
import me.functional.functions.F1;
import me.functional.functions.Try0;
import me.functional.hkt.Hkt;
import me.functional.hkt.Witness;
import me.functional.type.Bind;
import me.functional.type.BindUnit;

public class Try<A> implements Bind<Try.μ,A>, Hkt<Try.μ,A> {

  private Eval<Try<A>> eval;

  private <E extends Exception> Try(Try0<A,E> runTry) {
    eval = later(() -> {
      try {
        return new Success<A>(runTry.get());
      } catch(Exception ex) {
        return new Failure<A>(ex);
      }
    });
  }

  private Try(Eval<Try<A>> eval) {
    this.eval = eval;
  }

  public static class μ implements Witness{}

  @Override
  public <B> Try<B> map(F1<? super A, B> fn) {
    return new Try<>(eval.map(t -> t.map(fn)));
  }

  @Override
  public <B> Try<B> bind(F1<? super A, ? extends Bind<μ, B>> fn) {
    return new Try<>(eval.map(t -> t.bind(fn)));
  }

  @Override
  public <B> Try<B> semi(Bind<μ, B> mb) {
    return bind(a -> mb);
  }

  public boolean isSuccess() {
    return eval.value().isSuccess();
  }

  public boolean isFailure() {
    return eval.value().isFailure();
  }


  public static <A,E extends Exception> Try<A> with(Try0<A,E> runTry) {
    return new Try<>(runTry);
  }

  @Override
  public BindUnit<μ> yield() {
    return Try::success;
  }

  public Either<Exception,A> run() {
    return eval.value().run();
  }

  public <E extends Exception> Try<A> recoverWith(Class<E> cls, 
      F1<E, Try<A>> fn) {
    return new Try<>(eval.map(t -> t.recoverWith(cls,fn)));
  }

  public Try<A> recover(Class<? extends Exception> cls, A a) {
   return new Try<>(eval.map(t -> t.recover(cls,a)));
  }

  private Try() {}

  private static class Success<A> extends Try<A>  {

    A value;

    private Success(A value) {
      this.value = value;
    }

    @Override
    public <B> Try<B> map(F1<? super A, B> fn) {
      return new Success<>(fn.call(value));
    }

    @Override
    public <B> Try<B> bind(F1<? super A, ? extends Bind<μ, B>> fn) {
      return asTry(fn.call(value));
    }

    @Override
    public <B> Try<B> semi(Bind<μ, B> mb) {
      return bind(a -> mb);
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
    public boolean isFailure() {
      return false;
    }

    @Override
    public boolean isSuccess() {
      return true;
    }
    
    @Override
    public <E extends Exception> Try<A> recoverWith(Class<E> cls, F1<E, Try<A>> fn) {
      return this;
    }
  }

  private static class Failure<A> extends Try<A> {

    private final Exception exception;

    public Failure(Exception exception) {
      this.exception = exception;
    }

    @Override
    public <B> Try<B> map(F1<? super A, B> fn) {
      return new Failure<B>(exception);
    }

    @Override
    public <B> Try<B> bind(F1<? super A, ? extends Bind<μ, B>> fn) {
      return new Failure<B>(exception);
    }

    @Override
    public <B> Try<B> semi(Bind<μ, B> mb) {
      return bind(a -> mb);
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
    public boolean isFailure() {
      return true;
    }
  
    @Override
    public boolean isSuccess() {
      return false;
    }
    

    @Override
    public <E extends Exception> Try<A> recoverWith(Class<E> cls, F1<E, Try<A>> fn) {
      if (exception.getClass().equals(cls)) {
       return fn.call((E)exception); 
      }
      else {
        return this;
      }
    }
  }

  public static <B> Try<B> success(B b) {
    return new Success<>(b);
  }

  public static <B> Try<B> failure(Exception ex) {
    return new Failure<>(ex);
  }

  public static <A> Try<A> asTry(Bind<Try.μ,A> monad) {
    return (Try<A>) monad;
  }

  public static <A> Try<A> asTry(Hkt<Try.μ,A> hkt) {
    return (Try<A>) hkt;
  }

}
