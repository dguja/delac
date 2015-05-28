package hr.fer.zemris.composite.cluster.algorithm.bfr;

import hr.fer.zemris.composite.cluster.ICluster;
import hr.fer.zemris.composite.cluster.algorithm.IAlgorithm;
import hr.fer.zemris.composite.cluster.clusterable.IClusterable;
import hr.fer.zemris.composite.cluster.distance.DistanceType;
import hr.fer.zemris.composite.cluster.distance.IDistanceMeasure;
import hr.fer.zemris.composite.cluster.quality.IQualityMeasure;
import hr.fer.zemris.composite.cluster.quality.QualityType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BFR implements IAlgorithm {

  private static final int CHUNK_SIZE = 1000;
  
  private static int mahalanobisTreshold;
  
  private List<ICluster> clusters = new ArrayList<>();
  
  private Set<ClusterSummary> discardSet = new HashSet<>();
  
  private Set<ClusterSummary> compressedSet = new HashSet<>();
  
  private Set<IClusterable> retainedSet = new HashSet<>();
  
  private List<IClusterable> clusterables;

  private IDistanceMeasure distanceMeasure;

  private IQualityMeasure qualityMeasure;
  
  public BFR(IDistanceMeasure distanceMeasure, IQualityMeasure qualityMeasure) {
    super();
    this.distanceMeasure = distanceMeasure;
    this.qualityMeasure = qualityMeasure;
  }

  @Override
  public void setDistanceType(DistanceType distanceType) {
    distanceMeasure = distanceType.getDistanceMeasure();
  }

  @Override
  public void setQualityType(QualityType qualityType) {
    qualityMeasure = qualityType.getQualityMeasure();
  }

  @Override
  public List<ICluster> cluster(List<IClusterable> clusterables) {
    clusters.clear();
    
    mahalanobisTreshold = clusterables.get(0).getDimension();


    return clusters;
  }
  
  private void clusterChunk(List<IClusterable> clusterables) {
    
  }
  
}
