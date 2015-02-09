package hr.fer.zemris.composite.generator;

import hr.fer.zemris.composite.generator.distribution.IntegerDistributionLimiter;
import hr.fer.zemris.composite.generator.distribution.RealDistributionLimiter;
import hr.fer.zemris.composite.generator.exception.ParseException;
import hr.fer.zemris.composite.generator.random.RandomProvider;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.distribution.AbstractIntegerDistribution;
import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;
import org.apache.commons.math3.distribution.EnumeratedRealDistribution;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.GeometricDistribution;
import org.apache.commons.math3.distribution.IntegerDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Razred koji parsira config.json datoteku formatiranu u dogovorenom formatu. <br>
 * Podržani tipovi distribucija:<br>
 * 
 * diskretne:
 * <ul>
 * <li>binomial</li>
 * <li>enumerated</li>
 * <li>geometric</li>
 * <li>poisson</li>
 * <li>uniform</li>
 * </ul>
 * <br>
 * realne:
 * <ul>
 * <li>normal</li>
 * <li>exponential</li>
 * <li>enumerated</li>
 * <li>uniform</li>
 * </ul>
 * 
 * @author Mislav Magerl
 */
public class ConfigParser {

  /**
   * Vraća {@link ModelGenerator} popunjenog s podacima iz <code>json</code>a.
   * 
   * 
   * @param json JSON konfiguracijska datoteka generatora.
   * @return {@link ModelGenerator}
   */
  public static ModelGenerator parse(final String json) {
    final Map<String, IntegerDistribution> discreteDistributionsMap = new HashMap<>();

    final Map<String, RealDistribution> realDistributionsMap = new HashMap<>();

    final JsonParser parser = new JsonParser();

    final JsonObject root = parser.parse(json).getAsJsonObject();

    final int modelCount = root.get("modelCount").getAsInt();

    final boolean copyInputs = root.get("copyInputs").getAsBoolean();

    final JsonArray discreteDistributions = root.get("discreteDistributions").getAsJsonArray();
    for (int i = 0, size = discreteDistributions.size(); i < size; i++) {
      final JsonObject current = discreteDistributions.get(i).getAsJsonObject();
      putDiscreteDistribution(discreteDistributionsMap, current);
    }

    final JsonArray realDistributions = root.get("realDistributions").getAsJsonArray();
    for (int i = 0, size = realDistributions.size(); i < size; i++) {
      final JsonObject current = realDistributions.get(i).getAsJsonObject();
      putRealDistribution(realDistributionsMap, current);
    }

    return new ModelGenerator(modelCount, copyInputs, discreteDistributionsMap,
        realDistributionsMap);
  }

  private static int[] getIntArray(JsonArray jsonArray) {
    int[] array = new int[jsonArray.size()];
    for (int i = 0, size = jsonArray.size(); i < size; i++) {
      array[i] = jsonArray.get(i).getAsInt();
    }
    return array;
  }

  private static double[] getDoubleArray(JsonArray jsonArray) {
    double[] array = new double[jsonArray.size()];
    for (int i = 0, size = jsonArray.size(); i < size; i++) {
      array[i] = jsonArray.get(i).getAsDouble();
    }
    return array;
  }

  private static void putDiscreteDistribution(
      final Map<String, IntegerDistribution> discreteDistributionsMap, final JsonObject current) {
    final String name = current.get("name").getAsString();
    final JsonObject distribution = current.get("distribution").getAsJsonObject();
    final String type = distribution.get("type").getAsString();

    Bound bound = null;
    if (distribution.has("range")) {
      final JsonArray range = distribution.get("range").getAsJsonArray();
      bound = new Bound(range.get(0).getAsInt(), range.get(1).getAsInt());
    }

    final JsonObject parameters = distribution.get("parameters").getAsJsonObject();
    IntegerDistribution dist = null;
    switch (type) {
      case "binomial": {
        final int n = parameters.get("n").getAsInt();
        final double p = parameters.get("p").getAsDouble();

        dist =
            wrapDiscreteDistribution(new BinomialDistribution(RandomProvider.getGenerator(), n, p),
                bound);
        break;
      }
      case "enumerated": {

        final int[] v = getIntArray(parameters.get("v").getAsJsonArray());

        final double[] p = getDoubleArray(parameters.get("p").getAsJsonArray());

        dist =
            wrapDiscreteDistribution(
                new EnumeratedIntegerDistribution(RandomProvider.getGenerator(), v, p), bound);
        break;
      }
      case "geometric": {

        final double p = parameters.get("p").getAsDouble();
        dist =
            wrapDiscreteDistribution(new GeometricDistribution(RandomProvider.getGenerator(), p),
                bound);
        break;
      }
      case "poisson": {

        final double lambda = parameters.get("lambda").getAsDouble();
        dist =
            wrapDiscreteDistribution(new PoissonDistribution(RandomProvider.getGenerator(), lambda,
                PoissonDistribution.DEFAULT_EPSILON, PoissonDistribution.DEFAULT_MAX_ITERATIONS),
                bound);
        break;
      }
      case "uniform": {

        final int lower = parameters.get("lower").getAsInt();
        final int upper = parameters.get("upper").getAsInt();
        dist =
            wrapDiscreteDistribution(new UniformIntegerDistribution(RandomProvider.getGenerator(),
                lower, upper), bound);
        break;
      }
      default: {
        throw new ParseException("Distribution type not supported: " + type);
      }
    }
    discreteDistributionsMap.put(name, dist);
  }

  private static void putRealDistribution(final Map<String, RealDistribution> realDistributionsMap,
      final JsonObject current) {
    final String name = current.get("name").getAsString();

    final JsonObject distribution = current.get("distribution").getAsJsonObject();
    final String type = distribution.get("type").getAsString();

    Bound bound = null;
    if (distribution.has("range")) {
      final JsonArray range = distribution.get("range").getAsJsonArray();
      bound = new Bound(range.get(0).getAsInt(), range.get(1).getAsInt());
    }

    final JsonObject parameters = distribution.get("parameters").getAsJsonObject();
    RealDistribution dist = null;
    switch (type) {
      case "normal": {
        final int a = parameters.get("a").getAsInt();
        final double d = parameters.get("d").getAsDouble();

        dist =
            wrapRealDistribution(new NormalDistribution(RandomProvider.getGenerator(), a, d), bound);
        break;
      }
      case "exponential": {
        final double lambda = parameters.get("lambda").getAsDouble();

        dist =
            wrapRealDistribution(
                new ExponentialDistribution(RandomProvider.getGenerator(), lambda), bound);

        break;
      }
      case "enumerated": {

        final double[] v = getDoubleArray(parameters.get("v").getAsJsonArray());

        final double[] p = getDoubleArray(parameters.get("p").getAsJsonArray());

        dist =
            wrapRealDistribution(
                new EnumeratedRealDistribution(RandomProvider.getGenerator(), v, p), bound);
        break;
      }
      case "uniform": {

        final double lower = parameters.get("lower").getAsDouble();
        final double upper = parameters.get("upper").getAsDouble();
        dist =
            wrapRealDistribution(new UniformRealDistribution(RandomProvider.getGenerator(), lower,
                upper), bound);
        break;
      }
      default: {
        throw new ParseException("Distribution type not supported: " + type);
      }
    }
    realDistributionsMap.put(name, dist);
  }

  private static IntegerDistribution wrapDiscreteDistribution(
      final AbstractIntegerDistribution distribution, final Bound bound) {
    if (bound == null) {
      return distribution;
    }
    return new IntegerDistributionLimiter(distribution, bound.getLeftBound(), bound.getRightBound());
  }

  private static RealDistribution wrapRealDistribution(final AbstractRealDistribution distribution,
      final Bound bound) {
    if (bound == null) {
      return distribution;
    }
    return new RealDistributionLimiter(distribution, bound.getLeftBound(), bound.getRightBound());
  }

  private static class Bound {

    private final int leftBound;

    private final int rightBound;

    public Bound(final int leftBound, final int rightBound) {
      super();
      this.leftBound = leftBound;
      this.rightBound = rightBound;
    }

    public int getLeftBound() {
      return leftBound;
    }

    public int getRightBound() {
      return rightBound;
    }

  }

}
