package hr.fer.zemris.composite.generator;

import hr.fer.zemris.composite.generator.distribution.DiscreteDistributionAdapter;
import hr.fer.zemris.composite.generator.distribution.DiscreteDistributionLimiter;
import hr.fer.zemris.composite.generator.distribution.IDiscreteDistribution;
import hr.fer.zemris.composite.generator.distribution.IRealDistribution;
import hr.fer.zemris.composite.generator.distribution.RealDistributionAdapter;
import hr.fer.zemris.composite.generator.distribution.RealDistributionLimiter;
import hr.fer.zemris.composite.generator.random.RandomProvider;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.distribution.AbstractIntegerDistribution;
import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Razred koji parsira config.json datoteku formatiranu u dogovorenom formatu. <br>
 * Podržani tipovi distribucija:
 * <ul>
 * <li>binomial</li>
 * <li>normal</li>
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
    for (int i = 0; i < 100; i++){
      System.out.println(p1.sample());
    }
    
    return new ModelGenerator(modelCount, copyInputs, discreteDistributionsMap,
        realDistributionsMap);
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
    switch (type) {
      case "normal":
        final int a = parameters.get("a").getAsInt();
        final double d = parameters.get("d").getAsDouble();

        IRealDistribution dist =
            wrapRealDistribution(new NormalDistribution(RandomProvider.getGenerator(), a, d),
                bound);
        realDistributionsMap.put(name, dist);
        break;
    // default: exception
    }
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

    switch (type) {
      case "binomial":
        final int n = parameters.get("n").getAsInt();
        final double p = parameters.get("p").getAsDouble();

        IDiscreteDistribution dist =
            wrapDiscreteDistribution(
                new BinomialDistribution(RandomProvider.getGenerator(), n, p), bound);
        discreteDistributionsMap.put(name, dist);
        break;
    // default: exception
    }
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
