# fjava

This library is an attempt at create a working functional java library. By functional I mean the libray containes data types that are immutable and lazy. Along with lots of tools to create composble operations and functions.

What does Lazy mean in a programming language like Java. Java is normally a strict programming language, that is values within Java are evaluted before they are used, opposed to a language like Haskell where values are not evaluted until they are used. 

In Haskell all values exist in a lazy context, and nothing is evaluted before it is used, becuase in Java everyting exists within a strict context we must create a context in which the values of things are not evalueted until they are needed. Every construction within this code base computes the values within them in a lazy context.

## Useful Data Structures

## FList

FList is immutable and Lazy, FList is analogous to a stream of values.

FList a = Cons a (FList a) | Nil

Becuase FList is lazy we can create infinite list of elements. The below example is an infinite lists of even numbers.

```java
  public static boolean Presdicate<Integer> =  i % 2 == 0;

  FList<Integer> evenNumber = start(2).filter(isEven);
```

Below is an example of creating an infite list of prime numbers.

```java

  public static boolean isPrime(Integer i) {
   final int sqrtOfi = (int)Math.sqrt(i);
   return allTrueWhile(primes, p -> p <= sqrtOfi
       , p -> i % p != 0);
  }

  public static FList<Integer> primes = flist(2, 3, () -> start(4).filter(Numbers::isPrime));

  print(primes.take(12));

  //[2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37]

  print(primes.take(12).map( i -> i + 2));

  //[4, 5, 7, 9, 13, 14, 19, 21, 25, 27, 39, 39]
```

## Either

## Maybe

## Try






### Monads

### Transformers

#### MaybeT

#### ReaderT

#### WriterT

#### StateT

#### ParserT
