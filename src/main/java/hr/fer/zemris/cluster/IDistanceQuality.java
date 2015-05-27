package hr.fer.zemris.cluster;


@FunctionalInterface
public interface IDistanceQuality {

  double measure(IClusterable clusterable1, IClusterable clusterable2);
  
}
