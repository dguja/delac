package hr.fer.zemris.composite.cluster.algorithm.hierarchical;

public class Distance implements Comparable<Distance> {

  private Cluster first;

  private Cluster second;

  private Double distance;

  public Distance(Cluster first, Cluster second, double distance) {
    super();
    this.first = first;
    this.second = second;
    this.distance = distance;
  }

  public Cluster getFirst() {
    return first;
  }

  public Cluster getSecond() {
    return second;
  }

  public double getDistance() {
    return distance;
  }

  @Override
  public int compareTo(Distance o) {
    return distance.compareTo(o.distance);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((first == null) ? 0 : first.hashCode());
    result = prime * result + ((second == null) ? 0 : second.hashCode());
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
    Distance other = (Distance) obj;
    if (first == null) {
      if (other.first != null)
        return false;
    } else if (!first.equals(other.first))
      return false;
    if (second == null) {
      if (other.second != null)
        return false;
    } else if (!second.equals(other.second))
      return false;
    return true;
  }

}
