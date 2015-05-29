package hr.fer.zemris.composite.cluster.clusterable;

import java.util.Arrays;

public class Vector implements IClusterable {

  private double[] values;

  public Vector(int dimension) {
    values = new double[dimension];
  }

  public Vector(double[] values) {
    this.values = Arrays.copyOf(values, values.length);
  }

  @Override
  public int getDimension() {
    return values.length;
  }

  @Override
  public double get(int index) {
    if (index >= values.length) {
      throw new IndexOutOfBoundsException();
    }
    return values[index];
  }

  @Override
  public void set(int index, double value) {
    if (index < 0 || index >= values.length) {
      throw new IndexOutOfBoundsException();
    }
    values[index] = value;
  }

  @Override
  public void add(IClusterable other) {
    if (getDimension() != other.getDimension()) {
      throw new IllegalArgumentException("Incompatible array length.");
    }

    for (int i = 0; i < getDimension(); ++i) {
      values[i] += other.get(i);
    }
  }

  @Override
  public IClusterable nScalarMultiply(double scalar) {
    IClusterable vector = this.copy();
    for (int i = 0; i < values.length; ++i) {
      vector.set(i, vector.get(i) * scalar);
    }
    return vector;
  }

  @Override
  public IClusterable copy() {
    return new Vector(values);
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

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    for (int i = 0; i < values.length; i++) {
      sb.append(String.format("%.3f", values[i]));
      if (i != values.length - 1) {
        sb.append(", ");
      }
    }
    sb.append(")");
    return sb.toString();
  }

}
