package hr.fer.zemris.composite.generator.daniel;

import hr.fer.zemris.composite.generator.daniel.cluster.Cluster;

import java.util.List;

public class KMeans {

  private List<IClusterable> clusterable;

  private IDistanceMeasure distanceMeasure;

  public KMeans(List<IClusterable> clusterable, IDistanceMeasure distanceMeasure) {
    this.clusterable = clusterable;
    this.distanceMeasure = distanceMeasure;
  }

  public List<Cluster> run() {
    
    // TODO
    
    return null;
  }
  
}
