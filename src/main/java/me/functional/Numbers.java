package me.functional;

import static me.functional.data.FList.empty;
import static me.functional.data.FList.of;
import static me.functional.data.FList.functions.allTrueWhile;
import static me.functional.data.FList.functions.sequence;

import me.functional.data.FList;

public class Numbers {

  public static boolean isPrime(Integer i) {
   final int sqrtOfi = (int)Math.sqrt(i); 
   return allTrueWhile(primes, p -> p <= sqrtOfi
       , p -> i % p != 0);
  }

  public static FList<Integer> primes = of(2, 3, () -> start(4).filter(Numbers::isPrime));

  public static FList<Long> fibonacci = sequence(1L, 1L, (l1, l2) -> l1 + l2);

  public static FList<Integer> range(int from, int to) {
    if(from > to)
      return empty();
    return of(from, () -> range(from + 1, to));
  }

  public static FList<Integer> start(Integer i) {
    return of(i, () -> start(i + 1));
  }

  public static boolean isEven(int i) {
    return i % 2 == 0;
  }

  public static boolean isOdd(int i) {
    return !isEven(i);
  }

  public static boolean isEven(long i) {
    return i % 2 == 0;
  }

  public static boolean isOdd(long i) {
    return !isEven(i);
  }

  public static int sum(FList<Integer> flist) {
    return flist.reduce(0, (i1,i2) -> i1 + i2);
  }

  public static Integer add(Integer i1, Integer i2) {
    return i1 + i2;
  }

  public static Long add(Long l1, Long l2) {
    return l1 + l2;
  }

  public static Double add(Double d1, Double d2) {
    return d1 + d2;
  }
}
