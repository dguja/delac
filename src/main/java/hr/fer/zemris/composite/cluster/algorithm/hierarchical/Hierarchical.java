package hr.fer.zemris.composite.cluster.algorithm.hierarchical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import hr.fer.zemris.composite.cluster.ICluster;
import hr.fer.zemris.composite.cluster.algorithm.IAlgorithm;
import hr.fer.zemris.composite.cluster.clusterable.IClusterable;
import hr.fer.zemris.composite.cluster.distance.DistanceType;
import hr.fer.zemris.composite.cluster.distance.IDistanceMeasure;
import hr.fer.zemris.composite.cluster.quality.IQualityMeasure;
import hr.fer.zemris.composite.cluster.quality.QualityType;

public class Hierarchical implements IAlgorithm {

  private int k = 3;
  
  private IDistanceMeasure distanceMeasure;

  private IQualityMeasure qualityMeasure;

  public Hierarchical(IDistanceMeasure distanceMeasure, IQualityMeasure qualityMeasure) {
    super();
    this.distanceMeasure = distanceMeasure;
    this.qualityMeasure = qualityMeasure;
  }

  @Override
  public List<ICluster> cluster(List<IClusterable> vectors) {
    // TODO Algoritam
    TreeSet<Distance> distancePairs = new TreeSet<>();
    Map<Long, List<Distance>> distancePointer = new HashMap<>();
    List<Cluster> clusters = new ArrayList<>();

    int n = vectors.size();
    
    initClusters(clusters, vectors);
    initDistances(distancePairs, distancePointer, clusters);

    for (int i = 0; i < n - k; i++){
      Distance distance = distancePairs.first();
      Cluster first = distance.getFirst();
      Cluster second = distance.getSecond();
      Cluster newCluster = new Cluster(first, second);
      clusters.remove(first);
      clusters.remove(second);
      
      List<Distance> firstPairs = distancePointer.get(first.getId());
      List<Distance> secondPairs = distancePointer.get(second.getId());
      distancePairs.removeAll(firstPairs);
      distancePairs.removeAll(secondPairs);
      
      
      for (Cluster cluster : clusters){
        addDistance(distancePairs, distancePointer, cluster, newCluster);        
      }

      clusters.add(newCluster);      
    }
    
    return new ArrayList<ICluster>(clusters);
  }

  private void initClusters(List<Cluster> clusters, List<IClusterable> vectors) {
    for (IClusterable vector : vectors) {
      clusters.add(new Cluster(vector));
    }
  }

  private void initDistances(Set<Distance> distancePairs,
      Map<Long, List<Distance>> distancePointer, List<Cluster> clusters) {
    int n = clusters.size();
    for (int i = 0; i < n - 1; i++) {
      for (int j = i + 1; j < n; j++) {
        Cluster first = clusters.get(i);
        Cluster second = clusters.get(j);
        addDistance(distancePairs, distancePointer, first, second);
      }
    }
  }

  private void addDistance(Set<Distance> distancePairs, Map<Long, List<Distance>> distancePointer,
      Cluster first, Cluster second) {
    double distanceValue = distanceMeasure.measure(first.getCentroid(), second.getCentroid());
    Distance distance = new Distance(first, second, distanceValue);
    distancePairs.add(distance);
    addToMap(distancePointer, first, distance);
    addToMap(distancePointer, second, distance);
  }

  private void addToMap(Map<Long, List<Distance>> distancePointer, Cluster cluster,
      Distance distance) {
    List<Distance> distanceList = distancePointer.get(cluster.getId());
    if (distanceList == null) {
      distanceList = new ArrayList<>();
    }
    distanceList.add(distance);
    distancePointer.put(cluster.getId(), distanceList);
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
