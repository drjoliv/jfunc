package drjoliv.jfunc.data.deque;

import java.util.Iterator;

import drjoliv.jfunc.contorl.maybe.Maybe;
import drjoliv.jfunc.data.list.FList;
import drjoliv.jfunc.hlist.T2;

import static drjoliv.jfunc.contorl.maybe.Maybe.*;
import static drjoliv.jfunc.data.list.FList.*;
import static drjoliv.jfunc.hlist.T2.*;

public class Deque<A> implements Iterable<A> {

  private final FList<A> front;
  private final FList<A> rear;

  Deque(FList<A> list) {
    T2<FList<A>,FList<A>> t = list.split();
    this.front = t._1();
    this.rear  = t._2().reverse();
  }

  Deque(FList<A> front, FList<A> rear) {
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
      ? front.append(rear.reverse()).take(i)
      : front.take(i);
  }

  public FList<A> takeBack(int i) {
    return rear.size() < i
      ? rear.append(front.reverse()).take(i)
      : rear.take(i);
  }

  public Deque<A> pushBack(A a) {
    return front.isEmpty()
      ? new Deque<A>(front.cons(a), rear)
      : new Deque<A>(front, rear.cons(a));
  }

  public Deque<A> pushFront(A a) {
    return new Deque<A>(front.cons(a), rear);
  }

  public Maybe<T2<A, Deque<A>>> popFront() {
    return isEmpty()
      ? nothing()
      : maybe(t2(front.head(),new Deque<>(front.tail(),rear)));
  }

  public Maybe<T2<A, Deque<A>>> popBack() {
    return isEmpty() 
      ? nothing()
      : maybe(t2(rear.head(), new Deque<>(front,rear.tail())));
  }

  static <A> Deque<A> empty() {
    return new Deque<>(FList.empty(), FList.empty());
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
  public static <A> Deque<A> dequeue(A... args) {
    return dequeue(flist(args));
  }

  public static <A> Deque<A> dequeue(FList<A> list) {
    return new Deque<A>(list);
  }

  public Deque<A> concat(Deque<A> dequeue) {
    FList<A> f = dequeue.takeFront(dequeue.size());
    Deque<A> self = this;
    for(A a : f) {
      self = self.pushBack(a);
    }
    return self;
  }
}
