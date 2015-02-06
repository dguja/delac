package hr.fer.zemris.composite.generator.model.nodes;

import hr.fer.zemris.composite.generator.model.AbstractNode;
import hr.fer.zemris.composite.generator.model.NodeType;

public class InputNode extends AbstractNode implements Cloneable {

  public InputNode(final long id, final double reliability) {
    super(id, reliability);
  }

  private InputNode(final InputNode other) {
    super(other);
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
