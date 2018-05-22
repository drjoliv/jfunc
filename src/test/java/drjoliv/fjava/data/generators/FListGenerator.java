package drjoliv.fjava.data.generators;

import com.pholser.junit.quickcheck.generator.ComponentizedGenerator;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import drjoliv.fjava.data.FList;

@SuppressWarnings("rawtypes")
public class FListGenerator extends ComponentizedGenerator<FList> {

    public FListGenerator() {
     super(FList.class);
    }

  @Override
  public FList<?> generate(SourceOfRandomness rand, GenerationStatus status) {
    FList list = FList.empty();
    for(int i = rand.nextInt(0, 100); i > 0; i--) {
      list.add(componentGenerators().get(0).generate(rand, status));
    }
    return list;
  }

  @Override
  public int numberOfNeededComponents() {
    return 1;
  }
}
