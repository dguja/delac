package hr.fer.zemris.composite.cluster.algorithm.bfr;

import hr.fer.zemris.composite.cluster.ICluster;
import hr.fer.zemris.composite.cluster.clusterable.IClusterable;

import java.util.ArrayList;
import java.util.List;


public class Cluster implements ICluster {
  
  private List<IClusterable> clusterables = new ArrayList<>();
  
  private ClusterSummary summary;
  
  private void addClusterable(IClusterable clusterable) {
    clusterables.add(clusterable);
  }

  @Override
  public int getSize() {
    return clusterables.size();
  }

  @Override
  public IClusterable getComponent(int index) {
    return clusterables.get(index);
  }

}
