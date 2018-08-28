package drjoliv.jfunc.contorl;

import static drjoliv.jfunc.contorl.maybe.Maybe.maybe;

import drjoliv.jfunc.contorl.maybe.Maybe;
import drjoliv.jfunc.function.F0;
import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.function.P1;

public class CaseOf<A> {

  private final A a;

  public CaseOf(A a) {
    this.a = a;
  }

  public static <A> CaseOf<A> caseOf(A a) {
    return new CaseOf<>(a);
  }

  public <B> Cases<A,B> of(A a, F0<B> f) {
    return of(eq(a), f);
  }

  public <B> Cases<A,B> of(P1<A> p, F0<B> f) {
    Maybe<B> m = p.test(a) ? maybe(f.call()) : Maybe.nothing();
    return new Cases<>(a, m);
  }

  public <B> Cases<A,B> of(P1<A> p, F1<A,B> f) {
    Maybe<B> m = p.test(a) ? maybe(f.call(a)) : Maybe.nothing();
    return new Cases<>(a, m);
  }

  public static class Cases<A,B> {

    private final A a;
    private Maybe<B> value;

    private Cases(A a, Maybe<B> value) {
      this.a = a;
      this.value = value;
    }

    public Cases<A,B> of(A a, F0<B> f) {
      return of(CaseOf.eq(a), f);
    }

    public Cases<A,B> of(P1<A> p, F0<B> f) {
      if(value.isSome())
        return this;
      else {
        Maybe<B> m = p.test(a) ? maybe(f.call()) : Maybe.nothing();
        return new Cases<>(a, m);
      }
    }

    public Cases<A,B> of(P1<A> p, F1<A,B> f) {
      if(value.isSome())
        return this;
      else {
        Maybe<B> m = p.test(a) ? maybe(f.call(a)) : Maybe.nothing();
        return new Cases<>(a, m);
      }
    }

    public B otherwise() {
      return value.toNull();
    }

    public B otherwise(F1<A,B> f) {
     return value.orSome(f.call(a)); 
    }

    public B otherwise(B b) {
     return value.orSome(b); 
    }

    public B otherwise(F0<B> f) {
     return value.orSome(f.call()); 
    }
  }

  public static <A> P1<A> eq(A a) {
    return v -> v == a;
  }
}
