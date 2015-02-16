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

  public SequenceNode(final long id, final int level) {
    super(id, level);
  }

  @Override
  protected void calculateDirectReliability() {
    reliability = 1.0;
    for (final AbstractNode node : parents) {
      reliability *= node.getReliability();
    }
  }

  @Override
  public NodeType getType() {
    return NodeType.SEQUENCE;
  }

  @Override
  protected String getParameterText() {
    return "";
  }

}
