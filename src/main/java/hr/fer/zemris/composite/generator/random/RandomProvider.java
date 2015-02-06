package hr.fer.zemris.composite.generator.random;

import java.util.Random;

import org.apache.commons.math3.random.RandomAdaptor;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well44497b;

public class RandomProvider {

  private static RandomGenerator GENERATOR = new Well44497b();

  private static Random RANDOM = new RandomAdaptor(GENERATOR);

  public static RandomGenerator getGenerator() {
    return GENERATOR;
  }

  public static Random getRandom() {
    return RANDOM;
  }

  public static void setSeed(final int seed) {
    GENERATOR.setSeed(seed);
  }

}
