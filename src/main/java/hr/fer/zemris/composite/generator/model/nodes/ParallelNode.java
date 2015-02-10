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
  
  public ParallelNode(long id, int level, int k) {
    super(id, level);
    this.k = k;
  }

  @Override
  protected void calculateDirectReliability() {
    int numParents = parents.size();
    int numCombinations = 1<<numParents;
    
    for (int mask = 0; mask < numCombinations; ++mask) {
      if (Integer.bitCount(mask) < k) {
        continue;
      }
      
      double combReliability = 1.0;
      for (int i = 0; i < numParents; ++i) {
        double parentReliability = parents.get(i).getReliability();
        
        if ((mask&(1<<i)) == 0) {
          combReliability *= (1-parentReliability);
        } else {
          combReliability *= parentReliability;
        }
      }
      
      reliability += combReliability;
    }
  }

  @Override
  public NodeType getType() {
    return NodeType.PARALLEL;
  }
  
}
