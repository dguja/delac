package hr.fer.zemris.composite.cluster.algorithm.bfr;

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

  public static Cluster merge(Cluster cluster1, Cluster cluster2) {
    if (cluster1 == null) {
      return cluster2;
    }
    if (cluster2 == null) {
      return cluster1;
    }
    
    if (cluster1.clusterSummary.getN() < cluster2.clusterSummary.getN()) {
      Cluster tmp = cluster1;
      cluster1 = cluster2;
      cluster2 = tmp;
    }
    
    cluster1.points.addAll(cluster2.points);
    cluster1.clusterSummary.addClusterSummary(cluster2.clusterSummary);
    
    return cluster1;
  }

}
