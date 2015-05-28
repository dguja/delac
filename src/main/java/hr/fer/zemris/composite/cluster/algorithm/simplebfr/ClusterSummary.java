package hr.fer.zemris.composite.cluster.algorithm.simplebfr;

import hr.fer.zemris.composite.cluster.clusterable.IClusterable;

public class ClusterSummary {

  private int n;

  private IClusterable sum;

  private ClusterSummary(int n, IClusterable sum) {
    super();
    this.n = n;
    this.sum = sum;
  }

  public ClusterSummary(IClusterable point) {
    super();
    this.sum = point;
    this.n = 1;
  }

  public IClusterable getCentroid() {
    return sum.nScalarMultiply(1. / n);
  }

  public void addClusterSummary(ClusterSummary other) {
    sum.add(other.sum);
    n += other.n;
  }

  public ClusterSummary copy() {
    return new ClusterSummary(n, sum);
  }
}
