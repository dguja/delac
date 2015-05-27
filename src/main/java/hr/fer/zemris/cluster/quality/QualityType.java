package hr.fer.zemris.cluster.quality;

import hr.fer.zemris.cluster.IQualityMeasure;


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
