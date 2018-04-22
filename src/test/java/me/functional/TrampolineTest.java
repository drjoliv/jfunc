package me.functional;

import org.junit.Test;

import me.functional.functions.F0;
import me.functional.functions.F2;

public class TrampolineTest {
  public static Trampoline<Long> fibs(Long i) {
    if (i == 0)
      return Trampoline.done(() -> 1L);
    else if(i == 1)
      return Trampoline.done(() -> 1L);
    else {
      return Trampoline.more(() -> zipWith(longAdder,fibs(i-1),fibs(i-2)));
    }
  }

  public static Long fib(Long i) {
    Long valOne = new Long(i-1);
    Long valTwo = new Long(i-2);
    if (i == 0L || i < 0L)
      return new Long(1);
    else if(i == 1L)
      return new Long(1);
    else {
      return fib(valOne) + fib(valTwo);
    }
  }

  public static Long fact(Long n) {
    Long one = 1L;
    Long val = new Long(n - one);
    if(n == 0)
      return new Long(1);
    else if(n == 1)
      return new Long(1);
    else{
      return n * fact(val);
    }
  }
  public static F2<Long,Long,Long> longAdder = (l1,l2) -> l1 + l2;

  public static <A> Trampoline<A> zipWith(F2<A,A,A> fn, Trampoline<A> t1, Trampoline<A> t2) {
    if(t1.isDone() && t2.isDone())
      return Trampoline.done(() -> fn.apply(t1.get(),t2.get()));
    else
      return Trampoline.more(() -> zipWith(fn, t1.step(), t2.step()));
  }

  public static <A> Trampoline<A> done(F0<A> finalValue) {
    return Trampoline.done(finalValue);
  }

}
