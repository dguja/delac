package hr.fer.zemris.composite.generator.distributions;

public class ContinuousDistributionBounder implements IContinuousDistribution {

  private final double leftBound;

  private final double rightBound;

  private final IContinuousDistribution distribution;

  public ContinuousDistributionBounder(final double leftBound, final double rightBound,
      final IContinuousDistribution distribution) {
    super();

    this.leftBound = leftBound;
    this.rightBound = rightBound;
    this.distribution = distribution;
  }

  @Override
  public double nextNumber() {
    double result;

    do {
      result = distribution.nextNumber();
    } while (result < leftBound || result > rightBound);

    return result;
  }
}
