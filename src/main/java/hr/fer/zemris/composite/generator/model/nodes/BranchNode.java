package hr.fer.zemris.composite.generator.model.nodes;

import hr.fer.zemris.composite.generator.exception.GeneratorException;
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

  private final List<Double> probabilities = new ArrayList<>();

  private double probabilitySum = 0.;

  private final RealDistribution probabilityDistribution;

  public BranchNode(final long id, final int level, final RealDistribution probabilityDistribution) {
    super(id, level);

    this.probabilityDistribution = probabilityDistribution;
  }

  public List<Double> getNormalizedProbabilities() {
    List<Double> normalizedProbabilities = new ArrayList<>();
    
    for (Double probability : probabilities) {
      normalizedProbabilities.add(probability/probabilitySum);
    }
    
    return normalizedProbabilities;
  }

  @Override
  public void addParent(final AbstractNode parent) {
    super.addParent(parent);

    final double probability = probabilityDistribution.sample();
    if (probability < 0 || probability > 1) {
      throw new GeneratorException("'branchProbability' distribution returned a value outside [0, 1]: " + probability
          + ".");
    }

    probabilities.add(probability);
    probabilitySum += probability;
  }

  @Override
  public void clearParents() {
    probabilities.clear();
    probabilitySum = 0.;

    super.clearParents();
  }

  @Override
  protected void calculateDirectReliability() {
    final int numParents = parents.size();
    final int numCombinations = 1 << numParents;

    List<Double> normalizedProbabilites = getNormalizedProbabilities();
    reliability = 0.0;
    for (int mask = 0; mask < numCombinations; ++mask) {
      double falseProbability = 0;
      double combReliability = 1.0;

      for (int i = 0; i < numParents; ++i) {
        final double parentReliability = parents.get(i).getReliability();

        if ((mask & (1 << i)) == 0) {
          falseProbability += normalizedProbabilites.get(i);
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

  @Override
  protected String getParameterText() {
    return "";
  }

}
