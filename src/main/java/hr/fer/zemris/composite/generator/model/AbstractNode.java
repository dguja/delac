package hr.fer.zemris.composite.generator.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Apstraktni čvor
 * 
 * @author Daniel
 * 
 */

public abstract class AbstractNode implements Serializable {

  /**
   * Generated serial version ID.
   */
  private static final long serialVersionUID = 4195003577285391138L;

  protected long id;

  protected double reliability;

  protected double weight;

  protected List<AbstractNode> parents = new ArrayList<>();

  protected List<AbstractNode> children = new ArrayList<>();

  public AbstractNode(final long id) {
    super();

    this.id = id;
  }

  public AbstractNode(final long id, final double reliability) {
    super();

    this.id = id;
    this.reliability = reliability;
  }

  protected AbstractNode(final AbstractNode other) {
    super();

    this.id = other.id;
    this.reliability = other.reliability;
    this.weight = other.weight;
  }

  public long getId() {
    return id;
  }

  public List<AbstractNode> getParents() {
    return parents;
  }

  public List<AbstractNode> getChildren() {
    return children;
  }

  public double getReliability() {
    return reliability;
  }

  protected void setReliability(final double reliability) {
    this.reliability = reliability;
  }

  public double getWeight() {
    return weight;
  }

  public void setWeight(final double weight) {
    this.weight = weight;
  }

  @Override
  public int hashCode() {
    return 31 + (int) (id ^ (id >>> 32));
  }

  @Override
  public boolean equals(final Object object) {
    if (this == object) {
      return true;
    }

    if (object == null) {
      return false;
    }

    if (!(object instanceof AbstractNode)) {
      return false;
    }

    final AbstractNode other = (AbstractNode) object;
    if (id != other.id) {
      return false;
    }

    return true;
  }

  public abstract NodeType getType();

}
