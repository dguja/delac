package hr.fer.zemris.composite.cluster.algorithm.bfr;

import hr.fer.zemris.composite.cluster.ICluster;
import hr.fer.zemris.composite.cluster.algorithm.IAlgorithm;
import hr.fer.zemris.composite.cluster.clusterable.IClusterable;
import hr.fer.zemris.composite.cluster.distance.DistanceType;
import hr.fer.zemris.composite.cluster.distance.IDistanceMeasure;
import hr.fer.zemris.composite.cluster.quality.IQualityMeasure;
import hr.fer.zemris.composite.cluster.quality.QualityType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class BFR implements IAlgorithm {

  private static final int CLUSTER_NUM = 4;// 10;

  private static final int MAX_ITERATION = 100;

  private static final double BUCKET_FRACTION = 0.5; // 0.1

  private static final double DISCARDED_FRACTION = 0.8;// 0.2;

  private static final int SECONDARY_CLUSTER_NUM = 20;// 20;

  private static final int SECONDARY_MAX_ITERATION = 200;

  private static final double COMPRESSED_MAX_DEV = 1.3;

  private IDistanceMeasure distanceMeasure;

  private IQualityMeasure qualityMeasure;

  public BFR(DistanceType distanceType, QualityType qualityType) {
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
  public List<ICluster> cluster(List<IClusterable> points) {
    int numPoints = points.size();
    int clusterSize = (int) (numPoints * BUCKET_FRACTION);

    List<Cluster> clusters = new ArrayList<>();
    List<Cluster> bucketClusters = new ArrayList<>();

    int pos = 0;
    while (pos < numPoints) {
      while (pos < numPoints && bucketClusters.size() < clusterSize) {
        bucketClusters.add(new Cluster(points.get(pos)));
        ++pos;
      }

      clusters = clusterBucket(clusters, bucketClusters);
    }
    
    // preostale klastere iz bucketa pridruži postojećim klasterima
    clusters.addAll(bucketClusters);
    Map<ClusterSummary, List<Cluster>> centroidToClusterList = cluster(clusters, CLUSTER_NUM, MAX_ITERATION);
    
    clusters.clear();
    for (Entry<ClusterSummary, List<Cluster>> entry : centroidToClusterList.entrySet()) {
      Cluster resultCluster = null;
      for (Cluster cluster : entry.getValue()) {
        resultCluster = Cluster.merge(resultCluster, cluster);
      }
      
      clusters.add(resultCluster);
    }
    
    // pretvori u listu IClustera
    List<ICluster> iClusters = new ArrayList<>();
    iClusters.addAll(clusters);
    return iClusters;
  }

  private List<Cluster> clusterBucket(List<Cluster> clusters, List<Cluster> bucketClusters) {
    Map<ClusterSummary, List<Cluster>> centroidToClusterList;

    // napravi pocetno klasteriranje
    clusters.addAll(bucketClusters);
    centroidToClusterList = cluster(clusters, CLUSTER_NUM, MAX_ITERATION);

    List<Cluster> discardClusters = new ArrayList<>();
    List<Cluster> leftClusters = new ArrayList<>();

    // primary compression - sortiraj po Mahalanobis udaljenosti i odbaci najblize
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

      // sortiraj
      Collections.sort(clusterList, new Comparator<Cluster>() {

        @Override
        public int compare(Cluster o1, Cluster o2) {
          return Double.compare(distanceMap.get(o1), distanceMap.get(o2));
        }

      });

      int listSize = clusterList.size();
      if (listSize == 0) {
        continue;
      }

      // izvuci najbolje, spoji ih i odbaci, ostale spoji u jednu hrpu
      int numDiscarded = Math.max(1, (int) (listSize * DISCARDED_FRACTION));
      Cluster discardCluster = null;
      for (int i = 0; i < listSize; ++i) {
        Cluster cluster = clusterList.get(i);
        if (i < numDiscarded) {
          discardCluster = Cluster.merge(discardCluster, cluster);
        } else {
          leftClusters.add(cluster);
        }
      }

      discardClusters.add(discardCluster);
    }
    
    /*
    int sum = 0;
    for (Cluster cluster : discardClusters) {
      sum += cluster.getPoints().size();
    }
    for (Cluster cluster : leftClusters) {
      sum += cluster.getPoints().size();
    }
    System.out.println("SUMA: " + sum);
    */

    // secondary compression phase
    bucketClusters.clear();

    // klasteriraj po vecem broju klastera i iteracija
    centroidToClusterList = cluster(leftClusters, SECONDARY_CLUSTER_NUM, SECONDARY_MAX_ITERATION);

    List<Cluster> secondaryClusters = new ArrayList<>();
    for (Entry<ClusterSummary, List<Cluster>> entry : centroidToClusterList.entrySet()) {
      List<Cluster> clusterList = entry.getValue();

      int listSize = clusterList.size();
      if (listSize == 0) {
        continue;
      }

      // izracunaj varijancu svakog klastera
      ClusterSummary secondaryClusterSummary = clusterList.get(0).getClusterSummary().copy();
      for (int i = 1; i < listSize; ++i) {
        secondaryClusterSummary.addClusterSummary(clusterList.get(i).getClusterSummary());
      }

      if (secondaryClusterSummary.isDeviationLessThan(COMPRESSED_MAX_DEV)) {
        // spoji listu klastera u jedan klaster
        Cluster secondaryCluster = null;
        for (int i = 0; i < listSize; ++i) {
          secondaryCluster = Cluster.merge(secondaryCluster, clusterList.get(i));
        }

        secondaryClusters.add(secondaryCluster);
      } else {
        // labave klastere rastaviti i sve dijelove vrati u bucket
        for (Cluster cluster : clusterList) {
          bucketClusters.add(cluster);
        }
      }
    }

    // napravi hijerarhijsko klasteriranje
    List<Boolean> removed = new ArrayList<>();
    for (int i = 0; i < secondaryClusters.size(); ++i) {
      removed.add(Boolean.FALSE);
    }

    for (int i = 0; i < secondaryClusters.size(); ++i) {
      if (removed.get(i)) {
        continue;
      }

      for (int j = i + 1; j < secondaryClusters.size(); ++j) {
        if (removed.get(j)) {
          continue;
        }

        Cluster cluster1 = secondaryClusters.get(i);
        Cluster cluster2 = secondaryClusters.get(j);

        ClusterSummary clusterSummary = cluster1.getClusterSummary().copy();
        clusterSummary.addClusterSummary(cluster2.getClusterSummary());

        if (clusterSummary.isDeviationLessThan(COMPRESSED_MAX_DEV)) {
          Cluster merged = Cluster.merge(cluster1, cluster2);

          if (merged == cluster2) {
            secondaryClusters.set(i, merged);
          }
          removed.set(j, Boolean.TRUE);
        }
      }
    }

    // popuni bucket s compressed clusterima
    for (int i = 0; i < secondaryClusters.size(); ++i) {
      if (removed.get(i)) {
        continue;
      }

      bucketClusters.add(secondaryClusters.get(i));
    }

    return discardClusters;
  }

  private Map<ClusterSummary, List<Cluster>> cluster(List<Cluster> clusters, int k, int maxIter) {
    List<IClusterable> centroids = new ArrayList<>();
    fillCentroids(centroids, clusters, k);

    Map<Integer, List<Cluster>> centroidToClusterList = new HashMap<>();
    Map<ClusterSummary, List<Cluster>> summaryToClusterList = new HashMap<>();
    for (int iter = 0; iter < maxIter; ++iter) {
      ClusterSummary[] newCentroids = new ClusterSummary[centroids.size()];
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
        // novi dio

        ClusterSummary newCentroid = newCentroids[closestCentroidIndex];
        if (newCentroid == null) {
          newCentroid = clusterSummary.copy();
        } else {
          newCentroid.addClusterSummary(clusterSummary);
        }
        newCentroids[closestCentroidIndex] = newCentroid;
      }

      // popuni summary
      summaryToClusterList.clear();
      for (Entry<Integer, List<Cluster>> entry : centroidToClusterList.entrySet()) {
        summaryToClusterList.put(newCentroids[entry.getKey()], entry.getValue());
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

    for (int index = 0, size = centroids.size(); index < size; ++index) {
      double nDistance = distanceMeasure.measure(point, centroids.get(index));

      if (nDistance < distance) {
        distance = nDistance;
        closestCentroidIndex = index;
      }
    }

    return closestCentroidIndex;
  }

  private void fillClusterSummaryCentroids(List<IClusterable> centroids,
      ClusterSummary[] clusterSummaries) {
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

      double maxDistance = -Double.MAX_VALUE;
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
