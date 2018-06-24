package drjoliv.fjava.monoid;

/**
 * The monoid for Strings, the associative operation is string concatenation and the identity element is the empty String.
 * @author drjoliv@gmail.com
 */
public class StringMonoid implements Monoid<String> {

  private static final StringMonoid INSTANCE = new StringMonoid();

  private StringMonoid(){}

  /**
   * Returns a monoid under strings.
   * @return a monoid under strings.
   */
  public static Monoid<String> join() {
    return INSTANCE;
  }

  @Override
  public String mempty() {
    return "";
  }

  @Override
  public String mappend(String w1, String w2) {
    return w1 + w2;
  }
}
