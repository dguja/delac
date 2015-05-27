package hr.fer.zemris.cluster.distance;

import hr.fer.zemris.cluster.IDistanceQuality;


public enum DistanceType {

  EUCLID(EuclidDistance::measure), 
  NORMED_EUCLID(NormedEuclid::measure), 
  WEIGHTED_JACCARD(WeightedJaccard::measure), 
  COSINE(null);
  
  private IDistanceQuality distanceMeasure;
  
  private DistanceType(IDistanceQuality distanceMeasure) {
    this.distanceMeasure = distanceMeasure;
  }
  
  public IDistanceQuality getDistanceMeasure() {
    return distanceMeasure;
  }
  
}
