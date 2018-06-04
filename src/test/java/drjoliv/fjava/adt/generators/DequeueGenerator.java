package drjoliv.fjava.adt.generators;

import com.pholser.junit.quickcheck.generator.ComponentizedGenerator;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import drjoliv.fjava.adt.Dequeue;
import static drjoliv.fjava.adt.Dequeue.*;
import drjoliv.fjava.adt.FList;

@SuppressWarnings("rawtypes")
public class DequeueGenerator extends ComponentizedGenerator<Dequeue> {

    public DequeueGenerator() {
     super(Dequeue.class);
    }

  @SuppressWarnings("unchecked")
  @Override
  public Dequeue<?> generate(SourceOfRandomness rand, GenerationStatus status) {
    FList list = FList.empty();
    for(int i = rand.nextInt(0, 100); i > 0; i--) {
      list.add(componentGenerators().get(0).generate(rand, status));
    }
    return dequeue(list);
  }

  @Override
  public int numberOfNeededComponents() {
    return 1;
  }
}
