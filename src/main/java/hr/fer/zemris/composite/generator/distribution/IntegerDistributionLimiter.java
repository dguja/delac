package hr.fer.zemris.composite.generator.distribution;

import org.apache.commons.math3.distribution.AbstractIntegerDistribution;
import org.apache.commons.math3.distribution.IntegerDistribution;
import org.apache.commons.math3.exception.OutOfRangeException;

/**
 * 
 * Ograničava distribuciju na <code>[leftBound, rightBound]</code>.
 * 
 * @author Antun Razum
 */
public class IntegerDistributionLimiter extends AbstractIntegerDistribution {

  private static final long serialVersionUID = 657037031392180138L;

  private final IntegerDistribution distribution;

  private final int leftBound;

  private final int rightBound;

  private final double sum;

  private final double leftSum;

  public IntegerDistributionLimiter(final IntegerDistribution distribution, final int leftBound, final int rightBound) {
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
  public double cumulativeProbability(final int x) {
    return inRange(x) ? (distribution.cumulativeProbability(x) - leftSum) / sum : 0;
  }

  @Override
  public int inverseCumulativeProbability(final double p) throws OutOfRangeException {
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
  public int getSupportLowerBound() {
    return leftBound;
  }

  @Override
  public int getSupportUpperBound() {
    return rightBound;
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
  public double probability(final int x) {
    return inRange(x) ? distribution.probability(x) / sum : 0.;
  }

  private boolean inRange(final int x) {
    return x >= leftBound && x <= rightBound;
  }

}
