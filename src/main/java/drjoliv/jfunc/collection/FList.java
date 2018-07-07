package drjoliv.jfunc.collection;

import static drjoliv.jfunc.contorl.Eval.later;
import static drjoliv.jfunc.contorl.Eval.now;
import static drjoliv.jfunc.contorl.Maybe.maybe;
import static drjoliv.jfunc.contorl.Trampoline.done;
import static drjoliv.jfunc.contorl.Trampoline.more;
import static drjoliv.jfunc.hlist.T2.t2;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import drjoliv.jfunc.applicative.Applicative;
import drjoliv.jfunc.applicative.ApplicativePure;
import drjoliv.jfunc.collection.FList.μ;
import drjoliv.jfunc.contorl.Case2;
import drjoliv.jfunc.contorl.Eval;
import drjoliv.jfunc.contorl.Maybe;
import drjoliv.jfunc.contorl.Trampoline;
import drjoliv.jfunc.function.F0;
import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.function.F2;
import drjoliv.jfunc.function.F3;
import drjoliv.jfunc.function.F4;
import drjoliv.jfunc.hkt.Hkt;
import drjoliv.jfunc.hlist.T2;
import drjoliv.jfunc.monad.Monad;
import drjoliv.jfunc.monad.MonadUnit;
import drjoliv.jfunc.monoid.Monoid;
import drjoliv.jfunc.traversable.Traversable;

/**
 * A stream of values.
 * @author Desonte 'drjoliv' Jolivet eamil:drjoliv@gmail.com
 */
public abstract class FList<A> implements Hkt<FList.μ,A>,
       Monad<FList.μ,A>, Case2<FList<A>,FList.Nil<A>,FList.Cons<A>>, Iterable<A>, Traversable<FList.μ,A> {

  public static <A> FList<A> replicate(int i, A a){
    if(i == 0)
      return FList.empty();
    else
      return flist(a, () -> replicate(i - 1, a));
  }

    @Override
    public <B> FList<B> apply(Applicative<μ, ? extends F1<? super A, ? extends B>> applicative) {
      FList<F1<? super A, ? extends B>> fx = (FList<F1<? super A, ? extends B>>)applicative;
      return asFList(Monad.For(fx
         , f -> this
         ,(f,a) -> unit(f.call(a))
        ));
    }

  @Override
  public <N, F, B> Applicative<N, FList<B>> traverse(F1<A, ? extends Applicative<N, B>> fn,
      ApplicativePure<N> pure) {
    F2<FList<B>, B, FList<B>> cons = (FList<B> list, B b) -> list.cons(b);
    final Applicative<N, FList<B>> empty = pure.pure(empty());
    final F2<Applicative<N, FList<B>>, Applicative<N, B>, Applicative<N, FList<B>>> go = (l, app) -> {
      return app.apply(l.map(cons));
    };
    return map(fn)
      .foldr(go,empty);
  }


  @Override
  public <N, B> Monad<N, FList<B>> mapM(F1<A, ? extends Monad<N, B>> fn,
      MonadUnit<N> ret) {
    return mapM_prime(map(fn),ret);
  }

  private static <N, B> Monad<N,FList<B>> mapM_prime(FList<Monad<N,B>> mb, MonadUnit<N> ret) {
    if(mb.isEmpty())
      return ret.unit(empty());
    else {
      Monad<N,B> h = mb.head();
      return Monad.For(
              h
            , a        -> mapM_prime(mb.tail(), ret)
            , (a, ax)  -> {
              FList<B> l = flist(a, () -> ax);
              return ret.unit(l);
            });
    }
  }

  @Override
  public <B, C> FList<C> For(F1<? super A, ? extends Monad<μ, B>> fn,
      F2<? super A, ? super B, ? extends Monad<μ, C>> fn2) {
    return asFList(Monad.super.For(fn, fn2));
  }

  @Override
  public <B, C, D> FList<D> For(F1<? super A, ? extends Monad<μ, B>> fn,
      F2<? super A, ? super B, ? extends Monad<μ, C>> fn2,
      F3<? super A, ? super B, ? super C, ? extends Monad<μ, D>> fn3) {
    return asFList(Monad.super.For(fn, fn2, fn3));
  }

  @Override
  public <B, C, D, E> FList<E> For(F1<? super A, ? extends Monad<μ, B>> fn,
      F2<? super A, ? super B, ? extends Monad<μ, C>> fn2,
      F3<? super A, ? super B, ? super C, ? extends Monad<μ, D>> fn3,
      F4<? super A, ? super B, ? super C, ? super D, ? extends Monad<μ, E>> fn4) {
    return asFList(Monad.super.For(fn, fn2, fn3, fn4));
  }

  /**
  * The witness type of FList.
  */
  public static class μ {private μ(){}}

    public static MonadUnit<FList.μ> unit = new MonadUnit<FList.μ>() {
      @Override
      public <A> Monad<μ, A> unit(A a) {
        return FList.single(a);
      }
    };

  @Override
  public abstract <B> FList<B> map(F1<? super A, ? extends B> fn);

  public abstract FList<FList<A>> suffixes();

  @Override
  public Iterator<A> iterator() {
    final FList<A> self = this;
    return new Iterator<A>() {

      private FList<A> list = self;

      @Override
      public boolean hasNext() {
        return !list.isEmpty();
      }

      @Override
      public A next() {
        A a = list.head();
        list = list.tail();
        return a;
      }
    };
  }

  public abstract FList<A> update(int i, A a);

  @Override
  public <B> FList<B> bind(F1<? super A, ? extends Monad<FList.μ, B>> fn) {
     if(isEmpty()) {
      return Nil.instance();
    } else {
      return FList.flatten(map(fn).map(FList::asFList));
    }
  }

  public <B> B foldr(F2<B,? super A,B> f2, B b) {
    return foldr_prime(f2, b, this).result();
  }

  private static <A,B> Trampoline<B> foldr_prime(F2<B,? super A,B> f2, B b, FList<A> list) {
    return list.isEmpty()
      ? done(b)
      : more(() -> foldr_prime(f2 , f2.call(b,list.head()), list.tail()));
  }

  public T2<FList<A>, FList<A>> split() {
    int i = size();
    return t2(take(i/2),drop(i/2));
  }

  public T2<FList<A>, FList<A>> splitAt(int i) {
    return t2(take(i),drop(i));
  }

  public final FList<A> reverse() {
    return FList.reverse(this);
  } 

  @Override
  public <B> FList<B> semi(Monad<μ, B> mb) {
    return bind(a -> mb);
  }

  @Override
  public ApplicativePure<FList.μ> pure() {
    return FList::single;
  }

  @Override
  public MonadUnit<FList.μ> yield() {
    return FList::single;
  }

  private FList(){}

  public final FList<FList<A>> window(int i) {
    return isEmpty()
      ? FList.empty()
      : flist(take(i), () -> take(i).window(i));
  }

  /**
   * Take elements from this flist unti the predicate becomes false.
   * @param predicate
   * @return elements from this flist unti the predicate becomes false.
   */
  public FList<A> takeWhile(Predicate<A> predicate) {
    Objects.requireNonNull(predicate);
    return FList.takeWhile(this,predicate);
  }

  /**
   * Appends a value to this FList.
   * @param a An element to append to this FLiist.
   * @return A new FList with the given value appended to it.
   */
  public FList<A> snoc(A a) {
    return append(flist(a));
  }

  /**
   * Appends the given FList to this FList.
   * @param a The FList that will be appended to this FList.
   * @return A new FList with the given FList appended to it.
   */
  public abstract FList<A> append(FList<A> a);

  /**
   * Creates a new FList in which the given element is the head of the new FList.
   * <pre>
   * {@code
   * FList<Integer> list = of(2,3);
   * print(list.addd(1));
   * //prints
   * // [1, 2, 3]
   * }
   * </pre>
   * @param a The element that will be prepend to the head of this list.
   * @return a new FList in which the given element is at the head.
   */
  public final FList<A> cons(A a) {
    return new Cons<>(now(a), now(this));
  }

  /**
   * Return a new FList with the given FList appended to it.
    * <pre>
   * {@code
   * FList<Integer> list = of(2,3);
   * FList<Integer> list2 = of(-1,0);
   * print(list.addd(list2));
   * //prints
   * // [-1, 0, 2, 3]
   * }
   * </pre>
   * @param a The FList that will be appended the end of this FList.
   * @return A new FList with the given FList appended to it.
   */
  public final FList<A> prepend(FList<A> a) {
    return FList.concat(a, () -> this);  
  }


  /**
   * Returns the element at the given index. This method is unsafe and will return null if
   * the given index is not within this FList.
   * <pre>
   * {@code
   * FList<String> list = of("apple","banna");
   * String a = list.unsafeGet(0);
   * System.out.println(a);
   * // prints
   * // "apple"
   * }
   * </pre>
  *
   * @param index The index of the element to retrieve.
   * @return The element at the given index.
   */
  public A unsafeGet(int index) {
    if(index < 0) throw new IllegalArgumentException("index can not be lower that zero");
    if(index == 0)
      return head();
    else if(isEmpty())
      return null;
    else {
      FList<A> list = this;
      for(int j = index; j > 0; j--)
        list = list.tail(); 
      return list.head();
    }
  }

  /**
   * Obtains the element of this FList at the given index.
   *
   * @param index The index of this FList to return.
   * @return The element
   */
  public final Maybe<A> get(int index) {
    if(index < 0) throw new IllegalArgumentException("index can not be lower that zero");
    if(index == 0)
      return safeHead();
    else if(isEmpty())
      return Maybe.nothing();
    else {
      FList<A> list = this;
      for(int j = index; j > 0; j--)
        list = list.tail(); 
      return list.safeHead();
    }
  }

  /**
   * Removes <code> i </code> elements from this, returning an FList with those
   * elements droped.
   *
   * @param i The number of elements to drop from this FList.
   * @return an FList where <code> i </code> elements have been droped.
   */
  public FList<A> drop(int i) {
    if(i < 0)
      return take(size() - Math.abs(i)); 
    else if(i == 0)
      return this;
    else if(isEmpty())
      return this;
    else {
      FList<A> list = this;
      for(int j = i; j > 0; j--)
        list = list.tail(); 
      return list;
    }
  }

  /**
   * Returns the last value within this FList. This is unsafe because it may
   * return null if this list is empty.
   *<pre>
   * {@code
   * Integer a = of(2,3).unsafeLast();
   * system.out.println(a);
   * //prints
   * // 3
   *
   * Integer b = Flist.empty().unsafeLast();
   * System.out.println(b);
   * //prints
   * // null
   * }
   * </pre>
   * @return The last value within this FList. 
   */
  public A unsafeLast() {
    if(isEmpty())
      return null;
    else {
      FList<A> list = this;
      FList<A> tail = list.tail();
      while(!tail.isEmpty()) {
        list = list.tail();
        tail = list.tail();
      }
      return list.head();
    }
  }

  /**
   * Returns the last value within this FList, the value returned
   * is wrapped within a maybe and if this FList is empty the maybe will be 
   * emtpy.
   * <pre>
   * {@code
   * Maybe<Integer> a = of(2,3).last();
   * system.out.println(a);
   * //Maybe<3>
   *
   * Maybe<Integer> b = Flist.empty().last();
   * System.out.println(b);
   * //prints
   * // Nothing
   * }
   * </pre>
   * @return The last value within this FList. 
   */

  public Maybe<A> last() {
    return match(
        nil -> Maybe.nothing()
      , cons -> {
        A ret = null;
        for(A a : cons)
          ret = a;
        return maybe(ret);
    });
  }

  @Override
  public final String toString() {
    return FList.toString(this);
  }

  /**
   * Removes all elements within the FList that evaluate to true when applied to the predicate.
   * <pre>
   * {@code
   *  FList<Integer> oneToTweenty = of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20);
   *  String s = oneToTweenty.filter(i -> i % 2 == 0)
   *    .toString(); 
   *
   *  print(s); // [1, 3, 5, 7, 9, 11, 13, 15, 17, 19 ]
   *
   * // Finding sum of all natural number below 1000 that are divisible by 3 or 5.
   *
   *  FList<Integer> list = range(1,999)
   *    .filter(i -> i % 3 == 0 || i % 5 == 0);
   *
   *  System.out.println(list.reduce(0,(i, i2) -> i + i2)); //
   * }
   * </pre>
   * @param p the predicated used to filter this flist.
   * @return a new FList in which elements have been filtered out.
   */
  public final FList<A> filter(F1<A,Boolean> p) {
     if(isEmpty())
      return Nil.instance();
    else if(p.call(head()))
      return flist(head(), () -> tail().filter(p));
    else
      return FList.concat(Nil.instance(), () -> tail().filter(p));
  }

  /**
   * Returns a new FList containing the elements of this FList up to element at the <code>i</code> index.
   *
   * @param i The index up to where the elements will be taken.
   * @return A new FList containing the elements of this FList up to element at the ith index.
   */
  public final FList<A> take(int i) {
    if(i < 0)
      return reverse().take(Math.abs(i));
    if(isEmpty() || i == 0) {
      return Nil.instance();
    } else {
      return flist(head(), () -> tail().take(i-1));
    }
  }

  /**
   * Returns a value represented the combined values of this list.
   *
   * @param a The initial vaule used when reducing this list, if this list is emtry <code> a </code> is returned.
   * @param reducer a combining function that combines all the elements within this FList in a single value.
   * @return A value 
   */
  public final A reduce(final A a, final F2<A,A,A> reducer) {
    A reducedValue = a;
    Iterator<A> it = iterator();
    while(it.hasNext()) {
      reducedValue = reducer.call(reducedValue,it.next());
    }
    return reducedValue;
  }

  public final A reduce(Monoid<A> monoid) {
    return reduce(monoid.mempty(), monoid::mappend);
  }

  public final Maybe<A> reduce(final F2<A,A,A> reducer) {
    if(size() < 2) {
      return Maybe.nothing();
    }
    else {
      Iterator<A> it = iterator();
      A reducedValue = it.next();
      while(it.hasNext()) {
        reducedValue = reducer.call(reducedValue,it.next());
      }
      return Maybe.maybe(reducedValue);
    }
  }

  /**
   * Returns the number of elements within this FList.
   *
   * @return The number of elements within this FList.
   */
    public int size() {
      FList<A> f = this;
      int i = 0;
      while(f.isEmpty() == false) {
        i++;
        f = f.tail();
      }
      return i;
    }

  /**
   * Returns a Flist with the head of this FList dropped.
   *
   * @return A FList with the head of this FList dropped.
   */
  public abstract FList<A> tail();

  /**
   * Returns the element at the head of this FList as a Maybe.
   * This function should be used instae of unsafeHead, because it ensures that
   * a null pointer exception is not possible.
   *
   * @return Returns the element at the head of this FList as a Maybe.
   */
  public abstract Maybe<A> safeHead();

  /**
   * Returns the element at the head of this FList. This function is not safe
   * because it will return null if this FList is empty.
   *
   * @return The element at the head of this FList.
   */
  public abstract A head();

  public abstract Eval<A> head$();

  /**
   * Returns true if this FList has zero elements, or false otherwise.
   *
   * @return True if this FList has zero elements, or false otherwise.
   */
  public abstract boolean isEmpty();

  public static class Cons<A> extends FList<A> {

    private final Eval<A> datum; //The data withing this FList


    //A supplier that return the next FList in this LinkedList like structure
    private volatile Eval<FList<A>> tail;

    private Cons(Eval<A> datum, Eval<FList<A>> tail) {
      this.datum = datum;
      this.tail = tail;
    }

  /**
   * Appends the given FList to this FList.
   *
   * @param a The FList that will be appended to this FList.
   * @return A new FList with the given FList appended to it.
   */
  @Override
  public FList<A> append(FList<A> a) {
    return new Cons<>(datum, tail.map(f -> f.append(a)));
  }

    @Override
    public FList<A> tail() {
      return tail.value();
    }

    @Override
    public Maybe<A> safeHead() {
      return Maybe.maybe$(datum);
    }


    @Override
    public boolean isEmpty() {
      return false;
    }

    @Override
    public A head() {
      return datum.value();
    }

   @Override
    public Eval<A> head$() {
      return datum;
    }

    @Override
    public <B> FList<B> map(F1<? super A, ? extends B> fn) {
      return new Cons<B>(datum.map(fn), tail.map(l -> l.map(fn)));
    }

    @Override
    public FList<A> update(int i, A a) {
      return i == 0
      ? tail().cons(a)
      : new Cons<>(datum, tail.map(t -> t.update(i - 1, a)));
    }

    @Override
    public FList<FList<A>> suffixes() {
      return new Cons<>(now(this), tail.map(t -> t.suffixes()));
    }

    @Override
    public <C> C match(F1<Nil<A>, C> f1, F1<Cons<A>, C> f2) {
      return f2.call(this);
    }

  }

  public static class Nil<A> extends FList<A> {

    private static Nil<?> instance = new Nil();

    private Nil(){}

    @SuppressWarnings("unchecked")
    private static <B> Nil<B> instance() {
      return (Nil<B>)instance;
    }

    @Override
    public FList<A> append(FList<A> a) {
      return a;
    }

    @Override
    public FList<A> tail() {
      return instance();
    }

    @Override
    public Maybe<A> safeHead() {
      return Maybe.nothing();
    }

    @Override
    public boolean isEmpty() {
      return true;
    }

   @Override
    public Eval<A> head$() {
      throw new RuntimeException("No element in an empty list");
    }

    @Override
    public A head() {
      throw new RuntimeException("No element in an empty list");
    }

    @Override
    public <B> FList<B> map(F1<? super A, ? extends B> fn) {
      return Nil.empty();
    }
  
    @Override
    public FList<A> update(int i, A a) {
      throw new IndexOutOfBoundsException();
    }

    @Override
    public FList<FList<A>> suffixes() {
      return flist(empty());
    }

    @Override
    public <C> C match(F1<Nil<A>, C> f1, F1<Cons<A>, C> f2) {
      return f1.call(this);
    }


  }

  /**
   * Creates an empty FList.
   * @return An empty FList.
   */
  public static <A> FList<A> empty() {
    return Nil.instance();
  }

  public static <A> FList<A> single(A a) {
    return flist(a);
  }



  /**
   * Creats a new FList from an array of elements.
   *
   * @param elements The elements that will be turned into a FList.
   * @return A new FList.
   */
  @SafeVarargs
  public static <B> FList<B> flist(B... elements) {
    Objects.requireNonNull(elements);
    FList<B> list = Nil.instance();
    for (int i = elements.length - 1; i >= 0; i--) {
      list = list.cons(elements[i]);
    }
    return list;
  }

  @SuppressWarnings("unchecked")
  public static <B> FList<B> fromCollection(Collection<B> collection) {
    Objects.requireNonNull(collection);
    return flist((B[]) collection.toArray());
  }

  /**
   * Creates a new FList.
   *
   * @param b The element to placed at the head of this FList.
   * @param supplier A supplier that is used to generated the tail of this FList.
   * @return A new FList.
   */
  public static <B> FList<B> flist(B b, F0<FList<B>> supplier) {
    return new Cons<B>(now(b), later(supplier));
  }

  public static <B> FList<B> flist$(Eval<B> b, F0<FList<B>> supplier) {
    return new Cons<B>(b, later(supplier));
  }


  /**
   * Creates a new FList.
   *
   * @param b The element placed at the head of the created FList.
   * @param b1 The second element placed within the created FList.
   * @param supplier A supplier that is used to generate the tail of the created FList.
   * @return A new FList.
   */
  public static <B> FList<B> flist(B b, B b1, F0<FList<B>> supplier) {
   return flist(b, () -> flist(b1, supplier));
  }

  /**
   * Creates a new FList.
   *
   * @param b The element placed at the head of this created FList.
   * @param b1 The second element placed within the created FList.
   * @param b2 The third element placed within the created FList.
   * @param supplier A supplier that is used to generate the tail of the created FList.
   * @return a new FList.
   */
  public static <B> FList<B> flist(B b, B b1, B b2, F0<FList<B>> supplier) {
   return flist(b, () -> flist(b1, b2, supplier));
  }

  /**
   * Creates a new FList.
   *
   * @param b The element placed at the head of this created FList.
   * @param b1 The second element placed within the created FList.
   * @param b2 The third element placed within the created FList.
   * @param b3 The fourth element placed within the crated FList.
   * @param supplier A supplier that is used to generate the tail of the created FList.
   * @return a new FList.
   */
  public static <B> FList<B> flist(B b, B b1, B b2, B b3, F0<FList<B>> supplier) {
   return flist(b, () -> flist(b1, b2, b3, supplier));
  }

  /**
   * Converts a FList to a String.
   *
   * @param flist An FList that will be converted to an String.
   * @return The string representaion of <code> flist </code>.
   */
  public static <A> String toString(FList<A> flist) {
    Objects.requireNonNull(flist);
    StringBuilder builder = new StringBuilder(); 
    builder.append("[ ");
    Consumer<A> stringfy = a -> builder.append(a.toString()).append(" ");
    Iterator<A> it = flist.iterator();
    while(it.hasNext())
      stringfy.accept(it.next());
    builder.append("]");
    return builder.toString();
  }

    /**
     * Creates a FList from an Iterator.
     *
     * @param it an iterator from which elements will be drawed from to create the FList.
     * @return a FList.
     */
    public static final <A> FList<A> fromIterable(final Iterator<A> it){
     if(it.hasNext())
       return flist(it.next(), () ->  fromIterable(it));
     return FList.empty();
   }

  /**
   * Flattens an FList.
   *
   * @param listOfList a FList containing an FList.
   * @return a flattened FList.
   */
  public static final <A> FList<A> flatten(FList<FList<A>> listOfList) {
    if(listOfList.isEmpty())
      return Nil.instance();
    else
      return concat(listOfList.head(), () -> flatten(listOfList.tail()));
  }

    @SafeVarargs
    public static final <A> FList<A> flatten(FList<A> ... listOfList) {
    FList<FList<A>> listOfListPrime = flist(listOfList);
    return flatten(listOfListPrime);
  }

    /**
     * Concats two of the given FLists together.
     *
     * @param l1 The FList that will be used as the beginning of the created FList.
     * @param l2 A supplier that generated the end of the created FList.
     * @return A new FList with l1 as the beginning of theo FList and l2 as the end of the FList.
     */
    public static final <A> FList<A> concat(final FList<A> l1, F0<FList<A>> l2) {
      if(l1.isEmpty())
        return l2.call();
      else {
        FList<A> tail = l1.tail();
        return new Cons<>(l1.head$(), later(() -> concat(tail,l2)));
      }
    }

    /**
     * Takes elements from <code> list </code> while the predicate <code> p </code> is true.
     *
     * @param list the FList elements will be taken from.
     * @param p A predcate that maps over elements of <code> list </code>.
     * @return an FList.
     */
      public static <A> FList<A> takeWhile(FList<A> list, Predicate<A> p) {
     if(list.isEmpty())
        return Nil.instance(); 
      else if (p.test(list.head()))
        return flist(list.head(), () -> takeWhile(list.tail(), p));
      else
        return Nil.instance();
    }

    /**
     * Returns true if <code> p </code> is true for all elements within <code> list </code>.
     *
     * @param list an FList whose elements will be tested with predicate <code> p </code>.
     * @param p a predicate that will test the elements of <code> list </code>.
     * @return True if <code> p </code> is true for all elements within <code> list </code> and falst otherwise.
     */
      public static <A> boolean allTrue(FList<A> list, Predicate<A> p) {
            Objects.requireNonNull(list);
        Objects.requireNonNull(p);
 if(list.isEmpty())
        return true;
      else if(p.test(list.head()))
        return allTrue(list.tail(), p);
      else
        return false;
    }

    public static boolean and(FList<Boolean> list) {
      F2<Boolean, Boolean, Boolean> fn = Boolean::logicalAnd;
      return list.foldr(fn,Boolean.TRUE).booleanValue();
    }

    public static boolean or(FList<Boolean> list) {
      F2<Boolean, Boolean, Boolean> fn = Boolean::logicalOr;
      return list.foldr(fn,Boolean.FALSE).booleanValue();
    }

    /**
     *
     *
     * @param list
     * @param w
     * @param p
     * @return
     */
      public static <A> boolean allTrueWhile(FList<A> list, Predicate<A> w, Predicate<A> p) {
     if(list.isEmpty())
        return true;
      while(!list.isEmpty() && w.test(list.head())) {
        if(!p.test(list.head()))
          return false;
        list = list.tail();
      }
         return true;
    }

    public static <A> FList<A> repeat(A a) {
      return flist(a, () -> repeat(a));
    }

    public static <A> F2<FList<A>,FList<A>,FList<A>> concat() {
      return (a,b) -> a.append(b);
    }

    /**
     * Returns a new Flist generated by applying the binary function to pairs of elements from the two
     * given FLists.
     *
     * @param fn A binary function that will whose operand types match the given two list.
     * @param l1 The FList used in the first operand postion within the binary function.
     * @param l2 The FList used in the second operand position within the binary function.
     * @return A new Flist generated by applying the binary function to pairs of elements from the two
     * given FLists.
     */
      public static <A,B,C> FList<C> zipWith(F2<A,B,C> fn, FList<A> l1, FList<B> l2) {
     if(l1.isEmpty() ||  l2.isEmpty())
        return Nil.instance();
      return flist(fn.call(l1.head(), l2.head()), () -> zipWith(fn, l1.tail(), l2.tail()));
    }

    //public static <A> F2<A,FList<A>,FList<A>> cons() {
    //  return  (a,as) -> as.add(a);
    //}

    public static <A,B,C> FList<T2<A,B>> zip(FList<A> l1, FList<B> l2) {
     if(l1.isEmpty() ||  l2.isEmpty())
        return Nil.instance();
      return flist(t2(l1.head(), l2.head()), () -> zip(l1.tail(), l2.tail()));
    }

    /**
    * Tests if two FLists are equal.
    *
    * @param l1 an FList.
    * @param l2 an FList.
    * @return True if <code> l1 </code> and <code> l2 </code> or equal and false otherwise.
    */
    public static <A> boolean equals(FList<A> l1, FList<A> l2) {
     if(l1.isEmpty() != l2.isEmpty()) {
        return false;
      } else if(l1.isEmpty()) {
        return true;
      } else if(l1.head() == l2.head()){
        return equals(l1.tail(), l2.tail());
      } else {
        return false;
      }
    }

    /**
      * Creates a sequence from one seed element and a Function, <code>sequence</code> is useful when the previous elements in a
     * sequence dicate later elements within the sequence.
     *
     * @param seed The first value to be created within the generated sequence.
     * @param generator A function that use the previous value within the FList to create the next value.
     * @return A infinite FList.
     */
    public static <A> FList<A> sequence(A seed, F1<A,A> generator) {
     return flist(seed , () -> sequence(generator.call(seed), generator));
    }

    /**
     * Creates a sequence from two seed elements and a BiFunction, <code>sequence</code> is useful when the previous elements in a
     * sequence dicate later elements within the sequence.
     *
     * @param seed First element within a sequence.
     * @param seed1 Second element within a sequence.
     * @param generator A BiFunction that generates elements within the sequence using the previous two elements within the sequence.
     * @return a infinte FList.
     */
      public static <A> FList<A> sequence(A seed, A seed1, F2<A,A,A> generator) {
      Objects.requireNonNull(seed);
        Objects.requireNonNull(seed1);
        Objects.requireNonNull(generator);
        A seed2 = generator.call(seed,seed1);
        A seed3 = generator.call(seed1,seed2);
        return flist(seed , seed1, () -> sequence(seed2, seed3, generator));
    }

    /**
    *
    * Creates a sequence from three seed elements and a TriFuction, <code>sequence</code> is useful when the previous elements in a
    * sequence dictate later elements within the sequence.
    *
    * @param seed First element within a sequence.
    * @param seed1 Second element within a sequence.
    * @param seed2 Third element within a sequence.
    * @param generator A TriFunction that generates the next element within the sequnce using the previous three elements within the sequence.
    * @return an infinte FList.
    */
    public static <A> FList<A> sequence(A seed, A seed1, A seed2, F3<A,A,A,A> generator) {
      A seed3 = generator.call(seed,seed1,seed2);
      A seed4 = generator.call(seed1,seed2,seed3);
      A seed5 = generator.call(seed2,seed3,seed4);
      return flist(seed, seed1, seed2, () -> sequence(seed3, seed4, seed5, generator));
    }

    public static <B> FList<B> asFList(Monad<FList.μ,B> wider) {
      return (FList<B>) wider;
    }

    private static <A> FList<A> reverse(FList<A> l) {
      return reverse_prime(l, FList.empty()).result();
    }

    private static <A> Trampoline<FList<A>> reverse_prime(FList<A> l, FList<A> acc) {
      if(l.isEmpty())
        return done(acc);
      else {
        return more(() -> reverse_prime(l.tail(), acc.cons(l.head())));
      }
    }
}
