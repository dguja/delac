package hr.fer.zemris.composite.cluster.algorithm.lsh;

import hr.fer.zemris.composite.cluster.clusterable.IClusterable;

public class LshClusterable implements IClusterable {

  private final IClusterable clusterable;
  
  public LshClusterable(final IClusterable clusterable) {
    super();

    this.clusterable = clusterable;
  }
  
  @Override
  public int getDimension() {
    return clusterable.getDimension();
  }
  
  @Override
  public double get(final int index) {
    return clusterable.get(index);
  }
  
  @Override
  public void set(final int index, final double value) {
    clusterable.set(index, value);
  }
  
  @Override
  public void add(final IClusterable other) {
    clusterable.add(other);
  }
  
  @Override
  public IClusterable nScalarMultiply(final double scalar) {
    return clusterable.nScalarMultiply(scalar);
  }
  
  @Override
  public IClusterable copy() {
    return clusterable.copy();
  }
  
  public IClusterable getClusterable() {
    return clusterable;
  }
  
}
