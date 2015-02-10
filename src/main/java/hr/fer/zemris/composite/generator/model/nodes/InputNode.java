package hr.fer.zemris.composite.generator.model.nodes;

import java.awt.event.MouseAdapter;

import hr.fer.zemris.composite.generator.model.AbstractNode;
import hr.fer.zemris.composite.generator.model.NodeType;

public class InputNode extends AbstractNode implements Cloneable {

  /**
   * Generated serial version ID.
   */
  private static final long serialVersionUID = -8999082127044865007L;

  public InputNode(final long id, final double reliability) {
    super(id);
    this.reliability = reliability;
  }

  private InputNode(final InputNode other) {
    super(other.id);
    this.reliability = other.reliability;
    this.weight = other.weight;
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
  protected void calculateDirectReliability() {
    // NOTHING TO DO
  }

  @Override
  public void calculateReliability(boolean direction) {
    // TODO Auto-generated method stub
  }

}
