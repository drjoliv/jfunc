package drjoliv.jfunc.generators;

import com.pholser.junit.quickcheck.generator.ComponentizedGenerator;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Size;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import drjoliv.jfunc.data.list.FList;

@SuppressWarnings("rawtypes")
public class FListGenerator extends ComponentizedGenerator<FList> {

    public FListGenerator() {
     super(FList.class);
    }

    private Size size;

  @Override
  public FList<?> generate(SourceOfRandomness rand, GenerationStatus status) {
    int max = size != null ? size.max() : 100; 
    int min = size != null ? size.min() : 0; 

    FList list = FList.empty();
    for(int i = rand.nextInt(min, max); i > 0; i--) {
      list.cons(componentGenerators().get(0).generate(rand, status));
    }
    return list;
  }

  public void configure(Size size) {
    this.size = size;
  }

  @Override
  public int numberOfNeededComponents() {
    return 1;
  }
}
