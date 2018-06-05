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

import static org.hamcrest.number.OrderingComparison.*;
import static org.hamcrest.Matchers.*;

import org.hamcrest.Matcher;
import org.hamcrest.number.OrderingComparison;
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
    if(que.size() != 0)
      assertFalse(que.isEmpty());

    while(que.size() != 0) {
      que = que.popFront()
        .toNull()._2(); 
    }
    assertTrue(que.isEmpty());
  }

  @Property
  public void size(@From(DequeueGenerator.class)Dequeue<Integer> que) {
    int acc = 0;
    int s = que.size();

    while(que.size() != 0) {
      que = que.popFront()
        .toNull()._2(); 

      acc++;
    }
    assertEquals(s,acc);
  }

  @Property
  public void first(@From(DequeueGenerator.class)Dequeue<Integer> que, Integer i) {
    que = que.pushFront(i);
    assertEquals(i, que.first().toNull());
  }

  @Property
  public void last(@From(DequeueGenerator.class)Dequeue<Integer> que, Integer i) {
    que = que.pushBack(i);
    assertEquals(i, que.last().toNull());
  }

  @Property
  public void takeFront(@From(DequeueGenerator.class)Dequeue<Integer> que, @InRange(minInt=0)Integer i) {
    FList<Integer> l = que.takeFront(i);

    if(i > que.size()) {
      assertThat(new Integer(l.size()), is(que.size()));
    }
    assertThat(l.size(), OrderingComparison.lessThanOrEqualTo(que.size()));

    while(que.size() != 0) {
       Integer item = que.popFront()
        .toNull()._1(); 
       assertEquals(l.head(),item);
       que = que.popFront()
        .toNull()._2(); 
       l = l.tail();
    }
  }

  @Property
  public void takeBack(@From(DequeueGenerator.class)Dequeue<Integer> que, Integer i) {
    FList<Integer> l = que.takeBack(i);

    if(i > que.size()) {
      assertThat(new Integer(l.size()), is(que.size()));
    }
    assertThat(l.size(), OrderingComparison.lessThanOrEqualTo(que.size()));

    while(que.size() != 0) {
       Integer item = que.popBack()
        .toNull()._1(); 
       assertEquals(l.head(),item);
       que = que.popBack()
        .toNull()._2(); 
       l = l.tail();
    }
  }

  @Property
  public void pushBack(@From(DequeueGenerator.class)Dequeue<Integer> que, Integer i) {
    assertThat(que.pushBack(i).size(),
        is(equalTo(que.size() + 1)));

    assertThat(que.pushBack(i).last().toNull()
        , is(equalTo(i)));

    assertNotNull(que.pushBack(i)
        .popBack().toNull()._1());

    assertThat(que.pushBack(i)
        .popBack().toNull()._1()
        , is(equalTo(i)));
  }

  //@Property
  //public void pushFront(@From(DequeueGenerator.class)Dequeue<Integer> que, Integer i) {
  //  assertThat(que.pushBack(i).size(),
  //      is(equalTo(que.size() + 1)));

  //  assertThat(que.pushBack(i).last().toNull()
  //      , is(equalTo(i)));

  //  assertThat(que.pushBack(i)
  //      .popBack().toNull()._1()
  //      , is(equalTo(i)));
  //}

  //@Property
  //public void popFront(@From(DequeueGenerator.class)Dequeue<Integer> que) {
  //  fail("not yet implemented");
  //}

  //@Property
  //public void popBack(@From(DequeueGenerator.class)Dequeue<Integer> que) {
  //  fail("not yet implemented");
  //}
}
