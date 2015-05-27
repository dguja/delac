package hr.fer.zemris.composite.cluster.clusterable;

import java.util.Arrays;

public class ReliabilityImpactVector implements IClusterable {

  private double[] reliabilityImpact;

  public ReliabilityImpactVector(double[] reliabilityImpact) {
    this.reliabilityImpact = Arrays.copyOf(reliabilityImpact, reliabilityImpact.length);
  }

  public double[] getReliabilityImpact() {
    return reliabilityImpact;
  }

  @Override
  public int getDimension() {
    return reliabilityImpact.length;
  }

  @Override
  public double getComponent(int index) {
    if (index >= reliabilityImpact.length) {
      throw new IndexOutOfBoundsException();
    }
    return reliabilityImpact[index];
  }

  @Override
  public IClusterable copy() {
    return new ReliabilityImpactVector(reliabilityImpact);
  }

}
