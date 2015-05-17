package hr.fer.zemris.composite.generator.daniel.cluster;

import hr.fer.zemris.composite.generator.daniel.IClusterable;

import java.util.Arrays;

public class Vector implements IClusterable {

  private double[] values;

  public Vector(double[] values) {
    this.values = values;
  }

  @Override
  public int getDimension() {
    return values.length;
  }

  @Override
  public double[] getPoints() {
    return values;
  }

  @Override
  public double getPoint(int index) {
    if (index >= values.length) {
      throw new IndexOutOfBoundsException();
    }
    return values[index];
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(values);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Vector other = (Vector) obj;
    if (!Arrays.equals(values, other.values))
      return false;
    return true;
  }

}
