package drjoliv.fjava.data;

import org.junit.Test;
import drjoliv.fjava.data.DList;
import static drjoliv.fjava.data.DList.*;

public class DListTest {

  @Test
  public void concat() {

  }

  @Test
  public void map() {
    DList.<Integer>empty()
      .add(1)
      .add(2)
      .add(3)
      .add(4);

  }

}
