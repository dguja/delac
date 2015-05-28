package hr.fer.zemris.composite.cluster.clusterable;

public interface IClusterable {

  int getDimension();

  double get(int index);

  void set(int index, double value);
  
  void add(IClusterable other);
  
  IClusterable nScalarMultiply(double scalar);
  
  IClusterable copy();

}
