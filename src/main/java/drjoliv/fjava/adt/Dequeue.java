package drjoliv.fjava.adt;

import java.util.Iterator;

import static drjoliv.fjava.adt.FList.*;

import drjoliv.fjava.hlist.T2;

import static drjoliv.fjava.adt.Maybe.*;
import static drjoliv.fjava.hlist.T2.*;

public class Dequeue<A> implements Iterable<A> {

  private final FList<A> front;
  private final FList<A> rear;

  Dequeue(FList<A> list) {
    T2<FList<A>,FList<A>> t = list.split();
    this.front = t._1();
    this.rear  = t._2().reverse();
  }

  Dequeue(FList<A> front, FList<A> rear) {
    this.front = front;
    this.rear  = rear;
  }

  public boolean isEmpty() {
    return front.isEmpty();
  }

  public int size() {
    return front.size() + rear.size();
  }

  public Maybe<A> first() {
    return front.safeHead();
  }

  public Maybe<A> last() {
    return rear.safeHead();
  }

  public FList<A> takeFront(int i) {
    return front.size() < i
      ? front.concat(rear.reverse()).take(i)
      : front.take(i);
  }

  public FList<A> takeBack(int i) {
    return rear.size() < i
      ? rear.concat(front.reverse()).take(i)
      : rear.take(i);
  }

  public Dequeue<A> pushBack(A a) {
    return new Dequeue<A>(front, rear.add(a));
  }

  public Dequeue<A> pushFront(A a) {
    return new Dequeue<A>(front.add(a), rear);
  }

  public Maybe<T2<A, Dequeue<A>>> popFront() {
    return isEmpty()
      ? nothing()
      : maybe(t2(front.head(),new Dequeue<>(front.tail(),rear)));
  }

  public Maybe<T2<A, Dequeue<A>>> popBack() {
    return isEmpty() 
      ? nothing()
      : maybe(t2(rear.head(), new Dequeue<>(front,rear.tail())));
  }

  static <A> Dequeue<A> empty() {
    return new Dequeue<>(FList.empty(), FList.empty());
  }

  @Override
  public Iterator<A> iterator() {
    return new Iterator<A>(){

    private FList<A> f = front;
    private FList<A> r = rear.reverse();

      @Override
      public boolean hasNext() {
        return (!f.isEmpty()) || (!r.isEmpty());
      }

      @Override
      public A next() {
        if(!f.isEmpty()) {
          A a = f.head();
          f = f.tail();
          return a;
        } else {
          A a = r.head();
          r = r.tail();
         return a;
        }
      }
    };
  }

  /**
   *
   *
   * @param args
   * @return
   */
  public static <A> Dequeue<A> dequeue(A... args) {
    return dequeue(flist(args));
  }

  public static <A> Dequeue<A> dequeue(FList<A> list) {
    return new Dequeue<A>(list);
  }

  public Dequeue<A> concat(Dequeue<A> dequeue) {
    FList<A> f = dequeue.takeFront(dequeue.size());
    Dequeue<A> self = this;
    for(A a : f) {
      self = self.pushBack(a);
    }
    return self;
  }

}
