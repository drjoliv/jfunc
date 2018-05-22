package drjoliv.fjava.transformers;

import static org.junit.Assert.*;

import java.util.Random;
import java.util.function.Function;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

import drjoliv.fjava.Numbers;
import drjoliv.fjava.control.Bind;
import drjoliv.fjava.control.bind.Identity;
import drjoliv.fjava.control.bind.ReaderT.Reader;
import drjoliv.fjava.data.FList;
import drjoliv.fjava.data.generators.FListGenerator;
import drjoliv.fjava.functions.F1;

import static drjoliv.fjava.Numbers.*;
import static drjoliv.fjava.control.Bind.*;
import static drjoliv.fjava.control.bind.ReaderT.Reader.*;
import static drjoliv.fjava.data.FList.*;
import static drjoliv.fjava.data.FList.functions.*;

@RunWith(JUnitQuickcheck.class)
public class ReaderTTest {

 
  @Property
  public void semi(String r) {

    final Reader<String,Integer> f1 = reader(s -> {
      assertEquals(r,s);
      return s.length();
    });

    final Reader<String,String> f2 = reader(s -> {
      assertEquals(r,s);
      return s + s;
    });

    final Reader<String,int[]> f3 = reader(s -> {
      assertEquals(r,s);
      return new int[]{s.length()};
    });


    f1.semi(f2)
    .semi(f3);
  }

  @Property
  public void mBind(Integer r) {

    final Reader<Integer,Integer> f1 = reader(s -> {
      return s * 3;
    });

    final Reader<Integer,Integer> f2 = reader(s -> {
      return s + s;
    });

    final Reader<Integer,FList<Integer>> f3 = reader(s -> {
      return flist(s);
    });

    Reader<Integer,FList<Integer>> reader = asReader(
      For(f1
        , len -> {
          assertEquals(new Integer(r * 3), len);
          return f2;
        },(len, str) -> {
          assertEquals(new Integer(r + r), str);
          return f3;
      })
    );

    assertTrue(FList.functions.equals(reader.run(r), flist(r)));
  }

  @Property
  public void fmap(String str, Integer multipler, Integer added) {

    F1<Integer,Integer> mul = i -> i * multipler; 
    F1<Integer,Integer> adder = i -> i + added;
    F1<Integer,Integer> mul_adder = mul.then(adder);

    final Reader<String,Integer> f1 = reader(s -> {
      return s.length();
    });

    final Reader<String,Integer> f2 = f1.map(mul_adder);
    final Reader<String,Integer> f3 = f1.map(mul).map(adder);

    assertEquals(f2.run(str), f3.run(str));
  }

  @Property
  public void local(final String env) {
    final Reader<String,Integer> f1 = reader(s -> {
      assertEquals(s, env + env);
      return s.length();
    });

    readerLocal(s -> s + s, f1)
      .run(env);

  }

  @Property
  public void ask(final String str) {

    final Reader<String,Integer> f1 = reader(s -> {
      return s.length();
    });

    String env = f1
      .semi(readerAsk())
      .run(str);

    assertEquals(str,env);
  }
}
