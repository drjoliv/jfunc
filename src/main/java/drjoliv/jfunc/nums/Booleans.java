package drjoliv.jfunc.nums;

import drjoliv.jfunc.data.list.FList;
import drjoliv.jfunc.function.F2;

public class Booleans {
    public static boolean and(FList<Boolean> list) {
      F2<Boolean, Boolean, Boolean> fn = Boolean::logicalAnd;
      return list.foldr(fn,Boolean.TRUE).booleanValue();
    }

    public static boolean or(FList<Boolean> list) {
      F2<Boolean, Boolean, Boolean> fn = Boolean::logicalOr;
      return list.foldr(fn,Boolean.FALSE).booleanValue();
    }

}
