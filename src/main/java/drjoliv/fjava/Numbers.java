package me.functional;

import static me.functional.data.FList.*;
import static me.functional.data.FList.functions.allTrueWhile;
import static me.functional.data.FList.functions.sequence;

import me.functional.data.FList;

public class Numbers {

  public static boolean isPrime(Integer i) {
   final int sqrtOfi = (int)Math.sqrt(i); 
   return allTrueWhile(primes, p -> p <= sqrtOfi
       , p -> i % p != 0);
  }

  public static FList<Integer> primes = flist(2, 3, () -> start(4).filter(Numbers::isPrime));

  public static FList<Long> fibonacci = sequence(1L, 1L, (l1, l2) -> l1 + l2);

  public static FList<Integer> range(int from, int to) {
    if(from > to)
      return empty();
    return flist(from, () -> range(from + 1, to));
  }

  public static FList<Integer> start(Integer i) {
    return flist(i, () -> start(i + 1));
  }

  /**
   *
   *
   * @param i
   * @return
   */
  public static boolean isEven(int i) {
    return i % 2 == 0;
  }

  /**
   *
   *
   * @param i
   * @return
   */
  public static boolean isOdd(int i) {
    return !isEven(i);
  }

  /**
   *
   *
   * @param i
   * @return
   */
  public static boolean isEven(long i) {
    return i % 2 == 0;
  }

  /**
   *
   *
   * @param i
   * @return
   */
  public static boolean isOdd(long i) {
    return !isEven(i);
  }

  /**
   *
   *
   * @param flist
   * @return
   */
  public static int sum(FList<Integer> flist) {
    return flist.reduce(0, (i1,i2) -> i1 + i2);
  }

  /**
   *
   *
   * @param i1
   * @param i2
   * @return
   */
  public static Integer add(Integer i1, Integer i2) {
    return i1 + i2;
  }

  /**
   *
   *
   * @param l1
   * @param l2
   * @return
   */
  public static Long add(Long l1, Long l2) {
    return l1 + l2;
  }

  /**
   *
   *
   * @param d1
   * @param d2
   * @return
   */
  public static Double add(Double d1, Double d2) {
    return d1 + d2;
  }
}
