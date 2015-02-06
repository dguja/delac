package hr.fer.zemris.composite.generator.distribution;

import org.apache.commons.math3.distribution.AbstractRealDistribution;

public class RealDistributionAdapter implements IRealDistribution {

  private final AbstractRealDistribution distribution;

  public RealDistributionAdapter(final AbstractRealDistribution distribution) {
    super();
    this.distribution = distribution;
  }

  @Override
  public double sample() {
    return distribution.sample();
  }

}
