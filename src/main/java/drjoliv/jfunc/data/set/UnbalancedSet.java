package drjoliv.jfunc.data.set;

import drjoliv.jfunc.eq.Ord;

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

  @Override
  public abstract UnbalancedSet<E> insert(E e);

  @Override
  public abstract UnbalancedSet<E> remove(E e);

  @Override
  public abstract boolean member(E e);

  @Override
  public abstract int size();

  @Override
  public abstract  boolean isEmpty();

}
