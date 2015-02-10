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

  public void setReliability(final double reliability) {
    this.reliability = reliability;
  }

  public double getWeight() {
    return weight;
  }

  public void setWeight(double weight) {
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
  
  protected abstract void calculateDirectReliability();
  
  public abstract void calculateReliability(boolean direction);

  public abstract NodeType getType();

}
