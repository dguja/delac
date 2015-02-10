package hr.fer.zemris.composite.generator.model.nodes;

import hr.fer.zemris.composite.generator.model.AbstractNode;
import hr.fer.zemris.composite.generator.model.NodeType;

/**
 * Sequence node.
 * 
 * @author mmilisic
 *
 */
public class SequenceNode extends AbstractNode {

  /**
   * Generated serial version ID.
   */
  private static final long serialVersionUID = -3575455154989781205L;

  public SequenceNode(long id) {
    super(id);
  }

  @Override
  protected void calculateDirectReliability() {
    reliability = 1.0;
    for (AbstractNode node : parents) {
      reliability *= node.getReliability();
    }
  }

  @Override
  public void calculateReliability(boolean direction) {
    // TODO Auto-generated method stub

  }

  @Override
  public NodeType getType() {
    return NodeType.SEQUENCE;
  }

}
