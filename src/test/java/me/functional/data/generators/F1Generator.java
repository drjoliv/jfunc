package me.functional.data.generators;

import com.pholser.junit.quickcheck.generator.ComponentizedGenerator;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import static com.pholser.junit.quickcheck.generator.Lambdas.*;
import com.pholser.junit.quickcheck.generator.java.util.function.FunctionGenerator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import me.functional.functions.F1;

public class F1Generator<A,B> extends ComponentizedGenerator<F1> {

  public F1Generator() {
    super(F1.class);
  }

  @SuppressWarnings("unchecked")
  @Override
  public F1<A,B> generate(SourceOfRandomness random, GenerationStatus status) {
    return makeLambda(F1.class, componentGenerators().get(1), status);
  }

  @Override
  public int numberOfNeededComponents() {
    return 2;
  }

}
