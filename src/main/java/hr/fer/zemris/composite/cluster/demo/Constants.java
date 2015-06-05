package hr.fer.zemris.composite.cluster.demo;

import hr.fer.zemris.composite.cluster.distance.DistanceType;
import hr.fer.zemris.composite.cluster.quality.QualityType;

public class Constants {

  public static final String TEST1 = "data/texture"; // 11

  public static final String TEST2 = "data/winequality-white"; // 11

  public static final String TEST3 = "data/segment"; // 7

  public static final String TEST = TEST1;
  
  public static final DistanceType DISTANCE_TYPE = DistanceType.EUCLID;

  public static final QualityType QUALITY_TYPE = QualityType.SQUARED_DIST_SUM;
  
  public static final int CLUSTER_NUM = 11;
}
