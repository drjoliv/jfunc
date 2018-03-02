package me.functional.data;

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

import me.functional.Numbers;
import static me.functional.Numbers.*;
import me.functional.data.generators.FListGenerator;

@RunWith(JUnitQuickcheck.class)
public class FListTest {

  @Property
  public void add(@From(FListGenerator.class)FList<Integer> flist, Integer i) {
    assertEquals(flist.add(i).unsafeHead(), i);
  }

  @Property
  public void add(@From(FListGenerator.class)FList<Integer> flist, @From(FListGenerator.class)FList<Integer> flist1) {
    assertEquals(flist.add(flist1).size(), flist.size() + flist1.size());
    assertEquals(flist.add(flist1).take(flist1.size()), flist1);
    assertEquals(flist.add(flist1).drop(flist1.size()), flist);
  }

  @Property
  public void size(@InRange(minInt=0, maxInt=100)int i) {
    FList<Integer> nil = FList.empty();
    for(int j = i; i > 0; i--) {
      nil.add(j);
    }
    assertEquals(i, nil.size());
  }

  @Property
  public void concat(@From(FListGenerator.class)FList<Integer> flist,
     @From(FListGenerator.class)FList<Integer> flistSnd) {


    assertEquals(flist.size() + flistSnd.size()
        ,flist.concat(flistSnd).size());

    assertEquals(flist.size() + flistSnd.size()
        ,flist.concat(flistSnd).size());
 }

  @Property
  public void unsafeGet(@From(FListGenerator.class)FList<Integer> flist, @InRange(minInt=0)Integer i) {
    SourceOfRandomness rand = new SourceOfRandomness(new Random());
    Integer integer = flist.unsafeGet(rand.nextInt(0, flist.size()));
    if(flist.isEmpty())
      assertNull(integer);
    else
      assertNotNull(integer);

    if(i > flist.size() - 1)
      assertNull(flist.unsafeGet(i));
    else
      assertNotNull(flist.unsafeGet(i));
  }

  @Property
  public void get(@From(FListGenerator.class)FList<Integer> flist, @InRange(minInt=0)Integer i) {
    SourceOfRandomness rand = new SourceOfRandomness(new Random());
    Maybe<Integer> integer = flist.get(rand.nextInt(0, flist.size()));
    if(flist.isEmpty())
      assertFalse(integer.isSome());
    else
      assertTrue(integer.isSome());

    if(i > flist.size() - 1)
      assertFalse(flist.get(i).isSome());
    else
      assertTrue(flist.get(i).isSome());

  }

  @Property
  public void last(@From(FListGenerator.class)FList<Integer> flist, Integer i) {
    assertTrue(flist.concat(i).last().isSome());
    assertEquals(flist.concat(i).last().value(),i);
  }

  @Property
  public void usafeLast(@From(FListGenerator.class)FList<Integer> flist, @InRange(minInt=0)Integer i) {
    assertEquals(flist.concat(i).unsafeLast(),i);
  }

  @Property
  public void lastNil() {
    assertFalse(FList.empty().last().isSome());
  }

  @Property
  public void usafeLastNil() {
    assertNull(FList.empty().unsafeLast());
  }

  @Property
  public void dropPositivt(@From(FListGenerator.class)FList<Integer> flist, @InRange(minInt=0)int i) {
    int len = flist.size();
    FList<Integer> dropped = flist.drop(i);
    if(i >= len)
      assertEquals(0,dropped.size());
    else
      assertEquals(dropped.size(), len - i);
    assertEquals(dropped, flist.drop(i));
  }

  @Property
  public void takePositive(@From(FListGenerator.class)FList<Integer> flist, @InRange(minInt=0)int i) {
    int originalSize = flist.size();
    FList<Integer> taken = flist.take(i);
    if(i >= originalSize)
      assertEquals(taken.size(),originalSize);
    else
      assertEquals(taken.size(),originalSize - i);

    assertEquals(taken, flist.take(i));
  }

  @Property
  public void fmap(@From(FListGenerator.class)FList<Integer> flist, Integer multipler, Integer added) {
    Function<Integer,Integer> mul = i -> i * multipler; 
    Function<Integer,Integer> adder = i -> i + added;
    Function<Integer,Integer> mul_adder = mul.andThen(adder);

    assertEquals(flist.fmap(mul_adder), flist.fmap(mul).fmap(adder));
  }

  @Property
  public void mBind(@From(FListGenerator.class)FList<Integer> m) {
    Function<Integer,FList<Integer>> f = i -> FList.of(i * 2);
    Function<Integer,FList<Integer>> g = i -> FList.of(i + 2);
    assertEquals(m.mBind(f).mBind(g),m.mBind(x -> f.apply(x).mBind(g)));
  }

  @Test
  public void sequence() {
    //TODO
  }

  @Property
  public void tail() {
    //TODO
  }

  @Property
  public void takeWhile() {
    //TODO
  }

  @Property
  public void reduce() {
    //TODO
  }

  @Property
  public void filter(@From(FListGenerator.class)FList<Integer> m) {
    assertTrue(isEven(sum(m.filter(Numbers::isEven))));
  }

}
