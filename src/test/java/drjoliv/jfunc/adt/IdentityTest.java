package drjoliv.jfunc.adt;

import static drjoliv.jfunc.monad.Identity.asIdentity;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

import drjoliv.jfunc.monad.Identity;
import drjoliv.jfunc.monad.Monad;

@RunWith(JUnitQuickcheck.class)
public class IdentityTest {

  @Test
  public void For() {
   Identity<Integer> m = Identity.id(1); 
   Monad.For(m
       , i -> {
         assertEquals(new Integer(1),i);
         return Identity.id(2);
       });
  }

  @Test
  public void For2() {
     Identity<Integer> m = Identity.id(1); 
   Monad.For(m
       , i -> {
         assertEquals(new Integer(1),i);
         return Identity.id(i + 1);
      }, (i,i2) -> {
         assertEquals(new Integer(1),i);
         assertEquals(new Integer(i + 1), i2);
         return Identity.id(i + 1);
      });

  }

  @Test
  public void For3() {
       Identity<Integer> m = Identity.id(1); 
   Monad.For(m
       , i -> {
         return Identity.id(i + 1);
      }, (i,i2) -> {
         assertEquals(new Integer(1),i);
         return Identity.id(i2 + 1 );
      }, (i,i2,i3) -> {
         assertEquals(new Integer(1),i);
         assertEquals(new Integer(i + 1), i2);
         assertEquals(new Integer(i2 + 1), i3);
         return Identity.id(i + 1);
      });


  }

  @Test
  public void For4(){
        Identity<Integer> m = Identity.id(1); 
   Monad<Identity.μ,Integer> monad = Monad.For(m
       , (i) -> {
         assertEquals(new Integer(1),i);
         return Identity.id(i + 1);
      }, (i,i2) -> {
         assertEquals(new Integer(1),i);
         assertEquals(new Integer(i + 1), i2);
         return Identity.id(i2 + 1);
      }, (i,i2,i3) -> {
         assertEquals(new Integer(1),i);
         assertEquals(new Integer(i + 1), i2);
         assertEquals(new Integer(i2 + 1), i3);
         return Identity.id(i3 + 1);
      }, (i,i2,i3,i4) -> { 
         assertEquals(new Integer(1),i);
         assertEquals(new Integer(i + 1), i2);
         assertEquals(new Integer(i2 + 1), i3);
         assertEquals(new Integer(i3 + 1), i4);
         return Identity.id(i4 + 1); 
      });

    assertEquals(Identity.monad(monad).value(), new Integer(5));
  }
}
