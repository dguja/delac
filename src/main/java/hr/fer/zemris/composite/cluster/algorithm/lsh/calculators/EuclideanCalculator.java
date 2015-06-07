package hr.fer.zemris.composite.cluster.algorithm.lsh.calculators;

import hr.fer.zemris.composite.cluster.algorithm.lsh.IHashCalculator;
import hr.fer.zemris.composite.cluster.algorithm.lsh.Signature;
import hr.fer.zemris.composite.cluster.clusterable.IClusterable;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class EuclideanCalculator implements IHashCalculator {

  private final LineFunction[] functions;
  
  public EuclideanCalculator(final int functionCount, final int dimension, final double spacing, final Random random) {
    super();
    
    functions = new LineFunction[functionCount];
    for (int i = 0; i < functionCount; i++) {
      functions[i] = new LineFunction(dimension, spacing, random);
    }
  }
  
  @Override
  public Signature hashFunction(final IClusterable clusterable) {
    final List<Integer> components =
        Arrays.stream(functions).map(function -> function.hash(clusterable)).collect(Collectors.toList());
    return new Signature(components);
  }
  
  @Override
  public int getFunctionCount() {
    return functions.length;
  }
  
  private class LineFunction {
    
    private final double[] k;
    
    private final double kM;
    
    private final double tS;
    
    public LineFunction(final int dimension, final double spacing, final Random random) {
      k = new double[dimension];
      double kSum = 0;

      for (int i = 0; i < dimension; i++) {
        k[i] = random.nextDouble();
        kSum += Math.pow(k[i], 2);
      }
      
      kM = Math.sqrt(kSum) * spacing;
      tS = random.nextDouble();
    }

    public int hash(final IClusterable clusterable) {
      double product = 0.;
      for (int i = 0; i < k.length; i++) {
        product += k[i] * clusterable.get(i);
      }

      return (int) (product / kM - tS);
    }
    
  }
  
}
