package hr.fer.zemris.composite.generator.model.nodes;

import hr.fer.zemris.composite.generator.model.AbstractNode;
import hr.fer.zemris.composite.generator.model.NodeType;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.distribution.RealDistribution;

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

  protected List<Double> probabilities = new ArrayList<>();

  private final RealDistribution distribution;

  public BranchNode(final long id, final int level, final RealDistribution distribution) {
    super(id, level);

    this.distribution = distribution;
  }

  @Override
  public boolean addParent(final AbstractNode parent) {
    super.addParent(parent);

    probabilities.add(distribution.sample());

    return true;
  }

  @Override
  protected void calculateDirectReliability() {
    final int numParents = parents.size();
    final int numCombinations = 1 << numParents;

    reliability = 0.0;
    for (int mask = 0; mask < numCombinations; ++mask) {
      double falseProbability = 0;
      double combReliability = 1.0;

      for (int i = 0; i < numParents; ++i) {
        final double parentReliability = parents.get(i).getReliability();

        if ((mask & (1 << i)) == 0) {
          falseProbability += probabilities.get(i);
          combReliability *= (1 - parentReliability);
        } else {
          combReliability *= parentReliability;
        }
      }

      combReliability *= (1 - falseProbability);
      reliability += combReliability;
    }
  }

  @Override
  public NodeType getType() {
    return NodeType.BRANCH;
  }

}
