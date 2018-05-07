# Functional Java Tools

This library is an attempt at create a working functional java library. By functional I mean the libray containes data types that are immutable and lazy. Along with lots of tools to create composble operations and functions.

What does Lazy mean in a programming language like Java. Java is normally a strict programming language, that is values within Java are evaluted before they are used, opposed to a language like Haskell where values are not evaluted until they are used. 

In Haskell all values exist in a lazy context, and nothing is evaluted before it is used, becuase in Java everyting exists within a strict context we must create a context in which the values of things are not evalueted until they are needed. Every construction within this code base computes the values within them in a lazy context.


===============

* [Persistent Data Structures](persistent-data-structures)
  * [FList](#flist)
  * [DList](#dlist)
  * [Either](#either)
  * [Maybe](#maybe)
  * [Tuples](#tuples)
  * [Map](#map)
* Functions
  * F0-F8
  * P1-P8
  * Try0-Try8
* Trampoline
* Eval
* Excpetion Handling
  * Try
* Parsing
* Monads
  * Transformers
    * MaybeT
    * ReaderT
    * WriterT
    * StateT
    * TryT



## Persistent Data Structures

### FList

FList is immutable and Lazy, FList is analogous to a stream of values.

FList a = Cons a (FList a) | Nil

Becuase FList is lazy we can create infinite list of elements. The below example is an infinite lists of even numbers.

```java
  FList<Integer> evenNumber = start(2).filter(i % 2 == 0);
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

### DList

### Either

The Either type is paramatized by two type parameters. Each parameter represents a possible value that the either can hold, so a Either<L,R> can contain avalue of type L or a value of type R.

The Either type is vary useful for describing computation that can return one of two possible values that hava differnt types, makeing it very useful for error handling.

```java
  public static Either<String,FList<Integer>>> divBy(Integer i, FList<Integer> xi) {
    if(xi.isEmpty())
      return right(empty());
    else if(xi.head() == 0)
      return left("div by zero error");
    else
      return divBy(i, xi.tail())
        .match(l -> l
              ,r -> right(flist(i / xi.head(), () -> r.value())));
  }
```

```java
  @POST
  @Path("/login")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getToken(Login login) {

    EntityManger em = getEntityManager();

    Either<Exception, User> userRead =
      read(User.class, login.username)
        .runAndClose(em);

    Either<Response, Response> response =
      userRead.bimap(forbidden,validate.call(login))

    return either(response);
  }

  public static F1<Excpetion,Response> forbidden = 
    e -> Response.status(Response.Status.FORBIDDEN).entity(new ErrorResponse(e.getMessage())).build();

  public static F2<Login,User,Response> validate =
    (l,u) -> {
      String ps = u.getPassword();
      if (ps.equals(l.password)) {
        return Response.ok().entity(tokenGen(u)).build();
      } else {
        return Response.status(Response.Status.UNAUTHORIZED).build();
      }
    };
```

Their are no else/if statements in the above code, we abstract them away with bimap. If the either is a left containing an Excpetion the function forbidden is used to transform the the excpetion to a Response, if the value within the either is a right containing a user the partialy applied function validate is called. validate.call(login) == F1<User,Reponse>

The function either takes and Either whose left and right value are of the same type, and returns the value within the Either.

## Maybe

```java
  public static FList<Maybe<Integer>>> divBy(Integer i, FList<Integer> xi) {
    if(xi.isEmpty())
      return empty();
    else if(xi.head() == 0)
      return flist(nothing(), () -> divBy(i, xi.tail()));
    else
      return flist(maybe(i / xi.head()) () -> divBy(i, xi.tail());
  }
```

#### MaybeT

## Try

### Monads

### Transformers

## Trampoline

```java
  public static Trampoline<BigInteger> fib(Integer i) {
    return fib_prime(BigInteger.ZERO, BigInteger.ONE, i);
  }

  public static Trampoline<BigInteger> fib_prime(BigInteger a, BigInteger b, Integer i) {
    if(i == 0) return done(a);
    if(i == 1) return done(b);
    return fib_prime(b, a.add(b), i - 1);
  }
```

```java
  public static Trampoline<BigInteger> fact(BigInteger n) {
    if(n.equals(BigInteger.ZERO))
      return done(BigInteger.ONE);
    else if(n.equals(BigInteger.ONE))
      return done(BigInteger.ONE);
    else{
      return more(() -> {
        return fact(n.add(BigInteger.ONE.negate())).map(i -> n.multiply(i));
      });
    }
  }
```






#### ReaderT

#### WriterT

#### StateT

#### ParserT

Examples


```java

```

## References

Purely Functional Data Structures -Chris Okasaki
