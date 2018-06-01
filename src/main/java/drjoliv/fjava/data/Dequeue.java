package drjoliv.fjava.data;

import static drjoliv.fjava.data.FList.*;

/**
 *
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public interface Dequeue<A> extends Iterable<A> {

  /**
   *
   *
   * @return
   */
  public boolean isEmpty();

  /**
   *
   *
   * @return
   */
  public int size();

  /**
   *
   *
   * @return
   */
  public Maybe<A> first();

  /**
   *
   *
   * @return
   */
    public Maybe<A> last();

  /**
   *
   *
   * @param i
   * @return
   */
  public FList<A> takeFront(int i);

  /**
   *
   *
   * @param i
   * @return
   */
  public FList<A> takeBack(int i);

  /**
   *
   *
   * @param a
   * @return
   */
  public Dequeue<A> pushBack(A a);

  /**
   *
   *
   * @param a
   * @return
   */
  public Dequeue<A> pushFront(A a);

  /**
   *
   *
   * @return
   */
  public Maybe<T2<A,Dequeue<A>>> popFront();

  /**
   *
   *
   * @return
   */
  public Maybe<T2<A,Dequeue<A>>> popBack();

  /**
   *
   *
   * @param dequeue
   * @return
   */
  public Dequeue<A> concat(Dequeue<A> dequeue);

  public static <A> Dequeue<A> empty() {
   return null; 
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
    return new DequeueImpl<A>(list);
  }
}
