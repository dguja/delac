package hr.fer.zemris.composite.generator.model.nodes;

import hr.fer.zemris.composite.generator.model.AbstractNode;
import hr.fer.zemris.composite.generator.model.NodeType;

public class OutputNode extends SequenceNode {

  /**
   * Generated serial version ID.
   */
  private static final long serialVersionUID = -4865565898126417383L;

  public OutputNode(final long id, final int level) {
    super(id, level);
  }

  @Override
  protected void addChild(final AbstractNode child) {
    throw new UnsupportedOperationException("OutputNode can't have children.");
  }

  @Override
  public NodeType getType() {
    return NodeType.OUTPUT;
  }

}
