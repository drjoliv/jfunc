package drjoliv.jfunc.nums;

import static drjoliv.jfunc.data.list.FList.*;

import drjoliv.jfunc.contorl.Trampoline;
import static drjoliv.jfunc.contorl.Trampoline.*;
import drjoliv.jfunc.data.list.FList;
import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.function.F2;

public class Longs {

  public static FList<Long> start(Long i) {
    return flist(i, () -> start(i + 1L));
  }

  public static Long add(Long l1, Long l2) {
    return l1 + l2;
  }

  public static F2<Long, Long, Long> add() {
    return Longs::add;
  }

  public static Long multiply(Long l, Long l2) {
    return l * l2;
  }

  public static F2<Long, Long, Long> multiply() {
    return Longs::multiply;
  }

  public static Long product(FList<Long> list) {
    return list.foldr(multiply(),1L);
  }

  private static FList<Long> inc(Long from, Long to, F1<Long,Long> fn) {
    if(from > to)
      return FList.empty();
    else
      return flist(from, () -> inc(fn.call(from), to, fn));
  }

  public static F1<Long,Long> dec(Long l) {
    return ll -> ll - l;
  }

  public static F1<Long,Long> inc(Long l) {
    return ll -> ll + l;
  }

  public static F1<Long,Long> mul(Long l) {
    return ll -> ll * l;
  }

  public static F1<Long,Long> div(Long l) {
    return ll -> ll / l;
  }

  public static Long sum(FList<Long> flist) {
    return flist.foldr(add(), 0L);
  }

  public static F1<FList<Long>, Long> sum() {
    return Longs::sum;
  }

   public static FList<Long> range(Long from, Long to) {
    if(from > to)
      return FList.empty();
    else
      return flist(from, () -> range(from + 1, to));
  }

  public static FList<Long> range(Long from, Long to, F1<Long,Long> fn ) {
    if(from == to)
      return flist(from);
    else if(from > to)
      return dec(from, to, fn);
    else
      return inc(from, to, fn);
  }

  private static FList<Long> dec(Long from, Long to, F1<Long,Long> fn) {
    if(from < to)
      return FList.empty();
    else
      return flist(from, () -> dec(fn.call(from), to, fn));
  }

  public static long mod(long a, long b) {
    return a - (b * (a/b));
  }

  public static long lcm(long i, long i2) {
    return (i * i2) / gcd(i, i2);
  }

  public static long gcd(long a, long b) {
    return gcd_prime(a,b).result();
  }

  public static Trampoline<Long> gcd_prime(long a, long b) {
    if(b == 0)
      return done(a);
    else
      return more(() -> gcd_prime(b, mod(a,b)));
  }
}
