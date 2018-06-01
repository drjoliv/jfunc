package drjoliv.fjava.control.bind;

import static drjoliv.fjava.control.bind.Trampoline.done$;

import drjoliv.fjava.control.Bind;
import drjoliv.fjava.control.BindUnit;
import drjoliv.fjava.functions.F0;
import drjoliv.fjava.functions.F1;
import drjoliv.fjava.hkt.Witness;

public abstract class Eval<A> implements Bind<Eval.μ,A>{

  public static class μ implements drjoliv.fjava.hkt.Witness{}

  @Override
  public BindUnit<μ> yield() {
    return Eval::now;
  }

  @Override
  public abstract <B> Eval<B> map(F1<? super A, B> fn);

  @Override
  public abstract <B> Eval<B> bind(F1<? super A, ? extends Bind<μ, B>> fn);

  @Override
  public abstract <B> Eval<B> semi(Bind<μ, B> mb);

  abstract public A value();

  public static <A> Eval<A> now(A a) {
    return new Now<>(a);
  }

  public static <A> Eval<A> later(F0<A> fn) {
    return new Later<>(fn);
  }

  public static <A> Eval<A> always(F0<A> fn) {
    return new Always<A>(fn);
  }

  public static <B>  Eval<B> asEval(Bind<μ, B> monad) {
    return (Eval<B>)monad; 
  }

  public static <B> F1<Bind<μ, B>, Eval<B>> asEval() {
    return Eval::asEval;
  }

  public static class Now<A> extends Eval<A> {

    final A value;

    private Now(A value) {
      this.value = value;
    }

    @Override
    public A value() {
      return value;
    }

    @Override
    public <B> Eval<B> map(F1<? super A, B> fn) {
      return later(fn.curry().call(value));
    }

    @Override
    public <B> Eval<B> bind(F1<? super A, ? extends Bind<μ, B>> fn) {
      return asEval(fn.call(value));
    }

    @Override
    public <B> Eval<B> semi(Bind<μ, B> mb) {
      return bind(a -> mb);
    }

  }

  public static class Later<A> extends Eval<A> {

    private volatile Trampoline<A> tramp;
    private A value;

    private Later(F0<A> fn) {
      this.tramp = done$(fn);
    }

    private Later(Trampoline<A> fn) {
      this.tramp = fn;
    }

    @Override
    public A value() {
      return value != null ? value : computeValue();
    }

    private synchronized A computeValue() {
      if(value == null) {
        value = tramp.result();
        tramp = null;
        return value;
      } else {
        return value;
      }
    }

    @Override
    public <B> Eval<B> map(F1<? super A, B> fn) {
      if(tramp != null) {
        return new Later<B>(tramp.map(fn));
      } else {
        return new Later<B>(done$(fn.curry().call(value)));
      }
    }

    @Override
    public <B> Eval<B> bind(F1<? super A, ? extends Bind<μ, B>> fn) {
      return asEval(Bind.join(map(fn.then(asEval()))));
    }
    
    @Override
    public <B> Eval<B> semi(Bind<μ, B> mb) {
      return bind(a -> mb);
    }
  }

  public static class Always<A> extends Eval<A> {
    private final Trampoline<A> tramp;

    private Always(F0<A> fn) {
      this.tramp = done$(fn);
    }

    private Always(Trampoline<A> tramp) {
      this.tramp = tramp;
    }

    @Override
    public A value() {
      return tramp.result();
    }

    @Override
    public <B> Eval<B> map(F1<? super A, B> fn) {
      return new Always<>(tramp.map(fn));
    }

    @Override
    public <B> Eval<B> bind(F1<? super A, ? extends Bind<μ, B>> fn) {
      return asEval(Bind.join(map(fn.then(asEval()))));
    }

    @Override
    public <B> Eval<B> semi(Bind<μ, B> mb) {
      return bind(a -> mb);
    }
  }
}
