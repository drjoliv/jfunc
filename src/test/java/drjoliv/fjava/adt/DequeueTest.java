package drjoliv.fjava.adt;

import static drjoliv.fjava.adt.Either.*;
import static drjoliv.fjava.adt.FList.*;
import static drjoliv.fjava.adt.Trampoline.*;
import static drjoliv.fjava.nums.Numbers.fibonacci;
import static drjoliv.fjava.nums.Numbers.isEven;
import static drjoliv.fjava.nums.Numbers.range;
import static drjoliv.fjava.nums.Numbers.sum;
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

import drjoliv.fjava.adt.Either.RightException;
import drjoliv.fjava.adt.generators.DequeueGenerator;
import drjoliv.fjava.adt.generators.F1Generator;
import drjoliv.fjava.adt.generators.FListGenerator;
import drjoliv.fjava.functions.F1;
import drjoliv.fjava.nums.Numbers;


@RunWith(JUnitQuickcheck.class)
public class DequeueTest {

  @Property
  public void isEmpty(@From(DequeueGenerator.class)Dequeue<Integer> que) {
    fail("not yet implemented");
  }

  @Property
  public void size(@From(DequeueGenerator.class)Dequeue<Integer> que) {
    fail("not yet implemented");
  }

  @Property
  public void first(@From(DequeueGenerator.class)Dequeue<Integer> que) {
    fail("not yet implemented");
  }

  @Property
  public void last(@From(DequeueGenerator.class)Dequeue<Integer> que) {
    fail("not yet implemented");
  }

  @Property
  public void takeFront(@From(DequeueGenerator.class)Dequeue<Integer> que, int i) {
    fail("not yet implemented");
  }

  @Property
  public void takeBack(@From(DequeueGenerator.class)Dequeue<Integer> que, int i) {
    fail("not yet implemented");
  }

  @Property
  public void pushBack(@From(DequeueGenerator.class)Dequeue<Integer> que, Integer i) {
    fail("not yet implemented");
  }

  @Property
  public void pushFront(@From(DequeueGenerator.class)Dequeue<Integer> que, Integer i) {
    fail("not yet implemented");
  }

  @Property
  public void popFront(@From(DequeueGenerator.class)Dequeue<Integer> que) {
    fail("not yet implemented");
  }

  @Property
  public void popBack(@From(DequeueGenerator.class)Dequeue<Integer> que) {
    fail("not yet implemented");
  }
}
