package hr.fer.zemris.composite.cluster.distance;



public enum DistanceType {

  EUCLID(EuclidDistance::measure), 
  NORMED_EUCLID(NormedEuclid::measure), 
  WEIGHTED_JACCARD(WeightedJaccard::measure), 
  COSINE(null);
  
  private IDistanceMeasure distanceMeasure;
  
  private DistanceType(IDistanceMeasure distanceMeasure) {
    this.distanceMeasure = distanceMeasure;
  }
  
  public IDistanceMeasure getDistanceMeasure() {
    return distanceMeasure;
  }
  
}
