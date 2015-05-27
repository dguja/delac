package hr.fer.zemris.composite.cluster.distance;

import hr.fer.zemris.composite.cluster.clusterable.IClusterable;

@FunctionalInterface
public interface IDistanceMeasure {

  double measure(IClusterable clusterable1, IClusterable clusterable2);

}
