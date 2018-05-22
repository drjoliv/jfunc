package drjoliv.fjava.data;

public interface Monoid<W> {
  W mempty();
  W mappend(W w1,W w2);
}
