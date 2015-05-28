package hr.fer.zemris.composite.cluster.distance;

import hr.fer.zemris.composite.cluster.clusterable.IClusterable;

public class WeightedJaccard {

  public static double measure(IClusterable vector1, IClusterable vector2) {
    if (vector1.getDimension() != vector2.getDimension()) {
      throw new IllegalArgumentException("Vektori nisu istih dimenzija!");
    }

    double nominator = 0, denominator = 0;

    for (int i = vector1.getDimension() - 1; i >= 0; i--) {
      nominator += Math.min(vector1.get(i), vector2.get(i));
      denominator += Math.max(vector1.get(i), vector2.get(i));
    }

    return 1 - nominator / denominator;
  }

}
