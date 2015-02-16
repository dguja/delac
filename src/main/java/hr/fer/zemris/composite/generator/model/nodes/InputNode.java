package hr.fer.zemris.composite.generator.model.nodes;

import hr.fer.zemris.composite.generator.model.AbstractNode;
import hr.fer.zemris.composite.generator.model.NodeType;

public class InputNode extends AbstractNode implements Cloneable {

  /**
   * Generated serial version ID.
   */
  private static final long serialVersionUID = -8999082127044865007L;

  public InputNode(final long id, final double reliability) {
    super(id, 0);

    this.reliability = reliability;
  }

  private InputNode(final InputNode other) {
    super(other.id, other.level);

    this.reliability = other.reliability;
    this.weight = other.weight;
  }

  @Override
  public boolean addParent(final AbstractNode parent) {
    throw new UnsupportedOperationException("InputNode can't have parents.");
  }

  public void setReliability(final double reliability) {
    this.reliability = reliability;
  }

  @Override
  public InputNode clone() {
    return new InputNode(this);
  }

  @Override
  public NodeType getType() {
    return NodeType.INPUT;
  }

  @Override
  protected void calculateDirectReliability() {}

  @Override
  protected String getParameterText() {
    return "";
  }

}
