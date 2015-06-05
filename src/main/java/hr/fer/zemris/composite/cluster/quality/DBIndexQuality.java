package hr.fer.zemris.composite.cluster.quality;

import hr.fer.zemris.composite.cluster.ICluster;
import hr.fer.zemris.composite.cluster.clusterable.IClusterable;
import hr.fer.zemris.composite.cluster.clusterable.Vector;
import hr.fer.zemris.composite.cluster.distance.EuclidDistance;

import java.util.ArrayList;
import java.util.List;

public class DBIndexQuality {

  public static double measure(final List<ICluster> clusters) {
    final List<IClusterable> centroids = new ArrayList<>();
    final List<Double> averageDistances = new ArrayList<>();
    
    for (final ICluster cluster : clusters) {
      final IClusterable centroid = calculateCentroid(cluster);
      
      double distanceSum = 0;
      for (final IClusterable clusterable : cluster.getPoints()) {
        distanceSum += EuclidDistance.measure(centroid, clusterable);
      }
      
      centroids.add(centroid);
      averageDistances.add(distanceSum / cluster.getN());
    }
    
    double sum = 0;
    for (int i = 0; i < clusters.size(); i++) {
      double current = 0;
      
      for (int j = 0; j < clusters.size(); j++) {
        if (j != i) {
          current =
              Math.max(
                  current,
                  (averageDistances.get(i) + averageDistances.get(j))
                      / EuclidDistance.measure(centroids.get(i), centroids.get(j)));
        }
      }
      
      sum += current;
    }
    
    return sum / clusters.size();
  }
  
  private static IClusterable calculateCentroid(final ICluster cluster) {
    final int dimension = cluster.getPoints().get(0).getDimension();
    final Vector vector = new Vector(dimension);

    for (final IClusterable clusterable : cluster.getPoints()) {
      for (int i = 0; i < dimension; i++) {
        vector.set(i, vector.get(i) + clusterable.get(i));
      }
    }

    for (int i = 0; i < dimension; i++) {
      vector.set(i, vector.get(i) / cluster.getN());
    }

    return vector;
  }
  
}
