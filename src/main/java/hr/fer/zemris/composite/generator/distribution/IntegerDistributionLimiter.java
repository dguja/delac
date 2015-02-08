package hr.fer.zemris.composite.generator.distribution;

import org.apache.commons.math3.distribution.AbstractIntegerDistribution;
import org.apache.commons.math3.distribution.IntegerDistribution;

/**
 * 
 * Ograničava distribuciju na <code>[leftBound, rightBound></code>.
 * 
 * @author Antun Razum
 */
public class IntegerDistributionLimiter extends AbstractIntegerDistribution {

  private final IntegerDistribution distribution;

  private final int leftBound;

  private final int rightBound;

  public IntegerDistributionLimiter(final IntegerDistribution distribution, final int leftBound, final int rightBound) {
    super(); // TODO

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

  @Override
  public double probability(final int x) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double cumulativeProbability(final int x) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double getNumericalMean() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double getNumericalVariance() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int getSupportLowerBound() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int getSupportUpperBound() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public boolean isSupportConnected() {
    // TODO Auto-generated method stub
    return false;
  }

}
