package hr.fer.zemris.composite.generator.nodes;

import hr.fer.zemris.composite.generator.AbstractNode;

public class InputNode extends AbstractNode implements Cloneable {

  public InputNode(final double reliability) {
    super(reliability);
  }

  private InputNode(final InputNode other) {
    super(other);
  }

  @Override
  public InputNode clone() {
    return new InputNode(this);
  }

}
