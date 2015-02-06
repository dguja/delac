package hr.fer.zemris.composite.generator;

import hr.fer.zemris.composite.generator.distribution.DiscreteDistributionLimiter;
import hr.fer.zemris.composite.generator.distribution.IDiscreteDistribution;
import hr.fer.zemris.composite.generator.distribution.IRealDistribution;
import hr.fer.zemris.composite.generator.nodes.InputNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModelGenerator {

  private final int modelCount;

  private final Map<String, IDiscreteDistribution> discreteDistributions;

  private final Map<String, IRealDistribution> realDistributions;

  public ModelGenerator(final int modelCount, final Map<String, IDiscreteDistribution> discreteDistributions,
      final Map<String, IRealDistribution> realDistributions) {
    super();

    this.modelCount = modelCount;
    this.discreteDistributions = discreteDistributions;
    this.realDistributions = realDistributions;
  }

  public List<Model> generate() {
    // 1
    final int n = discreteDistributions.get("d1").sample();

    final List<InputNode> inputs = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      inputs.add(new InputNode(realDistributions.get("p1").sample()));
    }

    final IDiscreteDistribution distM = new DiscreteDistributionLimiter(discreteDistributions.get("d2"), 0, n + 1);

    final List<Model> models = new ArrayList<>();
    for (int i = 0; i < modelCount; i++) {
      models.add(generateModel(inputs, distM));
    }

    return models;
  }

  private Model generateModel(final List<InputNode> datasetInputs, final IDiscreteDistribution distM) {
    // 2
    final List<AbstractNode> inputs = new ArrayList<>();

    return null; // TODO

  }

}
