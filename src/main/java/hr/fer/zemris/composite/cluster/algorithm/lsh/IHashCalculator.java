package hr.fer.zemris.composite.cluster.algorithm.lsh;

import hr.fer.zemris.composite.cluster.clusterable.IClusterable;

public interface IHashCalculator {
  
  Signature hashFunction(IClusterable clusterable);
  
  int getFunctionCount();
  
}
