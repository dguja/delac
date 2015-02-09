package hr.fer.zemris.composite.generator.distribution;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.exception.OutOfRangeException;

/**
 * 
 * Ograničava distribuciju na <code>[leftBound, rightBound]</code>.
 * 
 * @author Antun Razum
 */
public class RealDistributionLimiter extends AbstractRealDistribution {

  private static final long serialVersionUID = 3869285722225751711L;

  private final RealDistribution distribution;

  private final double leftBound;

  private final double rightBound;

  private final double sum;

  private final double leftSum;

  public RealDistributionLimiter(final RealDistribution distribution, final double leftBound, final double rightBound) {
    super(null);

    if (leftBound > rightBound) {
      throw new IllegalArgumentException("Left bound is greater than right.");
    }

    this.distribution = distribution;
    this.leftBound = leftBound;
    this.rightBound = rightBound;

    this.leftSum = distribution.cumulativeProbability(leftBound) - distribution.probability(leftBound);
    this.sum = distribution.cumulativeProbability(rightBound) - leftSum;
  }

  @Override
  public double density(final double x) {
    return inRange(x) ? distribution.density(x) / sum : 0.;
  }

  @Override
  public double cumulativeProbability(final double x) {
    return inRange(x) ? (distribution.cumulativeProbability(x) - leftSum) / sum : 0;
  }

  @Override
  public double inverseCumulativeProbability(final double p) throws OutOfRangeException {
    if (p < 0.0 || p > 1.0) {
      throw new OutOfRangeException(p, 0.0, 1.0);
    }

    return distribution.inverseCumulativeProbability(leftSum + p * sum);
  }

  @Override
  public double getNumericalMean() {
    throw new UnsupportedOperationException();
  }

  @Override
  public double getNumericalVariance() {
    throw new UnsupportedOperationException();
  }

  @Override
  public double getSupportLowerBound() {
    return leftBound;
  }

  @Override
  public double getSupportUpperBound() {
    return rightBound;
  }

  @Override
  public boolean isSupportLowerBoundInclusive() {
    return leftBound != Double.NEGATIVE_INFINITY;
  }

  @Override
  public boolean isSupportUpperBoundInclusive() {
    return rightBound != Double.POSITIVE_INFINITY;
  }

  @Override
  public boolean isSupportConnected() {
    throw new UnsupportedOperationException("Unknown for distribution limiter.");
  }

  @Override
  public void reseedRandomGenerator(final long seed) {
    distribution.reseedRandomGenerator(seed);
  }

  @Override
  public double probability(final double x) {
    return inRange(x) ? distribution.probability(x) / sum : 0.;
  }

  private boolean inRange(final double x) {
    return x >= leftBound && x <= rightBound;
  }

}
