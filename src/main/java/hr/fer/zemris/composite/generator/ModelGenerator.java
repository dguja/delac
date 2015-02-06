package hr.fer.zemris.composite.generator;

import hr.fer.zemris.composite.generator.distribution.DiscreteDistributionLimiter;
import hr.fer.zemris.composite.generator.distribution.IDiscreteDistribution;
import hr.fer.zemris.composite.generator.distribution.IRealDistribution;
import hr.fer.zemris.composite.generator.nodes.InputNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModelGenerator {

  private int modelCount;

  private Map<String, IDiscreteDistribution> discreteParameters;

  private Map<String, IRealDistribution> realParameters;

  public List<Model> generate() {
    final int n = discreteParameters.get("d1").sample();

    final List<InputNode> inputs = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      inputs.add(new InputNode(realParameters.get("p1").sample()));
    }

    final IDiscreteDistribution distM = new DiscreteDistributionLimiter(discreteParameters.get("d2"), 0, n + 1);

    final List<Model> models = new ArrayList<>();
    for (int i = 0; i < modelCount; i++) {
      models.add(generateModel(inputs, distM));
    }

    return models;
  }

  private Model generateModel(final List<InputNode> inputs, final IDiscreteDistribution distM) {

    return null; // TODO

  }

}
