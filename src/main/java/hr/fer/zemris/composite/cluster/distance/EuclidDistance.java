package hr.fer.zemris.composite.cluster.distance;

import hr.fer.zemris.composite.cluster.clusterable.IClusterable;

public class EuclidDistance {

  public static double measure(IClusterable vector1, IClusterable vector2) {
    if (vector1.getDimension() != vector2.getDimension()) {
      throw new IllegalArgumentException("Vektori nisu istih dimenzija!");
    }

    int n = 0;
    double sum = 0;

    for (int i = vector1.getDimension() - 1; i >= 0; i--) {
      if (vector1.getComponent(i) != 0 && vector2.getComponent(i) != 0) {
        n++;
      }
      sum += Math.pow(vector1.getComponent(i) - vector2.getComponent(i), 2);
    }

    return Math.sqrt(sum) / (double) n;
  }

}
