package drjoliv.jfunc.nums;

import java.math.BigInteger;
import java.util.Comparator;

import drjoliv.jfunc.contorl.Trampoline;
import drjoliv.jfunc.data.list.FList;
import drjoliv.jfunc.data.list.Functions;

import static drjoliv.jfunc.contorl.Trampoline.*;
import static drjoliv.jfunc.data.list.FList.*;

import drjoliv.jfunc.function.F1;

public class Numbers {

  public static boolean isPrime(Long i) {
   final int sqrtOfi = (int)Math.sqrt(i); 
   return Functions.allTrueWhile(primes, p -> p <= sqrtOfi
       , p -> i % p != 0);
  }

  public static <A> A min(Comparator<A> comparator, A a1, A a2) {
    int x = comparator.compare(a1, a2);
      if(x == -1)
        return a1;
      else
        return a2;
  }

  public static <A> A max(Comparator<A> comparator, A a1, A a2) {
    int x = comparator.compare(a1, a2);
      if(x == 1)
        return a1;
      else
        return a2;
  }

  public static FList<Long> primeFactors(Long i) {
    if(i == 0)
      return FList.empty();
    else if(i == 1)
      return FList.empty();
    else {
      return primeFactors$(i, primes);
    }
  }

  private static FList<Long> primeFactors$(Long i, FList<Long> list) {
    Long x = list.head();
    if(i == x) {
      return flist(i);
    } else if (x * x > i) {
      return flist(i);
    } else if (i % x == 0) {
      return flist(x , () -> primeFactors$( i / x , list.tail()));
    } else {
      return primeFactors$(i, list.tail());
    }
  }

  public static FList<Long> primes = flist(2L, 3L, () -> Longs.start(4L).filter(Numbers::isPrime));

  public static FList<Long> fibonacci = sequence(1L, 1L, (l1, l2) -> l1 + l2);

  public static boolean even(long i) {
    return i % 2 == 0;
  }

  public static boolean odd(long i) {
    return !even(i);
  }

  public static FList<Integer> triangles() {
      return triangle_prime(1,2,1);
  }

  private static FList<Integer> triangle_prime(int n, int next, int sum) {
    return flist(n, () -> triangle_prime(next + sum, next + 1, next + sum));
  }

   public static FList<BigInteger> range(BigInteger from, BigInteger to) {
    if(from.compareTo(to) == -1)
      return FList.empty();
    else
      return flist(from, () -> range(from.add(BigInteger.ONE), to));
  }

  public static Double add(Double d1, Double d2) {
    return d1 + d2;
  }
}
