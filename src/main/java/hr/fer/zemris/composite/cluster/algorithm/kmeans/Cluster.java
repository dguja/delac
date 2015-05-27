package hr.fer.zemris.composite.cluster.algorithm.kmeans;

import hr.fer.zemris.composite.cluster.clusterable.IClusterable;

import java.util.Set;

public class Cluster {

  private Set<IClusterable> clusterable;

  private IClusterable centroid;

  public Cluster(Set<IClusterable> clusterable, IClusterable centroid) {
    this.clusterable = clusterable;
  }

  public Set<IClusterable> getClusterable() {
    return clusterable;
  }

  public void setClusterable(Set<IClusterable> clusterable) {
    this.clusterable = clusterable;
  }

  public IClusterable getCentroid() {
    return centroid;
  }

  public void setCentroid(IClusterable centroid) {
    this.centroid = centroid;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((centroid == null) ? 0 : centroid.hashCode());
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
    if (centroid == null) {
      if (other.centroid != null)
        return false;
    } else if (!centroid.equals(other.centroid))
      return false;
    if (clusterable == null) {
      if (other.clusterable != null)
        return false;
    } else if (!clusterable.equals(other.clusterable))
      return false;
    return true;
  }

}
