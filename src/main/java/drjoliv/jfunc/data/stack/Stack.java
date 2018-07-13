package drjoliv.jfunc.collection;

import static drjoliv.jfunc.collection.FList.flist;
import static drjoliv.jfunc.contorl.Maybe.maybe;
import static drjoliv.jfunc.hlist.T2.t2;

import java.util.Iterator;

import drjoliv.jfunc.contorl.Maybe;
import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.functor.Functor;
import drjoliv.jfunc.hkt.Hkt;
import drjoliv.jfunc.hlist.T2;

/**
 * Plain LIFO stack.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public class Stack<A> implements Iterable<A>, Hkt<Stack.μ,A> , Functor<Stack.μ,A> {


  /**
  * The witness type of {@code Stack}.
  */
  public static class μ {private μ(){}}

  private static Stack EMPTY_STACK = new Stack(0,FList.empty());

  private final int size;

  private final FList<A> list;

  public Stack(int size, FList<A> list) {
    this.size = size;
    this.list = list;
  }

  @Override
  public <B> Stack<B> map(F1<? super A, ? extends B> fn) {  
    return new Stack<>(size, list.map(fn));
  }

  @Override
  public Iterator<A> iterator() {
    return list.iterator();
  }

  /**
   * Returns the size of this stack.
   * @return the size
   */
  public int getSize() {
    return size;
  }

  /**
   * Returns a stack with the argument pushed onto it.
   * @param a an argument that will be pushed onto this stack.
   * @return a stack with the argument pushed onto it.
   */
  public Stack<A> push(A a) {
    return new Stack<>(size + 1, list.cons(a));
  }

  /**
   * Returns a maybe containing a tuple filled with the removed item and the newly created stack.
   * @return a maybe containing a tuple filled with the removed item and the newly created stack.
   */
  public Maybe<T2<A,Stack<A>>> pop() {
    return list.match(n -> Maybe.nothing()
        , c -> {
          T2<A,Stack<A>> t = t2(c.head(), new Stack<>(size - 1, c.tail()));
          return maybe(t);
        });
  }

  /**
   * Returns true if this stack is empty and false otherwise.
   * @return true if this stack is empty and false otherwise.
   */
  public boolean isEmpty() {
   return list.isEmpty();
  }

  /**
   * Returns an empty stack.
   * @return the empty stack.
   */
  public static <A> Stack<A> empty() {
    return EMPTY_STACK;
  }

  /**
   * Creates a stack from an array of items.
   * @param a an array of items used to create a new stack.
   * @return a new stack.
   */
  public static <A> Stack<A> stack(A... a) {
    return fromFList(flist(a));
  }

  /**
   * Creates a stack from a flist of items.
   * @param list a FList used to create a new stack.
   * @return a new stack.
   */
  public static <A> Stack<A> fromFList(FList<A> list) {
    return new Stack<>(list.size(), list);
  }
}
