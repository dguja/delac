package hr.fer.zemris.composite.cluster.algorithm.bfr;

import hr.fer.zemris.composite.cluster.clusterable.IClusterable;
import hr.fer.zemris.composite.cluster.clusterable.Vector;

public class ClusterSummary {

  private int n;

  private IClusterable sum;

  private IClusterable sumsq;

  private ClusterSummary(int n, IClusterable sum, IClusterable sumsq) {
    super();
    this.n = n;
    this.sum = sum;
    this.sumsq = sumsq;
  }

  public ClusterSummary(IClusterable point) {
    super();
    n = 1;
    sum = point;
    sumsq = new Vector(point.getDimension());
    for (int i = 0; i < point.getDimension(); ++i) {
      sumsq.set(i, Math.pow(point.get(i), 2));
    }
  }

  public IClusterable getCentroid() {
    return sum.nScalarMultiply(1. / n);
  }
  
  public IClusterable getVariance() {
    IClusterable variance = new Vector(sum.getDimension());
    for (int i = 0; i < variance.getDimension(); ++i) {
      variance.set(i, sumsq.get(i) / n - Math.pow(sum.get(i) / n, 2));
    }
    return variance;
  }

  public void addClusterSummary(ClusterSummary other) {
    n += other.n;
    sum.add(other.sum);
    sumsq.add(other.sumsq);
  }

  public ClusterSummary copy() {
    return new ClusterSummary(n, sum, sumsq);
  }

}
