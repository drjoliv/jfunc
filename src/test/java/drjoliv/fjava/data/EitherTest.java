package drjoliv.fjava.data;

import static drjoliv.fjava.Numbers.fibonacci;
import static drjoliv.fjava.Numbers.isEven;
import static drjoliv.fjava.Numbers.range;
import static drjoliv.fjava.Numbers.sum;
import static drjoliv.fjava.data.Either.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

import drjoliv.fjava.Numbers;
import drjoliv.fjava.control.bind.Trampoline;
import static drjoliv.fjava.control.bind.Trampoline.*;
import drjoliv.fjava.data.FList;
import static drjoliv.fjava.data.FList.*;
import drjoliv.fjava.data.Either;
import drjoliv.fjava.data.Either.RightException;
import drjoliv.fjava.data.generators.F1Generator;
import drjoliv.fjava.data.generators.FListGenerator;
import drjoliv.fjava.functions.F1;

@RunWith(JUnitQuickcheck.class)
public class EitherTest {
  
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  public static <A> F1<A,A> ex(String s) {
    return a -> {
      fail(s);
      return a;
    };
  }


  @Test
  public void isRight() {
    Either<Integer,Integer> e = right$(() -> 0);
    assertTrue("We assert that e is an instance of Right.", e.isRight());
  }

  @Test
  public void isLeft() {
    Either<Integer,Integer> e = left$(() -> 0);
    assertTrue("We assert that e is an instance of Left.", e.isLeft());
  }

  @Test
  public void valueLLeft() {
    Either<Integer,Integer> e = left$(() -> 0);
    assertEquals("we assert that e contains 0.",e.valueL(), new Integer(0));
  }

  @Test
  public void valueRLeft() throws LeftException {
    Either<Integer,Integer> e = left$(() -> 0);
    thrown.expect(LeftException.class);
    e.valueR();
  }


  @Test
  public void valueRRight() {
    Either<Integer,Integer> e = right$(() -> 0);
    assertEquals("We assert that e contains 0",e.valueR(), new Integer(0));

  }

  @Test
  public void valueLRight() throws RightException {
    Either<Integer,Integer> e = right$(() -> 0);
    thrown.expect(RightException.class);
    e.valueL();
  }

  @Property
  public void bimapLeft(@InRange(minInt=0)int val
      , @From(F1Generator.class)F1<Integer,Integer> first
      , @From(F1Generator.class)F1<Integer,Character> second) {

    Either<Integer,Integer>  e = left$(() -> val);

    Either<Character,Integer> e1 = 
       e.bimap(first,ex("The right function should not be called when either instance is Left"))
        .bimap(second,ex("The right function should not be called when either instance is Left"));

    Either<Character,Integer> e2 = 
       e.bimap(first.then(second),ex("The right function should not be called when either instance is Left"));

    assertEquals(e1.valueL(),e2.valueL());
  }

  @Property
  public void bimapRight(@InRange(minInt=0)int val
      , @From(F1Generator.class)F1<Integer,Integer> first
      , @From(F1Generator.class)F1<Integer,Character> second) {

    Either<Integer,Integer>  e = right$(() -> val);

    Either<Integer,Character> e1 = 
       e.bimap(ex("The left function should not be called when either instance is right"), first)
        .bimap(ex("The left function should not be called when either instance is right"), second);

    Either<Integer,Character> e2 = 
       e.bimap(ex("The left function should not be called when either instance is right"), first.then(second));

    assertEquals(e1.valueR(),e2.valueR());
  }

  @Property
  public void leftLeftProjectionMap(@InRange(minInt=0)int val
      , @From(F1Generator.class)F1<Integer,Integer> frst
      , @From(F1Generator.class)F1<Integer,Character> sec) {

    Either<Integer,Integer> e = left$(() -> val);
    
    Either<Character,Integer> e1 = e.left()
                                      .map(frst)
                                      .map(sec)
                                      .either();

    Either<Character,Integer> e2 = e.left()
                                      .map(frst.then(sec))
                                      .either();


    assertEquals(e1.valueL(),e2.valueL());
  }

  @Property
  public void leftRightProjectionMap(@InRange(minInt=0)int val
      , @From(F1Generator.class)F1<Integer,Integer> frst
      , @From(F1Generator.class)F1<Integer,Character> sec) {

    Either<Integer,Integer> e = left$(() -> val);
    
    Either<Integer,Character> e1 = e.right()
                                      .map(frst)
                                      .map(sec)
                                      .either();

    Either<Integer,Character> e2 = e.right()
                                      .map(frst.then(sec))
                                      .either();

    assertEquals(e1.valueL(),e2.valueL());
    assertEquals(new Integer(val),e1.valueL());
    assertEquals(new Integer(val),e2.valueL());
  }

  @Property
  public void rightLeftProjectionMap(@InRange(minInt=0)int val
      , @From(F1Generator.class)F1<Integer,Integer> frst
      , @From(F1Generator.class)F1<Integer,Character> sec) {

    Either<Integer,Integer> e = right$(() -> val);
    
    Either<Character,Integer> e1 = e.left()
                                      .map(frst)
                                      .map(sec)
                                      .either();

    Either<Character,Integer> e2 = e.left()
                                      .map(frst.then(sec))
                                      .either();

    assertEquals(e1.valueR(),e2.valueR());
    assertEquals(new Integer(val),e1.valueR());
    assertEquals(new Integer(val),e2.valueR());

  }

  @Property
  public void rightRightProjectionMap(@InRange(minInt=0)int val
      , @From(F1Generator.class)F1<Integer,Integer> frst
      , @From(F1Generator.class)F1<Integer,Character> sec) {

    Either<Integer,Integer> e = right$(() -> val);
    
    Either<Integer,Character> e1 = e.right()
                                      .map(frst)
                                      .map(sec)
                                      .either();

    Either<Integer,Character> e2 = e.right()
                                      .map(frst.then(sec))
                                      .either();

    assertEquals(e1.valueR(),e2.valueR());
  }
}
