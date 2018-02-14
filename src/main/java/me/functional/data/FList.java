package me.functional.data;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.Predicate;

import me.functional.hkt.Hkt;

public abstract class FList<A> implements Hkt<FList.μ,A> {

  public static class μ{}

  private FList(){}

  public final FList<A> append(A a) {
    return FList.append(this,a);
  }

  public final FList<A> append(FList<A> a) {
    return FList.append(this,a);  
  }

  public final FList<A> prepend(A a) {
    return FList.prepend(this,a);
  }

  public final FList<A> prepend(FList<A> a) {
    return FList.prepend(this, a);  
  }

  public final <B> FList<B> map(Function<A,B> fn) {
    return FList.map(this, fn);
  }

  public final Hkt<FList.μ, A> widen() {
    return (Hkt<FList.μ, A>) this;
  }

  public final <B> FList<B> bind(Function<A,FList<B>> fn) {
    return FList.bind(this, fn); 
  }

  public final void forEach(Consumer<A> consumer) {
    for(A a : iterable(this))
      consumer.accept(a); 
  }

  public final Boolean isEmpty(){
    return FList.empty(this);
  }

  public final FList<A> filter(Predicate<A> predicate) {
    return FList.filter(this,predicate);
  }

  public final FList<A> take(int i) {
    return FList.take(this, i);  
  }

  abstract Integer size();

  abstract FList<A> tail();

  abstract A head();

  public static class Cons<A> extends FList<A> {

    private final A datum;

    private volatile FList<A> tail;

    private final Supplier<FList<A>> tailSupplier;

    private Cons(A datum, Supplier<FList<A>> tailSupplier) {
      Objects.requireNonNull(datum);
      Objects.requireNonNull(tailSupplier);
      this.datum = datum;
      this.tailSupplier = tailSupplier;
    }

    private Cons(A datum, FList<A> tail, Supplier<FList<A>> tailSupplier) {
     Objects.requireNonNull(datum);
     Objects.requireNonNull(tail);
     Objects.requireNonNull(tailSupplier);
     this.datum = datum;
      this.tail = tail;
      this.tailSupplier = tailSupplier;
    }

    public FList<A> tail() {
      if(isTailNull()) {
        synchronized(this) {
          if(isTailNull()){
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
      return tail == null;
    }

    public A head() {
      return datum;
    }

    @Override
    public Integer size() {
      return 1 + tail.size();
    }

  }

  private static final <A> FList<A> take(final FList<A> l1, final int i) {
    if(l1.isEmpty() || i == 0) {
      return Nil.instance();
    } else if(l1 instanceof Cons<?>) {
      return new Cons<A>(l1.head(), () -> {
        return take(l1.tail(), i - 1);
      });
    } else {
      throw new RuntimeException();
    }
  }

  private static final <A> FList<A> append(final FList<A> l1, final FList<A> l2) {
    if(l1 instanceof Nil<?>) {
      return l2;
    } else if(l1 instanceof Cons<?>) {
      return new Cons<A>(l1.head(), () -> {
       return append(l1.tail(), l2); 
      });
    } else {
      throw new RuntimeException();
    }
  }
  
  private static final <A> FList<A> append(final FList<A> l1, final A a) {
    return append(l1, of(a));
  }

  private static final <A> FList<A> prepend(final FList<A> l1, final A a) {
      return new Cons<A>(a, l1, () -> l1);
  }

  private static final <A> FList<A> prepend(final FList<A> l1, final FList<A> l2) {
      return append(l2, l1);
  }

  public static final <A> Iterable<A> iterable(final FList<A> flist){

    return new Iterable<A>() {
      @Override
      public Iterator<A> iterator() {
        return new Iterator<A>() {

          private FList<A> list;
          {
            this.list = flist;
          }

          @Override
          public boolean hasNext() {
            return !list.isEmpty();
          }

          @Override
          public A next() {
            final A a = list.head();
            list = list.tail();
            return a;
          }
        };
      }

      };
  }

  private static final Boolean empty(FList<?> list) {
     if(list instanceof Nil<?>) {
      return true;
    } else if(list instanceof Cons<?>) {
      return false;
    } else {
      throw new RuntimeException();
    }
  }

  private static final <A,B> FList<B> map(final FList<A> l1, final Function<A,B> fn) {
    if(l1 instanceof Nil<?>) {
      return Nil.instance();
    } else if(l1 instanceof Cons<?>) {
      return new Cons<B>(fn.apply(l1.head()), () -> {
        return map(l1.tail(), fn);
      });
    } else {
      throw new RuntimeException();
    }
  }

  private static final <A> FList<A> filter(FList<A> l1, Predicate<A> predicate) {
    return l1.bind(a -> {
      if (predicate.test(a)) {
        return FList.<A>empty();
      } else {
        return FList.unit(a);
      }
    });
  }

  private static final <A,B> FList<B> bind(FList<A> l1, Function<A,FList<B>> fn) {
    if(l1 instanceof Nil<?>) {
      return Nil.instance();
    } else if(l1 instanceof Cons<?>) {
      final FList<B> l2 = fn.apply(l1.head());
      if(l2.isEmpty())
        return append(l2, bind(l1.tail(), fn));
      else
        return new Cons<B>(l2.head(), () -> append(l2.tail(), bind(l1.tail(), fn)));
    } else {
      throw new RuntimeException();
    }
  }

  private static class Nil<A> extends FList<A> {

    private static Nil<?> instance = new Nil();

    private Nil(){}

    @SuppressWarnings("unchecked")
    public static <B> Nil<B> instance() {
      return (Nil<B>)instance;
    }

    @Override
    Integer size() {
      return 0;
    }

    @Override
    FList<A> tail() {
      return instance();
    }

    @Override
    A head() {
      return null;
    }
  }


  public static <A> FList<A> unit(A a) {
    return of(a);
  }

  public static <A> FList<A> empty() {
    return Nil.instance();
  }

  public static <A> String toString(FList<A> flist) {
    StringBuilder builder = new StringBuilder(); 
    builder.append("[ ");
    Consumer<A> stringfy = a -> {
      builder.append(a.toString()).append(" ");
    };
    flist.forEach(stringfy);
    builder.append("]");
    return builder.toString();
  }

  public static FList<Integer> start(Integer i) {
    return new Cons<Integer>(i, () -> start(i + 1));
  }


  public static FList<Integer> range(int from, int to) {
    if(from > to)
      return Nil.instance();
    return new Cons<Integer>(from, () -> range(from + 1, to));
  }

  public static <A> FList<A> sequence(A seed, Function<A,A> generator) {
    return new Cons<A>(seed , () -> sequence(generator.apply(seed), generator));
  }

  @SafeVarargs
  public static <B> FList<B> of(B... elements) {
    FList<B> list = Nil.instance();
    for(int i = elements.length - 1; i >= 0; i--) {
      list = prepend(list,elements[i]);
    }
    return list;
  }
}
