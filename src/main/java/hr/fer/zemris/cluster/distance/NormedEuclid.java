package hr.fer.zemris.cluster.distance;

import hr.fer.zemris.cluster.IClusterable;

public class NormedEuclid {

  public static double measure(IClusterable vector1, IClusterable vector2) {
    if (vector1.getDimension() != vector2.getDimension()) {
      throw new IllegalArgumentException("Vektori nisu istih dimenzija!");
    }

    double sum = 0;
    int section = 0;
    int union = 0;

    for (int i = vector1.getDimension() - 1; i >= 0; i--) {
      if (vector1.getPoint(i) != 0 && vector2.getPoint(i) != 0) {
        section++;
      }
      if (vector1.getPoint(i) != 0 || vector2.getPoint(i) != 0) {
        union++;
      }
      sum += Math.pow(vector1.getPoint(i) - vector2.getPoint(i), 2);
    }

    return sum / (section / union);
  }

}
