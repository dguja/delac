package hr.fer.zemris.composite.generator.model.nodes;

import java.util.ArrayList;
import java.util.List;

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

  protected List<Double> probabilities;
  
  public BranchNode(long id, int level, List<Double> probabilities) {
    super(id, level);
    this.probabilities = new ArrayList<>(probabilities);
  }

  @Override
  protected void calculateDirectReliability() {
    int numParents = parents.size();
    int numCombinations = 1<<numParents;
    
    reliability = 0.0;
    for (int mask = 0; mask < numCombinations; ++mask) {
      double falseProbability = 0;
      double combReliability = 1.0;
      
      for (int i = 0; i < numParents; ++i) {
        double parentReliability = parents.get(i).getReliability();
        
        if ((mask&(1<<i)) == 0) {
          falseProbability += probabilities.get(i);
          combReliability *= (1-parentReliability);
        } else {
          combReliability *= parentReliability;
        }
      }
      
      combReliability *= (1-falseProbability);
      reliability += combReliability;
    }
  }

  @Override
  public NodeType getType() {
    return NodeType.BRANCH;
  }

}
