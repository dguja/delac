package hr.fer.zemris.composite.generator.reliability;

import java.util.Arrays;



public class ReliabilityImpactVector {

  private double[] reliabilityImpact;

  public ReliabilityImpactVector(double[] reliabilityImpact) {
    this.reliabilityImpact = Arrays.copyOf(reliabilityImpact, reliabilityImpact.length);
  }

  public double[] getReliabilityImpact() {
    return reliabilityImpact;
  }
  
}
