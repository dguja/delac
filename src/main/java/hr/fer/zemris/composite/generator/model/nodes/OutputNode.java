package hr.fer.zemris.composite.generator.model.nodes;

import hr.fer.zemris.composite.generator.model.AbstractNode;
import hr.fer.zemris.composite.generator.model.NodeType;

public class OutputNode extends AbstractNode {

  public OutputNode(final long id) {
    super(id);
  }

  @Override
  public NodeType getType() {
    return NodeType.OUTPUT;
  }

}
