package drjoliv.jfunc.data.list;

import drjoliv.jfunc.monoid.Monoid;

/**
 * FList monoid instance.
 * @author Desotne 'drjoliv' Joliver : drjoliv@gmail.com
 */
public class FListMonoid<A> implements Monoid<FList<A>>{

  private static final FListMonoid INSTANCE = new FListMonoid();

  public static <A> FListMonoid<A> flistMonoid() {
    return INSTANCE;
  }

  @Override
  public FList<A> mempty() {
    return FList.empty();
  }

  @Override
  public FList<A> mappend(FList<A> w1, FList<A> w2) {
    return w1.append(w2);
  }

}
