package hr.fer.zemris.composite.cluster.algorithm.simplebfr;

import hr.fer.zemris.composite.cluster.clusterable.IClusterable;
import hr.fer.zemris.composite.cluster.clusterable.Vector;


public class ClusterSummary {

  private int n;
  
  private IClusterable sum;
  
  public ClusterSummary(IClusterable point) {
    super();
    this.sum = point;
    this.n = 1;
  }

  public int getN() {
    return n;
  }
  
  public IClusterable getCentroid() {
    return sum.nScalarMultiply(1./n);
  }
     
  public void addClusterSummary(ClusterSummary other) {
    sum.add(other.sum);
    n += other.n;
  }
}
