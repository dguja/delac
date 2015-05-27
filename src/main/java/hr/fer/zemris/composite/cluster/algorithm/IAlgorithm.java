package hr.fer.zemris.composite.cluster.algorithm;

import hr.fer.zemris.composite.cluster.ICluster;
import hr.fer.zemris.composite.cluster.clusterable.IClusterable;
import hr.fer.zemris.composite.cluster.distance.DistanceType;
import hr.fer.zemris.composite.cluster.quality.QualityType;

import java.util.List;

public interface IAlgorithm {

  List<ICluster> cluster(List<IClusterable> clusterables, DistanceType distanceType,
      QualityType qualityType);
}
