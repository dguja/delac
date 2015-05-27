package hr.fer.zemris.composite.cluster.quality;

import hr.fer.zemris.composite.cluster.ICluster;

import java.util.List;

public interface IQualityMeasure {

  double measure(List<ICluster> clusters);

}
