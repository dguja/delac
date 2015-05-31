package hr.fer.zemris.composite.cluster.algorithm.lsh.calculators;

import hr.fer.zemris.composite.cluster.algorithm.lsh.IHashCalculator;
import hr.fer.zemris.composite.cluster.algorithm.lsh.Signature;
import hr.fer.zemris.composite.cluster.clusterable.IClusterable;

public class EuclideanCalculator implements IHashCalculator {

  private final int functionCount;
  
  private final double a;
  
  public EuclideanCalculator(final int functionCount, final double spacing) {
    super();
    
    this.functionCount = functionCount;
    this.a = spacing;
  }
  
  @Override
  public Signature hashFunction(final IClusterable clusterable) {
    // TODO Auto-generated method stub
    return null;
  }
  
  public int getFunctionCount() {
    return functionCount;
  }
  
}
