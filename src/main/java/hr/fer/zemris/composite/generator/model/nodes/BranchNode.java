package hr.fer.zemris.composite.generator.model.nodes;

import hr.fer.zemris.composite.generator.model.AbstractNode;
import hr.fer.zemris.composite.generator.model.NodeType;

/**
 * Branch node.
 * 
 * @author mmilisic
 *
 */
public class BranchNode extends AbstractNode {

  /**
   * Generated serial version ID.
   */
  private static final long serialVersionUID = 1510309148428873743L;

  public BranchNode(long id) {
    super(id);
    
  }

  @Override
  protected void calculateDirectReliability() {
    // TODO napisi kod
    
  }

  @Override
  public void calculateReliability(boolean direction) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public NodeType getType() {
    return NodeType.BRANCH;
  }

}
