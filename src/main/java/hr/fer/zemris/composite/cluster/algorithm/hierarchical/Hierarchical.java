package hr.fer.zemris.composite.cluster.algorithm.hierarchical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import hr.fer.zemris.composite.cluster.ICluster;
import hr.fer.zemris.composite.cluster.algorithm.IAlgorithm;
import hr.fer.zemris.composite.cluster.clusterable.IClusterable;
import hr.fer.zemris.composite.cluster.distance.DistanceType;
import hr.fer.zemris.composite.cluster.distance.IDistanceMeasure;
import hr.fer.zemris.composite.cluster.quality.IQualityMeasure;
import hr.fer.zemris.composite.cluster.quality.QualityType;

public class Hierarchical implements IAlgorithm {

  private int k;

  private IDistanceMeasure distanceMeasure;

  private IQualityMeasure qualityMeasure;

  public Hierarchical(DistanceType distanceType, QualityType qualityType) {
    super();
    setDistanceType(distanceType);
    setQualityType(qualityType);
  }

  @Override
  public List<ICluster> cluster(List<IClusterable> vectors, int k) {
    this.k = k;

    double quality = Double.MAX_VALUE;
    List<ICluster> result = null;
    for (int i = k; i <= k; i++) {
      List<ICluster> tmp = calculateClusters(vectors, i);
      double resultQuality = qualityMeasure.measure(tmp);
      System.out.println("k = " + i + ", quality = " + resultQuality);
      if (resultQuality < quality) {
        quality = resultQuality;
        result = tmp;
      }
    }
    return result;
  }

  private List<ICluster> calculateClusters(List<IClusterable> vectors, int k) {
    Queue<Distance> distancePairs = new PriorityQueue<>();
    Map<Cluster, Boolean> visited = new HashMap<>();
    List<Cluster> clusters = new ArrayList<>();

    int n = vectors.size();

    initClusters(clusters, vectors);
    initDistances(distancePairs, clusters);

    for (int i = 0; i < n - k; i++) {
      newIteration(distancePairs, visited, clusters);
    }

    return new ArrayList<ICluster>(clusters);
  }

  private void newIteration(Queue<Distance> distancePairs, Map<Cluster, Boolean> visited,
      List<Cluster> clusters) {
    Distance distance = null;
    while (true) {
      distance = distancePairs.poll();
      if (visited.get(distance.getFirst()) == null && visited.get(distance.getSecond()) == null) {
        break;
      }
    }

    Cluster first = distance.getFirst();
    Cluster second = distance.getSecond();
    visited.put(first, true);
    visited.put(second, true);
    Cluster newCluster = new Cluster(first, second);

    clusters.remove(first);
    clusters.remove(second);

    for (Cluster cluster : clusters) {
      addDistance(distancePairs, cluster, newCluster);
    }

    clusters.add(newCluster);
  }

  private void initClusters(List<Cluster> clusters, List<IClusterable> vectors) {
    for (IClusterable vector : vectors) {
      Cluster cluster = new Cluster(vector);
      clusters.add(cluster);
    }
  }

  private void initDistances(Queue<Distance> distancePairs, List<Cluster> clusters) {
    int n = clusters.size();
    for (int i = 0; i < n - 1; i++) {
      for (int j = i + 1; j < n; j++) {
        Cluster first = clusters.get(i);
        Cluster second = clusters.get(j);
        addDistance(distancePairs, first, second);
      }
    }
  }

  private void addDistance(Queue<Distance> distancePairs, Cluster first, Cluster second) {
    double distanceValue = distanceMeasure.measure(first.getCentroid(), second.getCentroid());
    Distance distance = new Distance(first, second, distanceValue);
    distancePairs.add(distance);
  }

  @Override
  public void setDistanceType(DistanceType distanceType) {
    distanceMeasure = distanceType.getDistanceMeasure();
  }

  @Override
  public void setQualityType(QualityType qualityType) {
    qualityMeasure = qualityType.getQualityMeasure();
  }

}
