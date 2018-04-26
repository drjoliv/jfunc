package me.functional.data;

import static me.functional.data.Try.*;
import static org.junit.Assert.*;

import java.util.IllegalFormatException;

import org.junit.Test;

import me.functional.data.Try.*;

public class TryTest {

  public static Try<String> getNameException = with(() -> {
   throw new IllegalArgumentException();
  });

  //@Test
  //public void recover(String s) {
  //  Either<Exception,String> e =
  //    getNameException
  //    .recover(IllegalFormatException.class, s)
  //    .run();
  //  assertTrue(e.isRight());
  //  assertEquals(e.valueR(), s);
  //}
}
