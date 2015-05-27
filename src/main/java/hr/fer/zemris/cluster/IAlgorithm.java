package hr.fer.zemris.cluster;

import hr.fer.zemris.cluster.distance.DistanceType;
import hr.fer.zemris.cluster.quality.QualityType;

import java.util.List;


public interface IAlgorithm {

  List<ICluster> cluster(List<IClusterable> clusterables, DistanceType distanceType, QualityType qualityType);
}
