package hr.fer.zemris.composite.generator.model.nodes;

import hr.fer.zemris.composite.generator.model.AbstractNode;
import hr.fer.zemris.composite.generator.model.NodeType;

public class InputNode extends AbstractNode implements Cloneable {

  /**
   * Generated serial version ID.
   */
  private static final long serialVersionUID = -8999082127044865007L;

  public InputNode(final long id, final double reliability) {
    super(id, reliability);
  }

  private InputNode(final InputNode other) {
    super(other);
  }
  
  @Override
  public boolean addParent(AbstractNode parent) {
    throw new UnsupportedOperationException("InputNode can't have parents.");
  }

  @Override
  public void setReliability(final double reliability) {
    super.setReliability(reliability);
  }

  @Override
  public InputNode clone() {
    return new InputNode(this);
  }

  @Override
  public NodeType getType() {
    return NodeType.INPUT;
  }

}
