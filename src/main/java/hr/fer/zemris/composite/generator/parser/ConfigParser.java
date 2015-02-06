package hr.fer.zemris.composite.generator.parser;

import hr.fer.zemris.composite.generator.ModelGenerator;
import hr.fer.zemris.composite.generator.distribution.DiscreteDistributionAdapter;
import hr.fer.zemris.composite.generator.distribution.IDiscreteDistribution;
import hr.fer.zemris.composite.generator.distribution.IRealDistribution;
import hr.fer.zemris.composite.generator.distribution.RealDistributionAdapter;
import hr.fer.zemris.composite.generator.distribution.RealDistributionLimiter;
import hr.fer.zemris.composite.generator.random.RandomProvider;

import java.util.HashMap;
import java.util.Map;

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
      final String name = current.get("name").getAsString();
      final JsonObject distribution = current.get("distribution").getAsJsonObject();
      final String type = distribution.get("type").getAsString();
      final JsonObject parameters = distribution.get("parameters").getAsJsonObject();
      if (type.equals("binomial")) {
        final int n = parameters.get("n").getAsInt();
        final double p = parameters.get("p").getAsDouble();

        discreteDistributionsMap.put(name,
            new DiscreteDistributionAdapter(new BinomialDistribution(RandomProvider.getGenerator(), n, p)));
      }
    }

    final JsonArray realDistributions = root.get("realDistributions").getAsJsonArray();
    for (int i = 0, size = realDistributions.size(); i < size; i++) {
      final JsonObject current = realDistributions.get(i).getAsJsonObject();
      final String name = current.get("name").getAsString();
      final JsonObject distribution = current.get("distribution").getAsJsonObject();
      final String type = distribution.get("type").getAsString();

      JsonArray range = null;
      int leftBound = 0;
      int rightBound = 0;
      if (distribution.has("range")) {
        range = distribution.get("range").getAsJsonArray();
        leftBound = range.get(0).getAsInt();
        rightBound = range.get(1).getAsInt();
      }

      final JsonObject parameters = distribution.get("parameters").getAsJsonObject();
      if (type.equals("normal")) {
        final int a = parameters.get("a").getAsInt();
        final double d = parameters.get("d").getAsDouble();

        IRealDistribution dist;
        dist = new RealDistributionAdapter(new NormalDistribution(RandomProvider.getGenerator(), a, d));
        if (range != null) {
          dist = new RealDistributionLimiter(dist, leftBound, rightBound);
        }
        realDistributionsMap.put(name, dist);
      }
    }
    return new ModelGenerator(modelCount, copyInputs, discreteDistributionsMap, realDistributionsMap);
  }

}
