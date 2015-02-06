package hr.fer.zemris.composite.generator.parser;

import hr.fer.zemris.composite.generator.ModelGenerator;
import hr.fer.zemris.composite.generator.distribution.DiscreteDistributionAdapter;
import hr.fer.zemris.composite.generator.distribution.IDiscreteDistribution;
import hr.fer.zemris.composite.generator.distribution.IRealDistribution;
import hr.fer.zemris.composite.generator.distribution.RealDistributionAdapter;
import hr.fer.zemris.composite.generator.distribution.RealDistributionLimiter;

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
  public static ModelGenerator parse(String json) {
    Map<String, IDiscreteDistribution> discreteDistributionsMap = new HashMap<>();

    Map<String, IRealDistribution> realDistributionsMap = new HashMap<>();

    JsonParser parser = new JsonParser();
    JsonObject root = parser.parse(json).getAsJsonObject();
    int modelCount = root.get("modelCount").getAsInt();
    JsonArray discreteDistributions = root.get("discreteDistributions").getAsJsonArray();
    JsonArray realDistributions = root.get("realDistributions").getAsJsonArray();

    for (int i = 0, size = discreteDistributions.size(); i < size; i++) {
      JsonObject current = discreteDistributions.get(i).getAsJsonObject();
      String name = current.get("name").getAsString();
      JsonObject distribution = current.get("distribution").getAsJsonObject();
      String type = distribution.get("type").getAsString();
      JsonObject parameters = distribution.get("parameters").getAsJsonObject();
      if (type.equals("binomial")) {
        int n = parameters.get("n").getAsInt();
        double p = parameters.get("p").getAsDouble();

        discreteDistributionsMap.put(name, new DiscreteDistributionAdapter(
            new BinomialDistribution(n, p)));
      }
    }

    for (int i = 0, size = realDistributions.size(); i < size; i++) {
      JsonObject current = realDistributions.get(i).getAsJsonObject();
      String name = current.get("name").getAsString();
      JsonObject distribution = current.get("distribution").getAsJsonObject();
      String type = distribution.get("type").getAsString();

      JsonArray range = null;
      int leftBound = 0;
      int rightBound = 0;
      if (distribution.has("range")) {
        range = distribution.get("range").getAsJsonArray();
        leftBound = range.get(0).getAsInt();
        rightBound = range.get(1).getAsInt();
      }

      JsonObject parameters = distribution.get("parameters").getAsJsonObject();
      if (type.equals("normal")) {
        int a = parameters.get("a").getAsInt();
        double d = parameters.get("d").getAsDouble();

        IRealDistribution dist;
        dist = new RealDistributionAdapter(new NormalDistribution(a, d));
        if (range != null) {
          dist = new RealDistributionLimiter(dist, leftBound, rightBound);
        }
        realDistributionsMap.put(name, dist);
      }
    }
    return new ModelGenerator(modelCount, discreteDistributionsMap, realDistributionsMap);
  }

}
