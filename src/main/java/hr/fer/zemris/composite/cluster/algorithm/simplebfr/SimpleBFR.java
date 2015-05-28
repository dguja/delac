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
import java.util.Map.Entry;

public class SimpleBFR implements IAlgorithm {

  private static final int CLUSTER_NUM = 10;

  private static final int MAX_ITERATION = 100;

  private static final double BUCKET_COEFF = 0.1;

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
    int clusterSize = (int) (points.size() * BUCKET_COEFF);

    List<Cluster> clusters = new ArrayList<>();
    for (int i = 0; i < points.size(); i += clusterSize) {
      clusters =
          clusterBucket(clusters, points.subList(i, Math.min(points.size(), i + clusterSize)));
    }

    // pretvori u listu IClustera
    List<ICluster> iClusters = new ArrayList<>();
    iClusters.addAll(clusters);
    return iClusters;
  }

  private List<Cluster> clusterBucket(List<Cluster> clusters, List<IClusterable> points) {
    // odredi centroide za klasteriranje
    List<IClusterable> centroids = new ArrayList<>();
    fillClusterCentroids(centroids, clusters);
    fillCentroids(centroids, points);

    // dodaj nove klastere
    fillClusters(clusters, points);

    Map<Cluster, Integer> clusterToCentroid = new HashMap<>();
    for (int iter = 0; iter < MAX_ITERATION; ++iter) {
      List<ClusterSummary> newCentroids = new ArrayList<>(centroids.size());

      // za svaki klaster odredi najblizi centroid
      for (Cluster cluster : clusters) {
        ClusterSummary clusterSummary = cluster.getClusterSummary();

        Integer closestCentroidIndex =
            findClosestCentroidIndex(clusterSummary.getCentroid(), centroids);

        clusterToCentroid.put(cluster, closestCentroidIndex);

        ClusterSummary newCentroid = newCentroids.get(closestCentroidIndex);
        if (newCentroid == null) {
          newCentroid = clusterSummary.copy();
        } else {
          newCentroid.addClusterSummary(clusterSummary);
        }
        newCentroids.set(closestCentroidIndex, newCentroid);
      }

      // zapamti stare centroide
      List<IClusterable> oldCentroids = centroids;

      // postavi nove centroide, dodaj nove ako ih fali
      centroids.clear();
      fillClusterSummaryCentroids(centroids, newCentroids);
      fillCentroids(centroids, points);
      
      // ako se nije nista promijenilo, prekini
      if (oldCentroids.equals(centroids)) {
        break;
      }
    }

    // napravi nove klastere
    List<Cluster> newClusters = new ArrayList<>();
    for (Entry<Cluster, Integer> entry : clusterToCentroid.entrySet()) {
      Cluster cluster = newClusters.get(entry.getValue());
      if (cluster == null) {
        cluster = entry.getKey();
      } else {
        cluster.addSubcluster(entry.getKey());
      }
      newClusters.set(entry.getValue(), cluster);
    }
    return newClusters;
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

  private void fillCentroids(List<IClusterable> centroids, List<IClusterable> points) {
    int fillCount = Math.min(CLUSTER_NUM - centroids.size(), points.size());
    for (int i = 0; i < fillCount; ++i) {
      Map<IClusterable, Double> pointDistance = new HashMap<>();

      // za svaku tocku odredi minimalnu udaljenost do centroida
      for (IClusterable point : points) {
        double minDistance = Double.MAX_VALUE;
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
