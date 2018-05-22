package drjoliv.fjava;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import drjoliv.fjava.control.Bind;
import drjoliv.fjava.control.bind.Trampoline;
import static drjoliv.fjava.control.bind.Trampoline.*;

import java.math.BigInteger;

import drjoliv.fjava.functions.F0;
import drjoliv.fjava.functions.F2;

public class TrampolineTest {

  public static Trampoline<BigInteger> fib(BigInteger i) {
    if (i.compareTo(BigInteger.ZERO) == -1)
      return done(BigInteger.ONE);
    else if(i.compareTo(BigInteger.ONE) == 0)
      return done(BigInteger.ONE);
    else {
      Trampoline<BigInteger> fib$  = fib(i.add(BigInteger.valueOf(-1)));
      Trampoline<BigInteger> fib$$ = fib(i.add(BigInteger.valueOf(-2)));
      Bind<Trampoline.μ,BigInteger> m = Bind.liftM2(fib$, fib$$ , (a,b) -> a.add(b));
      return asTrampoline(m);
    }
  }


  public static Trampoline<BigInteger> fact(BigInteger n) {
    if(n.equals(BigInteger.ZERO))
      return done(BigInteger.ONE);
    else if(n.equals(BigInteger.ONE))
      return done(BigInteger.ONE);
    else{
      return more(() -> {
        return fact(n.add(BigInteger.ONE.negate()))
          .map(i -> n.multiply(i));
      });
    }
  }

}
