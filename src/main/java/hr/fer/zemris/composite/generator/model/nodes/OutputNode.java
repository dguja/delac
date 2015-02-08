package hr.fer.zemris.composite.generator.model.nodes;

import hr.fer.zemris.composite.generator.model.AbstractNode;
import hr.fer.zemris.composite.generator.model.NodeType;

public class OutputNode extends AbstractNode {

  /**
   * Generated serial version ID.
   */
  private static final long serialVersionUID = -4865565898126417383L;

  public OutputNode(final long id) {
    super(id);
  }

  @Override
  public NodeType getType() {
    return NodeType.OUTPUT;
  }

}
