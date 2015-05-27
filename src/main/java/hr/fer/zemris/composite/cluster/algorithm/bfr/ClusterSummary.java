package hr.fer.zemris.composite.cluster.algorithm.bfr;

import hr.fer.zemris.composite.cluster.clusterable.IClusterable;

import java.util.ArrayList;
import java.util.List;

public class ClusterSummary {

  // number of points
  private int n;
  
  // sum of all vectors per component
  private double[] sum;

  // sum of squares of all vectors per component
  private double[] sumsq;
  
  // dimension of vector
  private int dim;
  
  public ClusterSummary(int n, double[] sum, double[] sumsq) {
    super();
    this.n = n;
    this.sum = sum;
    this.sumsq = sumsq;
    this.dim = sum.length;
    
    if (sumsq.length != sum.length) {
      throw new IllegalArgumentException("Different length of sum and sumsq vectors.");
    }
  }

  public double[] getCentroid() {
    double[] centroid = new double[dim];
    for (int i = 0; i < dim; ++i) {
      centroid[i] = sum[i] / n;
    }

    return centroid;
  }

  public double[] getVariance() {
    double[] variance = new double[dim];
    for (int i = 0; i < dim; ++i) {
      variance[i] = sumsq[i] / n - Math.pow(sum[i] / n, 2);
    }

    return variance;
  }
  
  public ClusterSummary nMerge(ClusterSummary other) {
    int mergeN = n + other.n;
    double[] mergeSum = new double[dim];
    double[] mergeSumsq = new double[dim];
    
    for (int i = 0; i < dim; ++i) {
      mergeSum[i] = sum[i] + other.sum[i];
      mergeSumsq[i] = sumsq[i] + other.sumsq[i];
    }
    
    return new ClusterSummary(mergeN, mergeSum, mergeSumsq);
  }
  
}
