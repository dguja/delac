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

  private static final int MAX_ITERATION = 300;

  private static final double BUCKET_FRACTION = 1;

  private int clusterNum;
  
  private IDistanceMeasure distanceMeasure;

  private IQualityMeasure qualityMeasure;

  public SimpleBFR(DistanceType distanceType, QualityType qualityType) {
    super();
    setDistanceType(distanceType);
    setQualityType(qualityType);
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
  public List<ICluster> cluster(List<IClusterable> points, int k) {
    clusterNum = k;
    
    int numPoints = points.size();
    int clusterSize = (int) (points.size() * BUCKET_FRACTION);

    List<Cluster> clusters = new ArrayList<>();
    List<IClusterable> bucketPoints = new ArrayList<>();

    int pos = 0;
    while (pos < numPoints) {
      while (pos < numPoints && bucketPoints.size() < clusterSize) {
        bucketPoints.add(points.get(pos));
        ++pos;
      }

      clusters = clusterBucket(clusters, bucketPoints);
      bucketPoints.clear();
    }

    // pretvori u listu IClustera
    return new ArrayList<ICluster>(clusters);
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
      ClusterSummary[] newCentroids = new ClusterSummary[centroids.size()];

      // za svaki klaster odredi najblizi centroid
      for (Cluster cluster : clusters) {
        ClusterSummary clusterSummary = cluster.getClusterSummary();

        Integer closestCentroidIndex =
            findClosestCentroidIndex(clusterSummary.getCentroid(), centroids);

        clusterToCentroid.put(cluster, closestCentroidIndex);

        ClusterSummary newCentroid = newCentroids[closestCentroidIndex];
        if (newCentroid == null) {
          newCentroid = clusterSummary.copy();
        } else {
          newCentroid.addClusterSummary(clusterSummary);
        }
        newCentroids[closestCentroidIndex] = newCentroid;
      }

      // zapamti stare centroide
      List<IClusterable> oldCentroids = new ArrayList<>(centroids);

      // postavi nove centroide, dodaj nove ako ih fali
      centroids.clear();
      fillClusterSummaryCentroids(centroids, newCentroids);
      fillCentroids(centroids, points);

      // ako se nije nista promijenilo, prekini
      if (oldCentroids.equals(centroids)) {
        break;
      }
    }

    /*
     * System.out.println("KONACNI CENTROIDI"); for (IClusterable centroid : centroids) {
     * System.out.println(centroid); } System.out.println();
     */

    // napravi nove klastere
    Cluster[] newClusters = new Cluster[centroids.size()];
    for (Entry<Cluster, Integer> entry : clusterToCentroid.entrySet()) {
      Cluster cluster = newClusters[entry.getValue()];
      if (cluster == null) {
        cluster = entry.getKey();
      } else {
        cluster.addSubcluster(entry.getKey());
      }
      newClusters[entry.getValue()] = cluster;
    }

    List<Cluster> listClusters = new ArrayList<>();
    for (Cluster cluster : newClusters) {
      if (cluster != null) {
        listClusters.add(cluster);
      }
    }

    return listClusters;
  }

  private Integer findClosestCentroidIndex(IClusterable point, List<IClusterable> centroids) {
    double distance = Double.MAX_VALUE;
    Integer closestCentroidIndex = null;

    for (int index = 0; index < centroids.size(); ++index) {
      // if (centroids.get(index) == null) {
      // continue;
      // }

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
      ClusterSummary[] clusterSummaries) {
    for (ClusterSummary clusterSummary : clusterSummaries) {
      if (clusterSummary != null) {
        centroids.add(clusterSummary.getCentroid());
      }
    }
  }

  private void fillCentroids(List<IClusterable> centroids, List<IClusterable> points) {
    int fillCount = Math.min(clusterNum - centroids.size(), points.size());
    for (int i = 0; i < fillCount; ++i) {
      Map<IClusterable, Double> pointDistance = new HashMap<>();

//      System.out.println("EVO ME OVDJE....");
      // za svaku tocku odredi minimalnu udaljenost do centroida
      for (IClusterable point : points) {
        double minDistance = Double.MAX_VALUE;
        for (IClusterable centroid : centroids) {
          minDistance = Math.min(minDistance, distanceMeasure.measure(point, centroid));
        }
//        System.out.println(minDistance);
        pointDistance.put(point, minDistance);
      }

      // odredi tocku s maksimalnom udaljenoscu
      IClusterable centroid = null;

      // System.out.println();
      double maxDistance = -Double.MAX_VALUE;
      for (Entry<IClusterable, Double> entry : pointDistance.entrySet()) {
        // System.out.println("EVO ME " + entry.getValue() + " " + maxDistance + " " +
        // entry.getKey() + " " + Double.MIN_VALUE + " " + Double.MAX_VALUE);
        if (entry.getValue() > maxDistance) {
          centroid = entry.getKey();
          maxDistance = entry.getValue();
        }
      }

//      if (centroid == null) {
//        System.out.println("PAZI!! " + fillCount + " " + points.size() + " " + maxDistance + " "
//            + centroids.size());
//      }
      centroids.add(centroid);
    }
  }

}
