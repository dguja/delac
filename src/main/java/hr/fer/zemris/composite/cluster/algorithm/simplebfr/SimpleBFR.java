package hr.fer.zemris.composite.cluster.algorithm.simplebfr;

import hr.fer.zemris.composite.cluster.ICluster;
import hr.fer.zemris.composite.cluster.algorithm.IAlgorithm;
import hr.fer.zemris.composite.cluster.clusterable.IClusterable;
import hr.fer.zemris.composite.cluster.distance.DistanceType;
import hr.fer.zemris.composite.cluster.distance.IDistanceMeasure;
import hr.fer.zemris.composite.cluster.quality.IQualityMeasure;
import hr.fer.zemris.composite.cluster.quality.QualityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleBFR implements IAlgorithm {

  private static final int K = 10;

  private static final int MAX_ITERATION = 100;

  private static final double BUCKET_COEFF = 0.1;

  private List<ICluster> clusters = new ArrayList<>();
  
  private IDistanceMeasure distanceMeasure;

  private IQualityMeasure qualityMeasure;

  public SimpleBFR(IDistanceMeasure distanceMeasure, IQualityMeasure qualityMeasure) {
    super();
    this.distanceMeasure = distanceMeasure;
    this.qualityMeasure = qualityMeasure;
  }

  @Override
  public void setDistanceType(DistanceType distanceType) {
    distanceMeasure = distanceType.getDistanceMeasure();
  }

  @Override
  public void setQualityType(QualityType qualityType) {
    qualityMeasure = qualityType.getQualityMeasure();
  }

  @Override
  public List<ICluster> cluster(List<IClusterable> points) {
    clusters.clear();
    int clusterSize = (int) (points.size() * BUCKET_COEFF);

    for (int i = 0; i < points.size(); i += clusterSize) {
      clusterChunk(points.subList(i, Math.min(points.size(), i + clusterSize)));
    }

    // pretvori listu Clustera u listu IClustera
    return clusters;
  }

  private void clusterChunk(List<IClusterable> points) {
    List<ClusterSummary> centroids = new ArrayList<>();
    List<ClusterSummary> clusterSummaries = new ArrayList<>();
    Map<Integer, Integer> summaryToCentroid = new HashMap<>();
    
    // odredi pocetne centroide
    //calculateCentroids();
    
    for (int iter = 0; iter < MAX_ITERATION; ++iter) {
      List<ClusterSummary> newCentroids = new ArrayList<>(centroids.size());
      
      // za svaku tocku, nadji najblizi centroid
      for (int index = 0; index < clusterSummaries.size(); ++index) {
        ClusterSummary clusterSummary = clusterSummaries.get(index);
        Integer closestCentroidIndex = findClosestCentroidIndex(clusterSummary.getCentroid(), centroids);
        
        summaryToCentroid.put(index, closestCentroidIndex);
        newCentroids.get(closestCentroidIndex).addClusterSummary(clusterSummary);
      }

      // ako se nista nije promijenilo, prekini
      if (clusters.equals(oldClusters)) {
        break;
      }
      oldClusters = clusters;

      // izracunaj nove centroide i ako ih fali dodaj nove
      updateClusters(clusters);
    }
    
    List<Cluster> newClusters = new ArrayList<>();
    for ()
  }

  private Integer findClosestCentroidIndex(IClusterable point, List<ClusterSummary> clusterSummaries) {
    double distance = Double.MAX_VALUE;
    Integer closestCentroidIndex = null;

    for (int index = 0; index < clusterSummaries.size(); ++index) {
      double nDistance = distanceMeasure.measure(point, clusterSummaries.get(index).getCentroid());

      if (nDistance < distance) {
        distance = nDistance;
        closestCentroidIndex = index;
      }
    }

    return closestCentroidIndex;
  }
  
  private void initializeClusters(List<Cluster> clusters) {
  }

  private void updateClusters(List<Cluster> clusters) {
    for (Cluster cluster : clusters) {
      cluster.calculateCentroid();
      cluster.clearPointSummaries();
    }
  }
  
}
