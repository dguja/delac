package hr.fer.zemris.composite.cluster.algorithm.kmeans;

import hr.fer.zemris.composite.cluster.clusterable.IClusterable;
import hr.fer.zemris.composite.cluster.clusterable.Vector;
import hr.fer.zemris.composite.cluster.distance.IDistanceMeasure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

public class KMeans {

  private List<IClusterable> clusterable;

  private IDistanceMeasure distanceMeasure;

  public KMeans(List<IClusterable> clusterable, IDistanceMeasure distanceMeasure) {
    this.clusterable = clusterable;
    this.distanceMeasure = distanceMeasure;
  }

  public List<Cluster> run(int k, int maxIter) {

    List<IClusterable> centroids = selectInitCentroids(k);

    List<Cluster> clusters = createClusters(centroids);
    List<Cluster> oldClusters = null;

    for (int i = 0; i < maxIter; i++) {

      for (IClusterable point : clusterable) {
        Cluster tmpCluster = null;
        double distance = Double.MAX_VALUE;

        for (Cluster cluster : clusters) {
          double tmpDistance = distanceMeasure.measure(cluster.getCentroid(), point);
          if (tmpDistance < distance) {
            distance = tmpDistance;
            tmpCluster = cluster;
          }
        }

        tmpCluster.getClusterable().add(point);

      }

      if (clusters.equals(oldClusters)) {
        break;
      }
      
      oldClusters = clusters;
      clusters = calculateNewCentroids(clusters);

    }

    return clusters;
  }

  private List<IClusterable> selectInitCentroids(int k) {
    List<IClusterable> centroids = new ArrayList<>();

    // odaberi slucajno prvi centroid
    Random rand = new Random();
    int randIndex = rand.nextInt(clusterable.size());
    IClusterable first = clusterable.get(randIndex);
    centroids.add(first);
    
    Set<IClusterable> tmpClusterable = new HashSet<>(clusterable);
    tmpClusterable.remove(first);
    
    for (int i = 1; i <= k; i++) {

      Map<IClusterable, Double> clusterableDistance = new HashMap<>();

      // izracunaj za svaku tocku minimalnu udaljenost do drugih centroida
      for (IClusterable point : tmpClusterable) {

        double distMin = Double.MAX_VALUE;

        for (IClusterable centroid : centroids) {
          distMin = Math.min(distMin, distanceMeasure.measure(point, centroid));
        }

        // pohrani minimalnu udaljenost tocke do centroida
        clusterableDistance.put(point, distMin);

      }

      // odaberi onu tocku za novi centroid koja ima maksimalnu minimalnu udaljenost do drugih
      // centroida
      double maxDist = Double.MIN_VALUE;
      IClusterable candidate = null;
      for (Entry<IClusterable, Double> entry : clusterableDistance.entrySet()) {
        if (entry.getValue() > maxDist) {
          candidate = entry.getKey();
          maxDist = entry.getValue();
        }
      }
      centroids.add(candidate.copy());
    }

    return centroids;
  }

  /**
   * Stvara klastere. Klasterima su postavljeni centroidi, ali ne i vektori koji pripadaju klasteru.
   * 
   * @param centroids klastera
   * @return lista klastera
   */
  private List<Cluster> createClusters(List<IClusterable> centroids) {
    List<Cluster> clusters = new ArrayList<>();

    for (IClusterable centroid : centroids) {
      clusters.add(new Cluster(new HashSet<IClusterable>(), centroid));
    }

    return clusters;
  }

  /**
   * Vraca listu novu klastera kojima je postavljen jedino centroid.
   * 
   * @param clusters na temelju kojih se racunaju novi centroidi
   * @return lista novih klastera s postavljenim centroidima
   */
  private List<Cluster> calculateNewCentroids(List<Cluster> clusters) {
    List<Cluster> newClusters = new ArrayList<>();

    for (Cluster cluster : clusters) {

      double[] avg = calculateAvg(cluster.getClusterable(), cluster.getCentroid().getDimension());
      newClusters.add(new Cluster(new HashSet<IClusterable>(), new Vector(avg)));

    }

    return newClusters;
  }

  private double[] calculateAvg(Set<IClusterable> clusterable, int dimension) {
    double[] avg = new double[dimension];

    for (IClusterable point : clusterable) {
      for (int i = avg.length - 1; i >= 0; --i) {
        avg[i] += point.getComponent(i);
      }
    }

    int size = clusterable.size();
    for (int i = avg.length - 1; i >= 0; --i) {
      avg[i] /= (double) size;
    }

    return avg;
  }

}
