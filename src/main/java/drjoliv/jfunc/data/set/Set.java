package drjoliv.jfunc.data.set;

public interface Set<E> {
  public Set<E> insert(E e);
  public Set<E> remove(E e);
  public boolean member(E e);
  public int size();
}
