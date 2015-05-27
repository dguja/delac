package hr.fer.zemris.composite.cluster.clusterable;

public interface IClusterable {

  int getDimension();

  double getComponent(int index);

  IClusterable copy();

}
