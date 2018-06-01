package drjoliv.fjava.transformers;

import org.junit.Test;

import drjoliv.fjava.control.bind.Identity;
import drjoliv.fjava.control.bind.MaybeT;
import drjoliv.fjava.control.bind.Identity.μ;

public class MaybeTTest {

  @Test
  public void identity() {
    MaybeT<Identity.μ,Integer> maybeT = MaybeT.liftMaybeT(Identity.id(2));
  }

}
