package hr.fer.zemris.composite.generator.nodes;

import java.util.List;

public abstract class AbstractNode {

  protected List<AbstractNode> parents;

  protected List<AbstractNode> children;

  public AbstractNode() {
    super();
  }

  public List<AbstractNode> getParents() {
    return parents;
  }

  public List<AbstractNode> getChildren() {
    return children;
  }

  public abstract double getReliability();

}
