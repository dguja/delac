package hr.fer.zemris.composite.generator.distribution;

import org.apache.commons.math3.distribution.AbstractIntegerDistribution;

public class DiscreteDistributionAdapter implements IDiscreteDistribution {

  private final AbstractIntegerDistribution distribution;

  public DiscreteDistributionAdapter(final AbstractIntegerDistribution distribution) {
    super();

    this.distribution = distribution;
  }

  @Override
  public int sample() {
    return distribution.sample();
  }

}
