package hr.fer.zemris.composite.cluster;

import hr.fer.zemris.composite.cluster.clusterable.IClusterable;

import java.util.List;

public interface ICluster {

  List<IClusterable> getPoints();

}
