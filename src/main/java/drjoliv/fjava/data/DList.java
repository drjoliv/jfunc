package drjoliv.fjava.data;

import drjoliv.fjava.control.Bind;
import drjoliv.fjava.control.BindUnit;
import drjoliv.fjava.control.Functor;
import drjoliv.fjava.data.DList.μ;
import drjoliv.fjava.functions.F1;
import drjoliv.fjava.functions.F2;
import drjoliv.fjava.hkt.Witness;
import drjoliv.fjava.data.FList;
import static drjoliv.fjava.data.FList.*;

import java.util.ArrayList;
import java.util.function.Function;

import drjoliv.fjava.Numbers;

/**
 *
 *
 * @author drjoliv@gmail.com
 */
public class DList<A> implements Functor<DList.μ,A> {

  public static interface μ extends Witness{}

  private final F1<FList<A>,FList<A>> list;

  private DList(F1<FList<A>,FList<A>> list) {
    this.list = list;
  }

  public static <A> DList<A> fromList(FList<A> flist) {
    return new DList<A>(DList.<A>concat().call(flist));
  }

  public static <A> DList<A> empty() {
    return new DList<>(F1.identity());
  }

  public static <A> DList<A> dlist(A... as) {
    return fromList(flist(as));
  }

  public DList<A> concat(DList<A> a) {
    return new DList<A>(list.before(a.list));
  }

  public DList<A> concat(A a) {
    return concat(dlist(a));
  }

  public FList<A> toList() {
    return list.call(FList.empty());
  }

  public DList<A> add(A a) {
    return new DList<>( DList.<A>cons()
        .call(a)
        .before(list)
    );
  }

  private static <A> F2<A,FList<A>,FList<A>> cons() {
    return (a,ax) -> ax.add(a);
  }

  private static <A> F2<FList<A>,FList<A>,FList<A>> concat() {
    return (l1,l2) -> l1.concat(l2);
  }

  public A reduce(final A a, final F2<A,A,A> fn) {
    return toList().reduce(a,fn);
  }

  public Maybe<A> reduce(final F2<A,A,A> fn) {
    return toList().reduce(fn);
  }

  @Override
  public <B> DList<B> map(F1<? super A, B> fn) {
    return new DList<>(list -> toList().map(fn));
  }
}
