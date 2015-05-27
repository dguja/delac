package hr.fer.zemris.composite.cluster;

import hr.fer.zemris.composite.cluster.clusterable.IClusterable;

public interface ICluster {

  int getSize();

  IClusterable getComponent(int index);

}
