package hr.fer.zemris.cluster;

import java.util.List;


public interface IQualityMeasure {

  double measure(List<ICluster> clusters);

}
