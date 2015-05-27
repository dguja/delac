package hr.fer.zemris.composite.cluster.quality;



public enum QualityType {

  SIMPLE(SimpleQuality::measure);
  
 private IQualityMeasure qualityMeasure;
  
  private QualityType(IQualityMeasure qualityMeasure) {
    this.qualityMeasure = qualityMeasure;
  }
  
  public IQualityMeasure getDistanceMeasure() {
    return qualityMeasure;
  }
}
