package drjoliv.fjava.transformers;

import static drjoliv.fjava.control.bind.WriterT.Writer.asWriter;
import static drjoliv.fjava.control.bind.WriterT.Writer.sequence_;
import static drjoliv.fjava.control.bind.WriterT.Writer.writer;
import static drjoliv.fjava.data.FList.flist;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.function.Function;

import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

import drjoliv.fjava.control.bind.Identity;
import drjoliv.fjava.control.bind.WriterT;
import drjoliv.fjava.control.bind.WriterT.Writer;
import drjoliv.fjava.data.FList;
import drjoliv.fjava.data.Maybe;
import drjoliv.fjava.data.Monoid;
import drjoliv.fjava.data.Unit;
import drjoliv.fjava.data.generators.FListGenerator;
import drjoliv.fjava.functions.F1;

@RunWith(JUnitQuickcheck.class)
public class WriterTTest {

  private static Monoid<FList<Integer>> intMonoidFlist = FList.instances.monoidInstance();

  private static Writer<FList<Integer>,Integer> writerInteger(Integer i) {
    return writer(i,flist(i),intMonoidFlist);
  }

  @Property
  public void exec(Integer i) {
    assertEquals(writerInteger(i)
        .exec(), i);
  }

  @Property
  public void log(@From(FListGenerator.class)FList<Integer> flist) {
    Maybe<WriterT<Identity.μ,FList<Integer>,Unit>> maybeWriter =
      sequence_(flist.map(WriterTTest::writerInteger));

    if(maybeWriter.isSome()) {
      FList<Integer> list = asWriter(maybeWriter.toNull()).log();
      assertEquals(flist,list);
    }
  }

  public void semi() {
    fail("Not yet implemented");
  }

  @Property
  public void map(@From(FListGenerator.class)FList<Integer> flist, Integer multipler, Integer added) {
    F1<Integer,Integer> mul = i -> i * multipler; 
    F1<Integer,Integer> adder = i -> i + added;
    F1<Integer,Integer> mul_adder = mul.then(adder);

    F1<Integer,Writer<FList<Integer>,Integer>> f1w = writer(intMonoidFlist);
    Writer<FList<Integer>,Integer>  w = f1w.call(1);
    assertEquals(w.map(mul_adder).exec(), w.map(mul).map(adder).exec());
  }

}
