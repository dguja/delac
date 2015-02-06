package hr.fer.zemris.composite.generator;

import hr.fer.zemris.composite.generator.distribution.DiscreteDistributionLimiter;
import hr.fer.zemris.composite.generator.distribution.IDiscreteDistribution;
import hr.fer.zemris.composite.generator.distribution.IRealDistribution;
import hr.fer.zemris.composite.generator.model.AbstractNode;
import hr.fer.zemris.composite.generator.model.Model;
import hr.fer.zemris.composite.generator.model.nodes.InputNode;
import hr.fer.zemris.composite.generator.random.RandomUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelGenerator {

  private final int modelCount;

  private final boolean copyInputs;

  private final Map<String, IDiscreteDistribution> discreteDistributions;

  private final Map<String, IRealDistribution> realDistributions;

  private int idCount;

  public ModelGenerator(final int modelCount, final boolean copyInputs,
      final Map<String, IDiscreteDistribution> discreteDistributions,
      final Map<String, IRealDistribution> realDistributions) {
    super();

    this.idCount = 0;

    this.modelCount = modelCount;
    this.copyInputs = copyInputs;
    this.discreteDistributions = discreteDistributions;
    this.realDistributions = realDistributions;
  }

  public List<Model> generate() {
    // 1
    final int n = discreteDistributions.get("d1").sample();

    final List<InputNode> inputs = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      inputs.add(new InputNode(nextId(), realDistributions.get("p1").sample()));
    }

    final IDiscreteDistribution mDistribution =
        new DiscreteDistributionLimiter(discreteDistributions.get("d2"), 0, n + 1);

    final List<Model> models = new ArrayList<>();
    for (int i = 0; i < modelCount; i++) {
      models.add(generateModel(inputs, mDistribution));
    }

    return models;
  }

  private Model generateModel(final List<InputNode> datasetInputs, final IDiscreteDistribution mDistribution) {
    // 2
    final List<InputNode> originals = RandomUtilities.choose(datasetInputs, mDistribution.sample());

    final List<AbstractNode> inputs = new ArrayList<>();
    for (int i = 0; i < originals.size(); i++) {
      inputs.add(copyInputs ? originals.get(i).clone() : originals.get(i));
    }

    // 3
    final int k = discreteDistributions.get("d3").sample();

    final List<Map<AbstractNode, Integer>> levelEdges = new ArrayList<>();
    for (int i = 0; i < k; i++) {
      levelEdges.add(new HashMap<AbstractNode, Integer>());
    }

    for (int i = 0; i < k; i++) {
      final List<AbstractNode> nodes;
      if (i == 0) {
        nodes = inputs;
      } else {
        nodes = new ArrayList<>();
        // 6
        // TODO
      }

      final IDiscreteDistribution targetLevelDistribution =
          new DiscreteDistributionLimiter(discreteDistributions.get("d9"), 0, k - i + 1);

      for (final AbstractNode node : nodes) {
        // 4
        final int edgeCount = discreteDistributions.get(node.getType().getDistributionName()).sample();
        for (int j = 0; j < edgeCount; j++) {
          final int targetLevel = targetLevelDistribution.sample();
        }
      }
    }

    return null; // TODO

  }

  private int nextId() {
    return idCount++;
  }

}
