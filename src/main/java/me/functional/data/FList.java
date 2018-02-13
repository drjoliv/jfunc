package me.functional.data;

import java.util.function.Function;
import java.util.function.Predicate;

import me.functional.hkt.Hkt;

public abstract class FList<A> implements Hkt<FList.μ,A> {

  public static class μ{}

  private FList(){}

  abstract FList<A> append(A a);

  abstract FList<A> append(FList<A> a);

  abstract FList<A> prepend(A a);

  abstract FList<A> prepend(FList<A> a);

  abstract <B> FList<B> map(Function<A,B> fn);

  abstract <B> FList<B> bind(Function<A,FList<B>> fn);

  abstract Integer size();

  abstract FList<A> tail();

  abstract FList<A> take(int num);

  private static class Cons<A> extends FList<A> {

    public final A datum;

    private volatile Cons<A> tail;

    private final Supplier<Cons<A>> tailSupplier;

    public Cons<A> tail() {
      if(isTailNull) {
        synchronized(this) {
          if(isTailNull){
              tail = tailSupplier.get();
          }
        }
        return tail;
      }
      else{
        return tail;
      }
      
    }

    private  Boolean isTailNull() {
      return tail == null
    }




  }

  private static class Nil {

    public final A datum;

    public final Maybe<Node<A>> tail;


  }

  public Hkt<FList.μ,A> widen() {
    return (Hkt<FList.μ,A>) this;
  }

  public static <A> FList<A> unit(A a) {
    throw new UnsupportedOperationException();
  }

  public static <A> FList<A> empty() {
    throw new UnsupportedOperationException();
  }

  public FList<A> filter(Predicate<A> predicate) {
    return bind(a -> {
      if(predicate.test(a)) {
        return FList.<A>empty();
      } else {
        return FList.unit(a);
      }
    });
  }

  @SafeVarargs
  public static <B> FList<B> of(B... elements) {
    throw new UnsupportedOperationException();
  }


}
