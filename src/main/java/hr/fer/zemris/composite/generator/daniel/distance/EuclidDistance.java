package hr.fer.zemris.composite.generator.daniel.distance;

import hr.fer.zemris.composite.generator.daniel.IClusterable;
import hr.fer.zemris.composite.generator.daniel.IDistanceMeasure;

public class EuclidDistance implements IDistanceMeasure {

  @Override
  public double compute(IClusterable vector1, IClusterable vector2) {
    if (vector1.getDimension() != vector2.getDimension()) {
      throw new IllegalArgumentException("Vektori nisu istih dimenzija!");
    }

    int n = 0;
    double sum = 0;

    for (int i = vector1.getDimension() - 1; i >= 0; i--) {
      if (vector1.getPoint(i) != 0 && vector2.getPoint(i) != 0) {
        n++;
      }
      sum += Math.pow(vector1.getPoint(i) - vector2.getPoint(i), 2);
    }

    return Math.sqrt(sum) / (double) n;
  }

}
