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
    n = 1;
    sum = point;
  }

  public IClusterable getCentroid() {
    return sum.nScalarMultiply(1. / n);
  }

  public void addClusterSummary(ClusterSummary other) {
    n += other.n;
    sum.add(other.sum);
  }

  public ClusterSummary copy() {
    return new ClusterSummary(n, sum);
  }
}
