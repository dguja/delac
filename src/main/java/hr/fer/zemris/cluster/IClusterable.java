package hr.fer.zemris.cluster;

public interface IClusterable {

  double getPoint(int index);

  int getDimension();

  IClusterable copy();

}
