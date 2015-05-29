package hr.fer.zemris.composite.cluster.algorithm.hierarchical;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import hr.fer.zemris.composite.cluster.ICluster;
import hr.fer.zemris.composite.cluster.clusterable.IClusterable;

public class Cluster implements ICluster {

  private static Long nextId = Long.valueOf(0);

  private Long id;

  private List<IClusterable> vectors = new ArrayList<>();

  private IClusterable vectorSum;

  public Cluster(IClusterable vector) {
    vectors.add(vector);
    vectorSum = vector;
    setId();
  }

  public Cluster(Collection<IClusterable> vector, IClusterable vectorSum) {
    vectors.addAll(vector);
    this.vectorSum = vectorSum;
    setId();
  }

  public Cluster(Cluster first, Cluster second){
    setId();
    vectors.addAll(first.vectors);
    vectors.addAll(second.vectors);
    vectorSum.add(first.vectorSum);
    vectorSum.add(second.vectorSum);
  }

  public Long getId() {
    return id;
  }

  private void setId() {
    this.id = nextId;
    nextId++;
  }

  public IClusterable getCentroid() {
    return vectorSum.nScalarMultiply(1. / vectors.size());
  }

  public int getSize() {
    return vectors.size();
  }

  public IClusterable getComponent(int index) {
    if (index >= vectors.size() || index < 0) {
      throw new ArrayIndexOutOfBoundsException("Expected index from 0 to " + (vectors.size() - 1)
          + " and got index " + index);
    }
    return vectors.get(index);
  }

  @Override
  public List<IClusterable> getPoints() {
    return Collections.unmodifiableList(vectors);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
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
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }

}
