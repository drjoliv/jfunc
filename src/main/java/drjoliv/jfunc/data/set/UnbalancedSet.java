package drjoliv.jfunc.data.set;

import java.util.Iterator;

import drjoliv.jfunc.contorl.CaseOf;
import drjoliv.jfunc.contorl.eval.Eval;
import drjoliv.jfunc.contorl.tramp.Trampoline;
import static drjoliv.jfunc.contorl.tramp.Trampoline.*;

import static drjoliv.jfunc.contorl.eval.Eval.*;
import drjoliv.jfunc.data.Unit;
import drjoliv.jfunc.data.list.FList;
import drjoliv.jfunc.eq.Ord;
import drjoliv.jfunc.eq.Ordering;
import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.function.F2;
import drjoliv.jfunc.function.F3;

public abstract class UnbalancedSet<E> implements Set<E> {

  private final Ord<E> order;

  private UnbalancedSet(Ord<E> order) {
    this.order = order;
  }

  final boolean lt(E e1, E e2) {
    return order.lt(e1, e2);
  }

  final boolean gt(E e1, E e2) {
    return order.gt(e1, e2);
  }

  final boolean eq(E e1, E e2) {
    return order.eq(e1, e2);
  }

  final Ord<E> getOrder() {
    return order;
  }

  public static <E> UnbalancedSet<E> empty(Ord<E> order) {
    return new Nil<E>(order);
  }

  public final FList<E> toList() {

    final F2< UnbalancedSet<E>
            , UnbalancedSet<E>
            , FList<E> > go = (left, right) -> left.toList().append(right.toList());

    return visit$(nil -> FList.empty()
        , (data, left ,right) -> FList.flist$( data, Eval.liftM2(left, right, go) ) );
  }

  final <B> B visit (F1<Unit, B> nil, F3<E, UnbalancedSet<E>, UnbalancedSet<E>, B> cons) {
    return match(empty -> nil.call(Unit.unit), con -> cons.call(con.data.value(), con.left.value(), con.right.value()));
  }

  final <B> B visit$ (F1<Unit, B> nil, F3<Eval<E>, Eval<UnbalancedSet<E>>, Eval<UnbalancedSet<E>>, B> cons) {
    return match(emp -> nil.call(Unit.unit), con -> cons.call(con.data, con.left, con.right));
  }

  abstract <B> B match (F1<UnbalancedSet.Nil<E>, B> nil, F1<UnbalancedSet.Cons<E>, B> cons);

  @Override
  public abstract UnbalancedSet<E> insert(E e);

  @Override
  public abstract UnbalancedSet<E> remove(E e);

  public abstract UnbalancedSet<E> insert(Eval<E> e);

  public abstract UnbalancedSet<E> remove(Eval<E> e);

  @Override
  public final boolean member(E e) {
    return member(e, this).result();
  }

  private static final <E> Trampoline<Boolean> member(E e, UnbalancedSet<E> set) {
    return set.visit( unit               -> Trampoline.done(Boolean.FALSE)
                    ,(data, left, right) -> {
                      Ordering order = set.getOrder().compare(data, e);
                      return CaseOf.caseOf(order)
                        .of(Ordering.LT, () -> more(() -> member(e, left)))
                        .of(Ordering.GT, () -> more(() -> member(e, right)))
                        .otherwise(Trampoline.done(Boolean.TRUE));
                    });
  }

  @Override
  public final int size() {
   return size(this, ZERO).result(); 
  }

  private static final Integer ZERO = Integer.valueOf(0);
  private static final Integer ONE  = Integer.valueOf(1);

  private static <E> Trampoline<Integer> size(UnbalancedSet<E> set, Integer i) {
    return set.visit( unit            -> Trampoline.done(i)
                    ,(e, left, right) -> Trampoline.more(() -> {
                  F2<Integer,Integer,Integer> go = (l, r) -> l + r + ONE;
                  Trampoline<Integer> tl = size(left, ZERO);
                  Trampoline<Integer> tr = size(right, ZERO);
                  Trampoline<Integer> retVal = Trampoline.liftM2(tl, tr, go);
                  return retVal;
                }));
  }

  @Override
  public final boolean isEmpty() {
    return match(n -> true, c -> false);
  }

  @Override
  public final Iterator<E> iterator() {
    return toList().iterator();
  }

  private static final class Nil<E> extends UnbalancedSet<E> {

    private Nil(Ord<E> order) {
      super(order);
    }

    @Override
    <B> B match(F1<Nil<E>, B> nil, F1<Cons<E>, B> cons) {
      return nil.call(this);
    }

    @Override
    public UnbalancedSet<E> insert(Eval<E> e) {
      return new Cons<>(e, now(this), now(this), getOrder());
    }

    @Override
    public UnbalancedSet<E> remove(Eval<E> e) {
      return this;
    }

    @Override
    public UnbalancedSet<E> insert(E e) {
      return insert(now(e));
    }

    @Override
    public UnbalancedSet<E> remove(E e) {
      return this;
    }

  }

  private static final class Cons<E> extends UnbalancedSet<E> {

    private final Eval<E> data;
    private final Eval<UnbalancedSet<E>> left;
    private final Eval<UnbalancedSet<E>> right;

    private Cons(Eval<E> data, Eval<UnbalancedSet<E>> left, Eval<UnbalancedSet<E>> right, Ord<E> order) {
      super(order);
      this.data = data;
      this.left = left;
      this.right = right;
    }

    @Override
    <B> B match(F1<Nil<E>, B> nil, F1<Cons<E>, B> cons) {
      return cons.call(this);
    }

    @Override
    public UnbalancedSet<E> insert(E e) {
      if(lt(e, data.value()))
        return new Cons<>(data, left.map(l -> l.insert(e)), right, getOrder());
      else if(gt(e, data.value()))
        return new Cons<>(data, left, right.map(r -> r.insert(e)), getOrder());
      else
        return this;
    }

    @Override
    public UnbalancedSet<E> remove(E e) {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public UnbalancedSet<E> insert(Eval<E> e) {
      if(Eval.liftM2(e,data, this::lt).value())
        return new Cons<>(data, left.map(l -> l.insert(e)), right, getOrder());
      else if(Eval.liftM2(e,data, this::gt).value())
        return new Cons<>(data, left, right.map(r -> r.insert(e)), getOrder());
      else
        return this;
    }

    @Override
    public UnbalancedSet<E> remove(Eval<E> e) {
      // TODO Auto-generated method stub
      return null;
    }
  }
}
