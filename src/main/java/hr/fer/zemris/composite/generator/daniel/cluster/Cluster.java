package hr.fer.zemris.composite.generator.daniel.cluster;

import hr.fer.zemris.composite.generator.daniel.IClusterable;

import java.util.List;

public class Cluster {

  private List<IClusterable> clusterable;

  private IClusterable center;

  public Cluster(List<IClusterable> clusterable, IClusterable center) {
    this.clusterable = clusterable;
  }

  public List<IClusterable> getClusterable() {
    return clusterable;
  }

  public void setClusterable(List<IClusterable> clusterable) {
    this.clusterable = clusterable;
  }

  public IClusterable getCenter() {
    return center;
  }

  public void setCenter(IClusterable center) {
    this.center = center;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((center == null) ? 0 : center.hashCode());
    result = prime * result + ((clusterable == null) ? 0 : clusterable.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Cluster other = (Cluster) obj;
    if (center == null) {
      if (other.center != null)
        return false;
    } else if (!center.equals(other.center))
      return false;
    if (clusterable == null) {
      if (other.clusterable != null)
        return false;
    } else if (!clusterable.equals(other.clusterable))
      return false;
    return true;
  }

}
