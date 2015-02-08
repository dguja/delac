package hr.fer.zemris.composite.generator;

import hr.fer.zemris.composite.generator.distribution.DiscreteDistributionAdapter;
import hr.fer.zemris.composite.generator.distribution.DiscreteDistributionLimiter;
import hr.fer.zemris.composite.generator.distribution.IDiscreteDistribution;
import hr.fer.zemris.composite.generator.distribution.IRealDistribution;
import hr.fer.zemris.composite.generator.distribution.RealDistributionAdapter;
import hr.fer.zemris.composite.generator.distribution.RealDistributionLimiter;
import hr.fer.zemris.composite.generator.exception.ParseException;
import hr.fer.zemris.composite.generator.random.RandomProvider;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.distribution.AbstractIntegerDistribution;
import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Razred koji parsira config.json datoteku formatiranu u dogovorenom formatu. <br>
 * Podržani tipovi distribucija:
 * <ul>
 * <li>binomial</li>
 * <li>enumerated</li>
 * <li>normal</li>
 * <li>exponential</li>
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
    final Map<String, IDiscreteDistribution> discreteDistributionsMap = new HashMap<>();

    final Map<String, IRealDistribution> realDistributionsMap = new HashMap<>();

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

    IRealDistribution p1 = realDistributionsMap.get("p1");
    for (int i = 0; i < 100; i++) {
      System.out.println(p1.sample());
    }

    return new ModelGenerator(modelCount, copyInputs, discreteDistributionsMap,
        realDistributionsMap);
  }

  private static void putDiscreteDistribution(
      final Map<String, IDiscreteDistribution> discreteDistributionsMap, final JsonObject current) {
    final String name = current.get("name").getAsString();
    final JsonObject distribution = current.get("distribution").getAsJsonObject();
    final String type = distribution.get("type").getAsString();

    Bound bound = null;
    if (distribution.has("range")) {
      JsonArray range = distribution.get("range").getAsJsonArray();
      bound = new Bound(range.get(0).getAsInt(), range.get(1).getAsInt());
    }

    final JsonObject parameters = distribution.get("parameters").getAsJsonObject();
    IDiscreteDistribution dist = null;
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

        final JsonArray vArray = parameters.get("v").getAsJsonArray();
        final int[] v = new int[vArray.size()];
        fillIntArray(vArray, v);

        final JsonArray pArray = parameters.get("p").getAsJsonArray();
        final double[] p = new double[pArray.size()];
        fillDoubleArray(pArray, p);

        dist =
            wrapDiscreteDistribution(
                new EnumeratedIntegerDistribution(RandomProvider.getGenerator(), v, p), bound);
        break;
      }
      default: {
        throw new ParseException("Distribution type not supported: " + type);
      }
    }
    discreteDistributionsMap.put(name, dist);
  }

  private static void fillIntArray(final JsonArray vArray, final int[] v) {
    for (int i = 0, size = vArray.size(); i < size; i++) {
      v[i] = vArray.get(i).getAsInt();
    }
  }

  private static void fillDoubleArray(final JsonArray pArray, final double[] p) {
    for (int i = 0, size = pArray.size(); i < size; i++) {
      p[i] = pArray.get(i).getAsDouble();
    }
  }

  private static void putRealDistribution(
      final Map<String, IRealDistribution> realDistributionsMap, final JsonObject current) {
    final String name = current.get("name").getAsString();

    final JsonObject distribution = current.get("distribution").getAsJsonObject();
    final String type = distribution.get("type").getAsString();

    Bound bound = null;
    if (distribution.has("range")) {
      JsonArray range = distribution.get("range").getAsJsonArray();
      bound = new Bound(range.get(0).getAsInt(), range.get(1).getAsInt());
    }

    final JsonObject parameters = distribution.get("parameters").getAsJsonObject();
    IRealDistribution dist = null;
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
      default: {
        throw new ParseException("Distribution type not supported: " + type);
      }
    }
    realDistributionsMap.put(name, dist);
  }

  private static IDiscreteDistribution wrapDiscreteDistribution(
      AbstractIntegerDistribution distribution, Bound bound) {
    IDiscreteDistribution dist = new DiscreteDistributionAdapter(distribution);
    if (bound == null) {
      return dist;
    }
    return new DiscreteDistributionLimiter(dist, bound.getLeftBound(), bound.getRightBound());
  }

  private static IRealDistribution wrapRealDistribution(AbstractRealDistribution distribution,
      Bound bound) {
    IRealDistribution dist = new RealDistributionAdapter(distribution);
    if (bound == null) {
      return dist;
    }
    return new RealDistributionLimiter(dist, bound.getLeftBound(), bound.getRightBound());
  }

  private static class Bound {

    private int leftBound;

    private int rightBound;

    public Bound(int leftBound, int rightBound) {
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
