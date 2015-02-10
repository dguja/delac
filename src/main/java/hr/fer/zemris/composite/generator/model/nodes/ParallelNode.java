package hr.fer.zemris.composite.generator.model.nodes;

import hr.fer.zemris.composite.generator.model.AbstractNode;
import hr.fer.zemris.composite.generator.model.NodeType;

/**
 * Parallel node.
 * 
 * @author mmilisic
 *
 */
public class ParallelNode extends AbstractNode {

  /**
   * Generated serial version ID.
   */
  private static final long serialVersionUID = 4126103513741427044L;
  
  /**
   * Indicates how many of the n parallel workflow execution paths have to be successfully 
   * completed in order for the composition to display fault-free behaviour.
   */
  protected int k;
  
  public ParallelNode(long id, int k) {
    super(id);
    this.k = k;
  }

  @Override
  protected void calculateDirectReliability() {
    for (int mask = 0; mask < (1<<parents.size()); ++mask) {
      
    }
  }

  @Override
  public void calculateReliability(boolean direction) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public NodeType getType() {
    return NodeType.PARALLEL;
  }

}
