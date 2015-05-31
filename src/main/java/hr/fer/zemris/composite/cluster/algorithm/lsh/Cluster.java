package hr.fer.zemris.composite.cluster.algorithm.lsh;

import hr.fer.zemris.composite.cluster.ICluster;
import hr.fer.zemris.composite.cluster.clusterable.IClusterable;

import java.util.Collections;
import java.util.List;

public class Cluster implements ICluster {
  
  private final List<IClusterable> points;
  
  public Cluster(final List<IClusterable> points) {
    super();

    this.points = points;
  }
  
  @Override
  public int getN() {
    return points.size();
  }

  @Override
  public List<IClusterable> getPoints() {
    return Collections.unmodifiableList(points);
  }
  
}
