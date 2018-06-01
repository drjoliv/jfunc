package drjoliv.fjava.data;

/**
 *
 *
 * @author drjoliv@gmail.com
 */
public interface Dequeue<A> {

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

}
