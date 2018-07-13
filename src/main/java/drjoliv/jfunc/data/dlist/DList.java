package drjoliv.jfunc.collection;

import static drjoliv.jfunc.collection.FList.flist;
import static drjoliv.jfunc.function.F1.compose;

import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.functor.Functor;

/**
 * A differnece list allows for quicker concatenation of elements.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public class DList<A> implements Functor<DList.μ,A> {

  /**
  * The witness type of {@code DList}.
  */
  public static class μ {private μ(){}}

  private final F1<FList<A>,FList<A>> list;

  @Override
  public <B> DList<B> map(F1<? super A, ? extends B> fn) {
    return new DList<>(list -> toList().map(fn));
  }

  private DList(F1<FList<A>, FList<A>> list) {
    this.list = list;
  }

  /**
   * Adds an elment to the head of this list, returning a new one.
   * @param a an element to prepend to his dlist.
   * @return a dlist.
   */
  public DList<A> cons(A a) {
    return new DList<>(list.then(ls -> ls.cons(a)));
  }

  /**
   * Prepends the given dlist to this dlist.
   * @param dl a dlist that will be prepended to this list.
   * @return a list with the argument prepended.
   */
  public DList<A> prepend(DList<A> dl) {
    return new DList<>(compose(list, dl.list));
  }

  /**
   * Appends the given element to the end of this list.
   * @param a the element to append to this list.
   * @return a list with the argument appended.
   */
  public DList<A> snoc(A a) {
    return new DList<>(list.before(ls -> ls.cons(a)));
  }

  /**
   * Appends a dlist to this dlist.
   * @param dl a dlist to to append.
   * @return a dlist with the argument appended.
   */
  public DList<A> append(DList<A> dl) {
    return new DList<>(compose(dl.list, list));
  }

  /**
   * Returns an empty dlist.
   * @return an empty dlist.
   */
  public static <A> DList<A> empty() {
    return new DList<>(F1.identity());
  }

  /**
   * Returns a dlist with a single element.
   * @param a the element to insert into an empty dlist.
   * @return a dlist containing the sinle argument.
   */
  public static <A> DList<A> singleton(A a) {
    return new DList<>(ls -> ls.cons(a));
  }

  /**
   * Converts a flist to a dlist.
   * @param flist the flist to be converted to a dlist.
   * @return a dlist.
   */
  public static <A> DList<A> fromList(FList<A> flist) {
    return new DList<>(ls -> ls.append(flist));
  }


  /**
   * Converst an array of elements to a dlist.
   * @param as an arry of elements.
   * @return a dlist.
   */
  public static <A> DList<A> dlist(A... as) {
    return fromList(flist(as));
  }

  /**
   * Transforms this dlist into a flist.
   * @return a flist.
   */
  public FList<A> toList() {
    return list.call(FList.empty());
  }
}
