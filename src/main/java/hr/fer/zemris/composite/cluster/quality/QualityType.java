package hr.fer.zemris.composite.cluster.quality;

public enum QualityType {

  SQUARED_DIST_SUM(SquaredDistSumQuality::measure), DB_INDEX(DBIndexQuality::measure);

  private IQualityMeasure qualityMeasure;
  
  private QualityType(final IQualityMeasure qualityMeasure) {
    this.qualityMeasure = qualityMeasure;
  }
  
  public IQualityMeasure getQualityMeasure() {
    return qualityMeasure;
  }

}
