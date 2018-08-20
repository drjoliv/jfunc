package drjoliv.jfunc.nums;

import static drjoliv.jfunc.contorl.tramp.Trampoline.*;
import static drjoliv.jfunc.data.list.FList.*;

import drjoliv.jfunc.contorl.tramp.Trampoline;
import drjoliv.jfunc.data.list.FList;
import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.function.F2;

public class Doubles {

  public static Double product(FList<Double> list) {
    return list.foldr(Doubles::multiply, 1.0);
  }

  public static Double multiply(Double d1, Double d2) {
    return d1 * d2;
  }

}
