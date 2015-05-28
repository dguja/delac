package hr.fer.zemris.composite.cluster.algorithm.simplebfr;

import hr.fer.zemris.composite.cluster.ICluster;
import hr.fer.zemris.composite.cluster.clusterable.IClusterable;
import hr.fer.zemris.composite.cluster.clusterable.Vector;

import java.util.ArrayList;
import java.util.List;

public class Cluster implements ICluster {

  private int dimension;
  
  private List<IClusterable> points = new ArrayList<>();
  
  private IClusterable centroid;

  @Override
  public List<IClusterable> getPoints() {
    return points;
  }

  public int getWeight() {
    return points.size();
  }
  
  public void addPoint(IClusterable point) {
    points.add(point);
  }
  
  public void addSubcluster(ICluster subcluster) {
    points.addAll(subcluster.getPoints());
  }

  public void calculateCentroid() {
    if (points.isEmpty()) {
      centroid = null;
    }

    double[] values = new double[dimension];
    for (IClusterable point : points) {
      for (int i = 0; i < dimension; ++i) {
        values[i] += point.get(i);
      }
    }
    for (int i = 0; i < dimension; ++i) {
      values[i] /= points.size();
    }

    centroid = new Vector(values);
  }

}
