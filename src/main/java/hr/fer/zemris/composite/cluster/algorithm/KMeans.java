package hr.fer.zemris.composite.cluster.algorithm;

import hr.fer.zemris.composite.cluster.KMeansCluster;
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

  public List<KMeansCluster> run(int k, int maxIter) {

    List<IClusterable> centroids = selectInitCentroids(k);

    List<KMeansCluster> kMeansClusters = createClusters(centroids);
    List<KMeansCluster> oldClusters = null;

    for (int i = 0; i < maxIter; i++) {

      for (IClusterable point : clusterable) {
        KMeansCluster tmpCluster = null;
        double distance = Double.MAX_VALUE;

        for (KMeansCluster kMeansCluster : kMeansClusters) {
          double tmpDistance = distanceMeasure.measure(kMeansCluster.getCentroid(), point);
          if (tmpDistance < distance) {
            distance = tmpDistance;
            tmpCluster = kMeansCluster;
          }
        }

        tmpCluster.getClusterable().add(point);

      }

      if (kMeansClusters.equals(oldClusters)) {
        break;
      }
      
      oldClusters = kMeansClusters;
      kMeansClusters = calculateNewCentroids(kMeansClusters);

    }

    return kMeansClusters;
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
  private List<KMeansCluster> createClusters(List<IClusterable> centroids) {
    List<KMeansCluster> kMeansClusters = new ArrayList<>();

    for (IClusterable centroid : centroids) {
      kMeansClusters.add(new KMeansCluster(new HashSet<IClusterable>(), centroid));
    }

    return kMeansClusters;
  }

  /**
   * Vraca listu novu klastera kojima je postavljen jedino centroid.
   * 
   * @param kMeansClusters na temelju kojih se racunaju novi centroidi
   * @return lista novih klastera s postavljenim centroidima
   */
  private List<KMeansCluster> calculateNewCentroids(List<KMeansCluster> kMeansClusters) {
    List<KMeansCluster> newClusters = new ArrayList<>();

    for (KMeansCluster kMeansCluster : kMeansClusters) {

      double[] avg = calculateAvg(kMeansCluster.getClusterable(), kMeansCluster.getCentroid().getDimension());
      newClusters.add(new KMeansCluster(new HashSet<IClusterable>(), new Vector(avg)));

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
