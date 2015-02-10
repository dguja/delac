package hr.fer.zemris.composite.generator.model.nodes;

import hr.fer.zemris.composite.generator.model.AbstractNode;
import hr.fer.zemris.composite.generator.model.NodeType;

/**
 * Loop node.
 * 
 * @author mmilisic
 *
 */
public class LoopNode extends AbstractNode {

  /**
   * Generated serial version ID.
   */
  private static final long serialVersionUID = 2416008536138769590L;

  protected int numRepetitions;

  public LoopNode(long id, int level) {
    super(id, level);
  }
    
  public int getNumRepetitions() {
    return numRepetitions;
  }
  
  public void setNumRepetitions(int numRepetitions) {
    this.numRepetitions = numRepetitions;
  }

  @Override
  protected void calculateDirectReliability() {
    reliability = Math.pow(parents.get(0).getReliability(), numRepetitions);
  }

  @Override
  public NodeType getType() {
    return NodeType.LOOP;
  }

}
