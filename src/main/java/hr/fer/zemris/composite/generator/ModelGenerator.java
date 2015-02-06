package hr.fer.zemris.composite.generator;

import hr.fer.zemris.composite.generator.distribution.DiscreteDistributionLimiter;
import hr.fer.zemris.composite.generator.distribution.IDiscreteDistribution;
import hr.fer.zemris.composite.generator.distribution.IRealDistribution;
import hr.fer.zemris.composite.generator.nodes.InputNode;

import java.util.ArrayList;
import java.util.List;

public class ModelGenerator {

  /**
   * N - distribucija odabira broja atomarnih komponenti
   */
  private IDiscreteDistribution distD1;

  /**
   * M - distribucija odabira atomarnih komponenti koje sudjeluju u izgradnji modela
   */
  private IDiscreteDistribution distD2;

  /**
   * P1 - distribucija odabira početne pouzdanosti čvora
   */
  private IRealDistribution distP1;

  public List<Model> generate() {
    final int n = distD1.sample();

    final List<InputNode> inputs = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      inputs.add(new InputNode(distP1.sample()));
    }

    final IDiscreteDistribution distM = new DiscreteDistributionLimiter(distD2, 0, n + 1);

    final List<Model> models = new ArrayList<>();

    return models;
  }

  private Model generateModel(final List<InputNode> inputs, final IDiscreteDistribution distM) {

    return null; // TODO
  }
}
