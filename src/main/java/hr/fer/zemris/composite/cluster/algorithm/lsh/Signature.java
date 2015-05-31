package hr.fer.zemris.composite.cluster.algorithm.lsh;

import java.util.ArrayList;
import java.util.List;

public class Signature {

  private final List<Integer> components;

  public Signature(final List<Integer> components) {
    super();

    this.components = new ArrayList<>(components);
  }

  public int size() {
    return components.size();
  }

  public int get(final int index) {
    return components.get(index);
  }

  public Signature subSignature(final int index, final int bandSize) {
    return new Signature(components.subList(index * bandSize, Math.min((index + 1) * bandSize, components.size())));
  }

  @Override
  public int hashCode() {
    return components.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj == null) {
      return false;
    }

    if (!(obj instanceof Signature)) {
      return false;
    }

    final Signature other = (Signature) obj;
    if (components == null) {
      if (other.components != null) {
        return false;
      }
    } else if (!components.equals(other.components)) {
      return false;
    }

    return true;
  }

}
