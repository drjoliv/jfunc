package drjoliv.jfunc.data.set;

import static drjoliv.jfunc.data.set.UnbalancedSet.fromFoldable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.Size;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

import drjoliv.jfunc.data.list.FList;
import drjoliv.jfunc.eq.Ord;
import drjoliv.jfunc.hlist.T2;

@RunWith(JUnitQuickcheck.class)
public class UnbalancedSetTest {

  private UnbalancedSet<Integer> set;

  @Before
  public void before() {
    set = UnbalancedSet.empty(Ord.<Integer>orderable());
  }

  @Property
  public void fromFoldableTest(@Size(max = 20)FList<Integer> flist) {
    Set<Integer> set =
      fromFoldable(flist, Ord.<Integer>orderable());

    for(Integer i : flist)
      assertTrue("Value :" + i + " should be the set.", set.member(i));
  }

  @Property
  public void ascendingList(@Size(max = 20)FList<Integer> nums) {
    set = fromFoldable(nums, Ord.<Integer>orderable());

    FList<Integer> sortedList = set.ascendingList();

    sortedList
      .window(2)
      .map(l -> T2.t2( l.get(0), l.get(1) ) )
      .forEach(t -> assertTrue("List should be in order.", t._1() < t._2()));
  }

  @Property
  public void descendingList(@Size(max = 20)FList<Integer> nums) {
    set = fromFoldable(nums, Ord.<Integer>orderable());

    FList<Integer> sortedList = set.descendingList();

    sortedList
      .window(2)
      .map(l -> T2.t2( l.get(0), l.get(1) ) )
      .forEach(t -> assertTrue("List should be in order.", t._1() > t._2()));
  }

  @Test
  public void size() {
    int num_of_elements = 100;

    for(int i = 1; i < num_of_elements; i++) {
      set = set.insert(i);
      assertEquals("Size of the list should match the iteration number.", set.size(), i);
    }
  }

  @Property
  public void member(@Size(max = 20)FList<Integer> flist) {
    fromFoldable(flist, Ord.<Integer>orderable())
      .toList()
      .forEach(i -> assertTrue("Value should be within the set.", set.member(i)));
  }

  @Property
  public void insert(@Size(max = 20)FList<Integer> flist) {
    for(Integer i : flist) {
      set = set.insert(i);
      assertTrue("This value should be a memeber of the set.", set.member(i));
    }
  }

  @Property
  public void isEmpty(@Size(max = 20)FList<Integer> flist) {
    assertTrue("Both FList and Set should return the smae value from isEmpty."
        , fromFoldable(flist, Ord.<Integer>orderable()).isEmpty() == flist.isEmpty() );
  }
}
