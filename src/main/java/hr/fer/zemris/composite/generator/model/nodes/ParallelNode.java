package hr.fer.zemris.composite.generator.model.nodes;

import hr.fer.zemris.composite.generator.distribution.IntegerDistributionLimiter;
import hr.fer.zemris.composite.generator.exception.GeneratorException;
import hr.fer.zemris.composite.generator.model.AbstractNode;
import hr.fer.zemris.composite.generator.model.NodeType;

import org.apache.commons.math3.distribution.IntegerDistribution;

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
   * Indicates how many of the n parallel workflow execution paths have to be successfully completed
   * in order for the composition to display fault-free behaviour.
   */
  private int k = 0;

  private final IntegerDistribution kDistribution;

  public ParallelNode(final long id, final int level, final IntegerDistribution kDistribution) {
    super(id, level);

    this.kDistribution = kDistribution;
  }

  @Override
  protected void calculateDirectReliability() {
    final int numParents = parents.size();
    final int numCombinations = 1 << numParents;

    for (int mask = 0; mask < numCombinations; ++mask) {
      if (Integer.bitCount(mask) < k) {
        continue;
      }

      double combReliability = 1.0;
      for (int i = 0; i < numParents; ++i) {
        final double parentReliability = parents.get(i).getReliability();

        if ((mask & (1 << i)) == 0) {
          combReliability *= (1 - parentReliability);
        } else {
          combReliability *= parentReliability;
        }
      }

      reliability += combReliability;
    }
  }

  @Override
  public boolean addParent(final AbstractNode parent) {
    super.addParent(parent);

    k = new IntegerDistributionLimiter(kDistribution, 0, children.size()).sample();
    if (k < 1) {
      throw new GeneratorException("'parallelParameter' distribution returned a value lesser than 1: " + k + ".");
    }

    return true;
  }

  @Override
  public void clearParents() {
    super.clearParents();

    k = 0;
  }

  @Override
  public NodeType getType() {
    return NodeType.PARALLEL;
  }

  @Override
  protected String getParameterText() {
    return Integer.toString(k);
  }

}
