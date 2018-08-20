package drjoliv.jfunc.function;

public interface P1<A> {
  public boolean test(A a);

  public default P1<A> AND(boolean b) {
    return a -> test(a) && b;
  }

  public default P1<A> OR(boolean b) {
    return a -> test(a) || b;
  }

}
