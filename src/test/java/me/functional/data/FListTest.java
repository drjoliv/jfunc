package me.functional.data;

import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

import me.functional.Numbers;
import me.functional.Trampoline;
import me.functional.data.generators.FListGenerator;

import static org.junit.Assert.*;

import org.junit.Test;

@RunWith(JUnitQuickcheck.class)
public class FListTest {

  @Property
  public void add(@From(FListGenerator.class)FList<Integer> flist, Integer i) {
    assertEquals(flist.add(i).unsafeHead(), i);
  }

  @Property
  public void concat(@From(FListGenerator.class)FList<Integer> flist,
     @From(FListGenerator.class)FList<Integer> flistSnd, Integer i) {


    assertEquals(flist.size() + flistSnd.size()
        ,flist.concat(flistSnd).size());

    assertEquals(flist.size() + flistSnd.size()
        ,flist.concat(flistSnd).size());
 }

  @Property
  public void unsafeGet(@From(FListGenerator.class)FList<Integer> flist, Integer i) {
  }
}
