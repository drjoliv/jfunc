package drjoliv.fjava.adt;

import static drjoliv.fjava.hlist.T2.*;

import java.util.Iterator;

import drjoliv.fjava.adt.Stack.μ;
import drjoliv.fjava.functions.F1;
import drjoliv.fjava.functor.Functor;
import drjoliv.fjava.hkt.Hkt;
import drjoliv.fjava.hkt.Witness;
import drjoliv.fjava.hlist.T2;
import static drjoliv.fjava.adt.FList.*;
import static drjoliv.fjava.adt.Maybe.*;

public class Stack<A> implements Iterable<A>, Hkt<Stack.μ,A> , Functor<Stack.μ,A>{

  public static class μ implements Witness{private μ(){}}

  private static Stack EMPTY_STACK = new Stack(0,FList.empty());

  private final int size;

  private final FList<A> list;

  public Stack(int size, FList<A> list) {
    this.size = size;
    this.list = list;
  }

  @Override
  public <B> Stack<B> map(F1<? super A, B> fn) {
    return new Stack<>(size, list.map(fn));
  }

  @Override
  public Iterator<A> iterator() {
    return list.iterator();
  }

  public Stack<A> push(A a) {
    return new Stack<>(size + 1, list.add(a));
  }

  public Maybe<T2<A,Stack<A>>> pop() {
    return list.match(n -> Maybe.nothing()
        , c -> {
          T2<A,Stack<A>> t = t2(c.head(), new Stack<>(size - 1, c.tail()));
          return maybe(t);
        });
  }

  public boolean isEmpty() {
   return list.isEmpty();
  }

  public static <A> Stack<A> empty() {
    return EMPTY_STACK;
  }

  public static <A> Stack<A> stack(A... a) {
    return fromFList(flist(a));
  }

  public static <A> Stack<A> fromFList(FList<A> list) {
    return new Stack<>(list.size(), list);
  }
}
