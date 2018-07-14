package drjoliv.jfunc.show;

import static drjoliv.jfunc.data.list.FList.*;

import drjoliv.jfunc.data.list.FList;
import drjoliv.jfunc.function.F1;

public abstract class Show<A> implements F1<A,FList<Character>> {
  public abstract FList<Character> show(A a);

  @Override
  public FList<Character> call(A a) {
    return show(a);
  }

  final static Show DEFAULT = new Show() {
    @Override
    public FList show(Object a) {
      return array(a.toString().toCharArray());
    }
  };

  public static <A>  Show<A> defaultShow() {
    return DEFAULT;
  }
   

  public final void println(A a) {
    show(a).forEach(c -> System.out.println("this"));
    System.out.println();
  }

  public static Show<Double> showDbl() {
    return DEFAULT;
  }

  public static Show<Byte> showByte() {
    return DEFAULT;
  }

  public static Show<Short> showShort() {
    return DEFAULT;
  }

  public static Show<Float> showFlt() {
    return DEFAULT;
  }

  public static Show<Long> showLng() {
    return DEFAULT;
  }

  public static Show<Integer> showInt() {
    return DEFAULT;
  }

  public static Show<String> showStr() {
    return DEFAULT;
  }

}
