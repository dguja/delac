package hr.fer.zemris.composite.cluster.quality;

import hr.fer.zemris.composite.cluster.ICluster;
import hr.fer.zemris.composite.cluster.clusterable.IClusterable;
import hr.fer.zemris.composite.cluster.clusterable.Vector;
import hr.fer.zemris.composite.cluster.distance.EuclidDistance;

import java.util.List;


public class SquaredDistSumQuality {

  public static double measure(List<ICluster> clusters) {
    double distance = 0;
    for (ICluster cluster : clusters) {
      // nadji centroid
      IClusterable sum = new Vector(cluster.getPoints().get(0).getDimension());
      for (IClusterable point : cluster.getPoints()) {
        sum.add(point);
      }
      IClusterable centroid = sum.nScalarMultiply(1./cluster.getN());
      
      // izracunaj udaljenosti
      for (IClusterable point : cluster.getPoints()) {
        distance += Math.pow(EuclidDistance.measure(point, centroid), 2);
      }
    }
    return distance;
  }
}
