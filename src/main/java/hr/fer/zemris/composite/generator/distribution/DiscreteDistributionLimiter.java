package hr.fer.zemris.composite.generator.distribution;

/**
 * 
 * Ograničava distribuciju na <code>[leftBound, rightBound></code>.
 * 
 * @author Antun Razum
 */
public class DiscreteDistributionLimiter implements IDiscreteDistribution {

  private final IDiscreteDistribution distribution;

  private final int leftBound;

  private final int rightBound;

  public DiscreteDistributionLimiter(final IDiscreteDistribution distribution, final int leftBound, final int rightBound) {
    super();

    this.distribution = distribution;
    this.leftBound = leftBound;
    this.rightBound = rightBound;
  }

  @Override
  public int sample() {
    int result = leftBound - 1;

    while (result < leftBound || result >= rightBound) {
      result = distribution.sample();
    }

    return result;
  }

}
