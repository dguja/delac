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

    double[][] dp = new double[numParents + 1][k];
    dp[0][0] = 1;
    for (int i = 1; i <= numParents; ++i) {
      double parentReliability = parents.get(i - 1).getReliability();
      for (int j = 0; j < k; ++j) {
        dp[i][j] = dp[i - 1][j] * (1 - parentReliability);
        if (j > 0) {
          dp[i][j] += dp[i - 1][j - 1] * parentReliability;
        }
      }
    }

    reliability = 1.0;
    for (int i = 0; i < k; ++i) {
      reliability -= dp[numParents][i];
    }
  }

  @Override
  public void addParent(final AbstractNode parent) {
    super.addParent(parent);

    k = new IntegerDistributionLimiter(kDistribution, 0, parents.size()).sample();
    if (k < 1) {
      throw new GeneratorException(
          "'parallelParameter' distribution returned a value lesser than 1: " + k + ".");
    }
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
