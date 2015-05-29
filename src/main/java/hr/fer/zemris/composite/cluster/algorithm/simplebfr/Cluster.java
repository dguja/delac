package hr.fer.zemris.composite.cluster.algorithm.simplebfr;

import hr.fer.zemris.composite.cluster.ICluster;
import hr.fer.zemris.composite.cluster.clusterable.IClusterable;

import java.util.ArrayList;
import java.util.List;

public class Cluster implements ICluster {

  private List<IClusterable> points = new ArrayList<>();

  private ClusterSummary clusterSummary;

  public Cluster(IClusterable point) {
    super();
    points.add(point);
    clusterSummary = new ClusterSummary(point);
  }

  @Override
  public int getN() {
    return points.size();
  }
  
  @Override
  public List<IClusterable> getPoints() {
    return points;
  }

  public ClusterSummary getClusterSummary() {
    return clusterSummary;
  }

  public void addSubcluster(Cluster subcluster) {
    points.addAll(subcluster.getPoints());
    clusterSummary.addClusterSummary(subcluster.getClusterSummary());
  }

}
