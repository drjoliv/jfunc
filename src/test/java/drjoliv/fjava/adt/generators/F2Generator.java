package drjoliv.fjava.adt.generators;

import com.pholser.junit.quickcheck.generator.ComponentizedGenerator;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import static com.pholser.junit.quickcheck.generator.Lambdas.*;
import com.pholser.junit.quickcheck.generator.java.util.function.FunctionGenerator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import drjoliv.fjava.functions.F2;

@SuppressWarnings("rawtypes")
public class F2Generator<A,B,C> extends ComponentizedGenerator<F2> {

  public F2Generator() {
    super(F2.class);
  }

  @SuppressWarnings("unchecked")
  @Override
  public F2<A,B,C> generate(SourceOfRandomness random, GenerationStatus status) {
    return makeLambda(F2.class, componentGenerators().get(2), status);
  }

  @Override
  public int numberOfNeededComponents() {
    return 3;
  }

}
