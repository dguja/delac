package hr.fer.zemris.composite.cluster.algorithm.bfr;

import hr.fer.zemris.composite.cluster.ICluster;
import hr.fer.zemris.composite.cluster.algorithm.IAlgorithm;
import hr.fer.zemris.composite.cluster.algorithm.simplebfr.Cluster;
import hr.fer.zemris.composite.cluster.clusterable.IClusterable;
import hr.fer.zemris.composite.cluster.distance.DistanceType;
import hr.fer.zemris.composite.cluster.distance.IDistanceMeasure;
import hr.fer.zemris.composite.cluster.quality.IQualityMeasure;
import hr.fer.zemris.composite.cluster.quality.QualityType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class BFR implements IAlgorithm {

  private static final int CLUSTER_NUM = 10;

  private static final int MAX_ITERATION = 100;

  private static final double BUCKET_FRACTION = 0.1;

  private static final double DISCARDED_FRACTION = 0.2;

  private static final int SECONDARY_CLUSTER_NUM = 20;

  private static final int SECONDARY_MAX_ITERATION = 200;

  private static final double COMPRESSED_MAX_DEV = 1.3;

  private IDistanceMeasure distanceMeasure;

  private IQualityMeasure qualityMeasure;

  public BFR(IDistanceMeasure distanceMeasure, IQualityMeasure qualityMeasure) {
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
    int clusterSize = (int) (points.size() * BUCKET_FRACTION);

    List<Cluster> clusters = new ArrayList<>();
    List<Cluster> bucketClusters = new ArrayList<>();

    int pos = 0;
    while (pos < points.size()) {
      while (pos < points.size() && bucketClusters.size() < clusterSize) {
        bucketClusters.add(new Cluster(points.get(pos)));
        ++pos;
      }

      clusters = clusterBucket(clusters, bucketClusters);
    }

    // pretvori u listu IClustera
    List<ICluster> iClusters = new ArrayList<>();
    iClusters.addAll(clusters);
    return iClusters;
  }

  private List<Cluster> clusterBucket(List<Cluster> clusters, List<Cluster> bucketClusters) {
    // odredi centroide za klasteriranje
    List<IClusterable> centroids = new ArrayList<>();
    fillClusterCentroids(centroids, clusters);
    fillCentroids(centroids, bucketClusters, CLUSTER_NUM);

    Map<ClusterSummary, List<Cluster>> centroidToClusterList =
        cluster(centroids, clusters, CLUSTER_NUM, MAX_ITERATION);

    // sortiraj po Mahalanobis udaljenosti
    for (Entry<ClusterSummary, List<Cluster>> entry : centroidToClusterList.entrySet()) {
      ClusterSummary clusterSummary = entry.getKey();
      List<Cluster> clusterList = entry.getValue();
      Map<Cluster, Double> distanceMap = new HashMap<>();

      IClusterable clusterCentroid = clusterSummary.getCentroid();
      IClusterable clusterVariance = clusterSummary.getVariance();

      // izracunaj Mahalanobis udaljenost na kvadrat
      for (Cluster cluster : clusterList) {
        double distance = 0;
        IClusterable point = cluster.getClusterSummary().getCentroid();

        for (int i = 0; i < clusterCentroid.getDimension(); ++i) {
          distance += Math.pow((clusterCentroid.get(i) - point.get(i)), 2) / clusterVariance.get(i);
        }

        distanceMap.put(cluster, distance);
      }

      Collections.sort(clusterList, new Comparator<Cluster>() {

        @Override
        public int compare(Cluster o1, Cluster o2) {
          return Double.compare(distanceMap.get(o1), distanceMap.get(o2));
        }

      });
    }

  }

  private Map<ClusterSummary, List<Cluster>> cluster(List<IClusterable> centroids,
      List<Cluster> clusters, int k, int maxIter) {
    // Map<Cluster, Integer> clusterToCentroid = new HashMap<>();

    Map<Integer, List<Cluster>> centroidToClusterList = new HashMap<>();
    Map<ClusterSummary, List<Cluster>> summaryToClusterList = new HashMap<>();
    for (int iter = 0; iter < maxIter; ++iter) {
      List<ClusterSummary> newCentroids = new ArrayList<>(centroids.size());
      centroidToClusterList.clear();

      // za svaki klaster odredi najblizi centroid
      for (Cluster cluster : clusters) {
        ClusterSummary clusterSummary = cluster.getClusterSummary();

        Integer closestCentroidIndex =
            findClosestCentroidIndex(clusterSummary.getCentroid(), centroids);

        // novi dio
        List<Cluster> clusterList = centroidToClusterList.get(closestCentroidIndex);
        if (clusterList == null) {
          clusterList = new ArrayList<>();
        }
        clusterList.add(cluster);
        centroidToClusterList.put(closestCentroidIndex, clusterList);
        // clusterToCentroid.put(cluster, closestCentroidIndex);

        ClusterSummary newCentroid = newCentroids.get(closestCentroidIndex);
        if (newCentroid == null) {
          newCentroid = clusterSummary.copy();
        } else {
          newCentroid.addClusterSummary(clusterSummary);
        }
        newCentroids.set(closestCentroidIndex, newCentroid);
      }

      // popuni summary
      summaryToClusterList.clear();
      for (Entry<Integer, List<Cluster>> entry : centroidToClusterList.entrySet()) {
        summaryToClusterList.put(newCentroids.get(entry.getKey()), entry.getValue());
      }

      // zapamti stare centroide
      List<IClusterable> oldCentroids = new ArrayList<>(centroids);

      // postavi nove centroide, dodaj nove ako ih fali
      centroids.clear();
      fillClusterSummaryCentroids(centroids, newCentroids);
      fillCentroids(centroids, clusters, k);

      // ako se nije nista promijenilo, prekini
      if (oldCentroids.equals(centroids)) {
        break;
      }
    }

    return summaryToClusterList;
  }

  private Integer findClosestCentroidIndex(IClusterable point, List<IClusterable> centroids) {
    double distance = Double.MAX_VALUE;
    Integer closestCentroidIndex = null;

    for (int index = 0; index < centroids.size(); ++index) {
      double nDistance = distanceMeasure.measure(point, centroids.get(index));

      if (nDistance < distance) {
        distance = nDistance;
        closestCentroidIndex = index;
      }
    }

    return closestCentroidIndex;
  }

  private void fillClusters(List<Cluster> clusters, List<IClusterable> points) {
    for (IClusterable point : points) {
      clusters.add(new Cluster(point));
    }
  }

  private void fillClusterCentroids(List<IClusterable> centroids, List<Cluster> clusters) {
    for (Cluster cluster : clusters) {
      centroids.add(cluster.getClusterSummary().getCentroid());
    }
  }

  private void fillClusterSummaryCentroids(List<IClusterable> centroids,
      List<ClusterSummary> clusterSummaries) {
    for (ClusterSummary clusterSummary : clusterSummaries) {
      if (clusterSummary != null) {
        centroids.add(clusterSummary.getCentroid());
      }
    }
  }

  private void
      fillCentroids(List<IClusterable> centroids, List<Cluster> clusters, int maxCentroids) {
    int fillCount = Math.min(maxCentroids - centroids.size(), clusters.size());
    for (int i = 0; i < fillCount; ++i) {
      Map<IClusterable, Double> pointDistance = new HashMap<>();

      // za svaku tocku odredi minimalnu udaljenost do centroida
      for (Cluster cluster : clusters) {
        double minDistance = Double.MAX_VALUE;
        IClusterable point = cluster.getClusterSummary().getCentroid();
        for (IClusterable centroid : centroids) {
          minDistance = Math.min(minDistance, distanceMeasure.measure(point, centroid));
        }
        pointDistance.put(point, minDistance);
      }

      // odredi tocku s maksimalnom udaljenoscu
      IClusterable centroid = null;

      double maxDistance = Double.MIN_VALUE;
      for (Entry<IClusterable, Double> entry : pointDistance.entrySet()) {
        if (entry.getValue() > maxDistance) {
          centroid = entry.getKey();
          maxDistance = entry.getValue();
        }
      }

      centroids.add(centroid);
    }
  }

}
