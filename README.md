# fjava
Attempt and working functional java library

```java

  public static boolean isPrime(Integer i) {
   final int sqrtOfi = (int)Math.sqrt(i);
   return allTrueWhile(primes, p -> p <= sqrtOfi
       , p -> i % p != 0);
  }

  public static FList<Integer> primes = of(2, 3, () -> start(4).filter(Numbers::isPrime));

  print(primes.take(12));

  //[2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37]

  print(primes.take(12).map( i -> i + 2));

  //[4, 5, 7, 9, 13, 14, 19, 21, 25, 27, 39, 39]
```
