package drjoliv.jfunc.nums;

import static drjoliv.jfunc.data.list.FList.*;

import drjoliv.jfunc.data.list.FList;
import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.function.F2;

public class Integers {

  public static FList<Integer> naturals() {
    return start(1);
  }

  public static FList<Integer> start(Integer i) {
    return flist(i, () -> start(i + 1));
  }

  public static Integer multiply(Integer l, Integer l2) {
    return l * l2;
  }

  public static F2<Integer, Integer, Integer> multiply() {
    return Integers::multiply;
  }

  public static Integer product(FList<Integer> list) {
    return list.foldr(multiply(),1);
  }

  public static F1<Integer,Integer> increment(Integer i) {
    return ii -> ii + i;
  }


  public static F1<Integer,Integer> decrement(Integer i) {
    return ii -> ii - i;
  }


  public static F1<Integer,Integer> multiply(Integer i) {
    return ii -> ii * i;
  }

  public static FList<Integer> range(Integer from, Integer to) {
    if(from > to)
      return FList.empty();
    else
      return flist(from , () -> range(from + 1, to));
  }

  public static Integer add(Integer i1, Integer i2) {
    return i1 + i2;
  }

  public static FList<Integer> range(Integer from, Integer to, F1<Integer,Integer> fn ) {
    if(from == to)
      return flist(from);
    else if(from > to)
      return dec(from, to, fn);
    else
      return inc(from, to, fn);
  }

  private static FList<Integer> dec(Integer from, Integer to, F1<Integer,Integer> fn) {
    if(from < to)
      return FList.empty();
    else
      return flist(from, () -> dec(fn.call(from), to, fn));
  }

  private static FList<Integer> inc(Integer from, Integer to, F1<Integer,Integer> fn) {
    if(from > to)
      return FList.empty();
    else
      return flist(from, () -> inc(fn.call(from), to, fn));
  }
}
