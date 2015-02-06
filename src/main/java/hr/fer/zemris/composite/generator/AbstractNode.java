package hr.fer.zemris.composite.generator;

import java.util.List;

/**
 * Apstraktni čvor
 * 
 * @author Daniel
 * 
 */

public abstract class AbstractNode {

  private long id;

  protected double reliability;

  protected double weight;

  protected List<AbstractNode> parents;

  protected List<AbstractNode> children;

  public AbstractNode() {
    super();
  }

  protected AbstractNode(final double reliability) {
    super();

    this.reliability = reliability;
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

}
