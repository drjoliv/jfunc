package drjoliv.jfunc.eq;

public class Ordering {

  public static final Ordering LT = new Lt();

  public static final Ordering GT = new Gt();

  public static final Ordering EQ = new Eq();

  public static final class Lt extends Ordering {
    private Lt(){}
  }

  public static final class Gt extends Ordering {
    private Gt(){}
  }

  public static final class Eq extends Ordering {
    private Eq(){}
  }
}
