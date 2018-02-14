package me.functional.data;

import org.junit.Test;
import static org.junit.Assert.*;

public class FListTest {

  @Test
  public void append() {
    FList<Integer> ints = FList.<Integer>empty()
      .prepend(3);

    assertEquals(new Integer(3),ints.head());

    assertEquals(FList.<Integer>empty(), ints.tail());

    System.out.println(FList.toString(ints));

    System.out.println(FList.toString(FList.of(1,2,3,4,5,6,7,8).map(i -> i * 2)));

    System.out.println(FList.toString(FList.of(1,2,3,4,5,6,7,8).bind(i -> FList.of( i + "" + i))));

    System.out.println(FList.toString(FList.of(1,2,3,4,5,6,7,8).filter(i -> i % 2 == 0)));

    FList<Integer> nums = FList.range(0,999);

    //System.out.println(FList.toString(nums));

    System.out.println(FList.toString(nums.filter(i -> i % 5 == 0 || i % 3 == 0)));

    int sum = 0;
    for(Integer i : FList.iterable(nums.filter(i -> i % 5 == 0 || i % 3 == 0)))
      sum += i;

    System.out.println(sum);




    FList<Integer> testSeq = FList.sequence(1, i -> i * 2 - 3);
    FList<Integer> testSeqPlusOne = testSeq.map(i -> i + 1);
    System.out.println(FList.toString(testSeq.take(10)));
    System.out.println(FList.toString(testSeqPlusOne.take(10)));

  }
}
