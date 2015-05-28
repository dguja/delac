package hr.fer.zemris.composite.cluster.quality;



public enum QualityType {

  SQUARED_DIST_SUM(SquaredDistSumQuality::measure);
  
 private IQualityMeasure qualityMeasure;
  
  private QualityType(IQualityMeasure qualityMeasure) {
    this.qualityMeasure = qualityMeasure;
  }
  
  public IQualityMeasure getQualityMeasure() {
    return qualityMeasure;
  }
}
