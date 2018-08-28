package drjoliv.jfunc.data.set;

import java.util.Iterator;

import drjoliv.jfunc.contorl.CaseOf;
import drjoliv.jfunc.contorl.eval.Eval;
import drjoliv.jfunc.contorl.tramp.Trampoline;
import static drjoliv.jfunc.contorl.tramp.Trampoline.*;

import static drjoliv.jfunc.contorl.eval.Eval.*;
import drjoliv.jfunc.data.Unit;
import drjoliv.jfunc.data.list.FList;
import drjoliv.jfunc.data.list.Functions;
import drjoliv.jfunc.eq.Ord;
import drjoliv.jfunc.eq.Ordering;
import drjoliv.jfunc.foldable.Foldable;
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

  public final FList<E> ascendingList() {
    return visit(nil -> FList.<E>empty()
        , (data, left ,right) ->
        Functions.flatten( left.ascendingList() , FList.flist(data), right.ascendingList()  ) );
  }

  public final FList<E> descendingList() {
    return visit(nil -> FList.<E>empty()
        , (data, left ,right) ->
        Functions.flatten( right.descendingList() , FList.flist(data), left.descendingList()  ) );
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
      // TODO implement remove
      throw new UnsupportedOperationException();
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
      // TODO implement remove
      throw new UnsupportedOperationException();
    }
  }

  public static <M,E> UnbalancedSet<E> fromFoldable(Foldable<M,E> foldable, Ord<E> order) {
    return foldable.foldr((set, e) -> set.insert(e) , empty(order));
  }

  //private static void shuffleArray(int[] array, java.util.Random ran) {
  //        for (int i = array.length - 1; i > 0; i--) {
  //          int index = ran.nextInt(i + 1);
  //            // Simple swap
  //           int a = array[index];
  //           array[index] = array[i];
  //           array[i] = a;
  //        }
  //}

  //public static void main(String[] args) {
  //  int number_of_ints = 1_000_000;
  //  long start;
  //  long time;

  //  int[] nums = new int[number_of_ints];
  //  java.util.ArrayList<Integer> testNums = new java.util.ArrayList<>();

  //  java.util.Random ran = new java.util.Random();

  //  java.util.HashSet<Integer> hashSet = new java.util.HashSet<>();
  //  UnbalancedSet<Integer> set = UnbalancedSet.empty(Ord.<Integer>orderable());

  //  UnbalancedSet<Integer> warm = UnbalancedSet.empty(Ord.<Integer>orderable());

  //  for(int i = 0; i < number_of_ints; i++) {
  //    warm = warm.insert(ran.nextInt()); 
  //    nums[i] = ran.nextInt();
  //  }


  //  for(int i : nums) {
  //    hashSet.add(i);
  //  }

  //  start = System.currentTimeMillis();
  //  for(int i : nums) {
  //    set = set.insert(i);
  //  }
  //  System.out.println(System.currentTimeMillis() - start);

  //  shuffleArray(nums, ran);
  //  for(int i = 0; i < 50_000; i++)
  //    testNums.add(nums[i]);

  //  start = System.currentTimeMillis();
  //  for(int i : nums)
  //    assert hashSet.contains(i);
  //  System.out.println(System.currentTimeMillis() - start);

  //  start = System.currentTimeMillis();
  //  for(int i : nums)
  //    assert set.member(i);
  //  System.out.println(System.currentTimeMillis() - start);
  //}
}
