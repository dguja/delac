package hr.fer.zemris.composite.generator.distribution;

/**
 * 
 * Ograničava distribuciju na <code>[leftBound, rightBound></code>.
 * 
 * @author Antun Razum
 */
public class RealDistributionLimiter implements IRealDistribution {

  private final IRealDistribution distribution;

  private final double leftBound;

  private final double rightBound;

  public RealDistributionLimiter(final IRealDistribution distribution, final double leftBound, final double rightBound) {
    super();

    this.distribution = distribution;
    this.leftBound = leftBound;
    this.rightBound = rightBound;
  }

  @Override
  public double sample() {
    double result = leftBound - 1;

    while (result < leftBound || result >= rightBound) {
      result = distribution.sample();
    }

    return result;
  }

}
