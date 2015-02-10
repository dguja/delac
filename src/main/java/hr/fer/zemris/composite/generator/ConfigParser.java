package hr.fer.zemris.composite.generator;

import hr.fer.zemris.composite.generator.distribution.IntegerDistributionLimiter;
import hr.fer.zemris.composite.generator.distribution.RealDistributionLimiter;
import hr.fer.zemris.composite.generator.random.RandomProvider;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
import com.google.gson.JsonElement;
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

  private static final String ERROR_ROOT_PARAMETER = "Error while parsing root parameter. ";

  private static final String ERROR_DISCRETE_DISTRIBUTIONS_PARAMETER =
      "Error while parsing a discrete distribution at position ";

  private static final String ERROR_DISCRETE_DISTRIBUTIONS_NAME =
      "Error while parsing a discrete distribution with name ";

  private static final String ERROR_REAL_DISTRIBUTIONS_PARAMETER =
      "Error while parsing a real distribution at position ";

  private static final String ERROR_REAL_DISTRIBUTIONS_NAME =
      "Error while parsing a real distribution with name ";

  private int modelCount;

  private boolean copyInputs;

  private Map<String, IntegerDistribution> discreteDistributions = new HashMap<>();

  private Map<String, RealDistribution> realDistributions = new HashMap<>();

  /**
   * Fills <code>modelCount</code>, <code>copyInputs</code>, <code>discreteDistributions</code> and
   * <code>realDistributions</code> from <code>inputStream</code> containing a Json file.
   * 
   * @param inputStream {@link InputStream} with a Json file
   */
  public void parse(final InputStream inputStream) {
    final JsonParser parser = new JsonParser();

    final JsonObject root =
        parser.parse(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).getAsJsonObject();

    modelCount = getInt(root, "modelCount", ERROR_ROOT_PARAMETER);

    copyInputs = getBoolean(root, "copyInputs", ERROR_ROOT_PARAMETER);

    if (root.has("discreteDistributions")) {
      final JsonArray discreteDistributions =
          getJsonArray(root, "discreteDistributions", ERROR_ROOT_PARAMETER);
      for (int i = 0, size = discreteDistributions.size(); i < size; i++) {
        final JsonObject current = discreteDistributions.get(i).getAsJsonObject();
        putDiscreteDistribution(current, ERROR_DISCRETE_DISTRIBUTIONS_PARAMETER + i + ". ");
      }
    }

    if (root.has("realDistributions")) {
      final JsonArray realDistributions =
          getJsonArray(root, "realDistributions", ERROR_ROOT_PARAMETER);
      for (int i = 0, size = realDistributions.size(); i < size; i++) {
        final JsonObject current = realDistributions.get(i).getAsJsonObject();
        putRealDistribution(current, ERROR_REAL_DISTRIBUTIONS_PARAMETER + i + ". ");
      }
    }
    if (root.has("discreteDistributions")) {
      final JsonArray discreteDistributions =
          getJsonArray(root, "discreteDistributions", ERROR_ROOT_PARAMETER);
      for (int i = 0, size = discreteDistributions.size(); i < size; i++) {
        final JsonObject current = discreteDistributions.get(i).getAsJsonObject();
        putDiscreteDistribution(current, ERROR_DISCRETE_DISTRIBUTIONS_PARAMETER + i + ". ");
      }
    }

    if (root.has("realDistributions")) {
      final JsonArray realDistributions =
          getJsonArray(root, "realDistributions", ERROR_ROOT_PARAMETER);
      for (int i = 0, size = realDistributions.size(); i < size; i++) {
        final JsonObject current = realDistributions.get(i).getAsJsonObject();
        putRealDistribution(current, ERROR_REAL_DISTRIBUTIONS_PARAMETER + i + ". ");
      }
    }
  }

  private void putDiscreteDistribution(final JsonObject current, final String error) {
    final String name = getString(current, "name", error);
    final JsonObject distribution = getJsonObject(current, "distribution", error);
    final String type = getString(current, "type", error);

    DiscreteBound bound = null;
    if (distribution.has("range")) {
      bound =
          getDiscreteBound(distribution, "range", ERROR_DISCRETE_DISTRIBUTIONS_NAME + name + ". ");
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
    discreteDistributions.put(name, dist);
  }

  private void putRealDistribution(final JsonObject current, String error) {
    final String name = getString(current, "name", error);
    final JsonObject distribution = getJsonObject(current, "distribution", error);
    final String type = getString(current, "type", error);

    RealBound bound = null;
    if (distribution.has("range")) {
      bound = getRealBound(distribution, "range", ERROR_REAL_DISTRIBUTIONS_NAME + name + ". ");
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
    realDistributions.put(name, dist);
  }

  private static int[] getIntArray(final JsonArray jsonArray) {
    final int[] array = new int[jsonArray.size()];
    for (int i = 0, size = jsonArray.size(); i < size; i++) {
      array[i] = jsonArray.get(i).getAsInt();
    }
    return array;
  }

  private static double[] getDoubleArray(final JsonArray jsonArray) {
    final double[] array = new double[jsonArray.size()];
    for (int i = 0, size = jsonArray.size(); i < size; i++) {
      array[i] = jsonArray.get(i).getAsDouble();
    }
    return array;
  }

  private static IntegerDistribution wrapDiscreteDistribution(
      final AbstractIntegerDistribution distribution, final DiscreteBound bound) {
    if (bound == null) {
      return distribution;
    }
    return new IntegerDistributionLimiter(distribution, bound.getLeftBound(), bound.getRightBound());
  }

  private static RealDistribution wrapRealDistribution(final AbstractRealDistribution distribution,
      final RealBound bound) {
    if (bound == null) {
      return distribution;
    }
    return new RealDistributionLimiter(distribution, bound.getLeftBound(), bound.getRightBound());
  }

  private static JsonElement getElement(final JsonObject object, final String name,
      final String error) {
    if (!object.has(name)) {
      throw new ParseException(error + " Element '" + name + "' doesn't exist.");
    }

    return object.get(name);
  }

  private static int getInt(final JsonObject object, final String name, final String error) {
    final JsonElement element = getElement(object, name, error);

    try {
      return element.getAsInt();
    } catch (ClassCastException | IllegalStateException exception) {
      throw new ParseException(error + " Element '" + name + "' is not an integer.", exception);
    }
  }

  private static boolean getBoolean(final JsonObject object, final String name, final String error) {
    final JsonElement element = getElement(object, name, error);

    try {
      return element.getAsBoolean();
    } catch (ClassCastException | IllegalStateException exception) {
      throw new ParseException(error + " Element '" + name + "' is not a boolean.", exception);
    }
  }

  private static String getString(final JsonObject object, final String name, final String error) {
    final JsonElement element = getElement(object, name, error);

    try {
      return element.getAsString();
    } catch (ClassCastException | IllegalStateException exception) {
      throw new ParseException(error + " Element '" + name + "' is not a string.", exception);
    }
  }

  private static JsonObject getJsonObject(final JsonObject object, final String name,
      final String error) {
    final JsonElement element = getElement(object, name, error);

    try {
      return element.getAsJsonObject();
    } catch (ClassCastException | IllegalStateException exception) {
      throw new ParseException(error + " Element '" + name + "' is not a valid Json object.",
          exception);
    }
  }

  private static JsonArray getJsonArray(final JsonObject object, final String name,
      final String error) {
    final JsonElement element = getElement(object, name, error);

    try {
      return element.getAsJsonArray();
    } catch (ClassCastException | IllegalStateException exception) {
      throw new ParseException(error + " Element '" + name + "' is not a valid Json array.",
          exception);
    }
  }

  private static DiscreteBound getDiscreteBound(final JsonObject object, final String name,
      String error) {
    final JsonArray array = getJsonArray(object, name, error);
    if (array.size() != 2) {
      throw new ParseException(error + "Expected two integers, got " + array.size() + ". ");
    }

    DiscreteBound bound = null;
    try {
      bound = new DiscreteBound(array.get(0).getAsInt(), array.get(1).getAsInt());
    } catch (ClassCastException | IllegalStateException exception) {
      throw new ParseException(error + "Didn't get two valid integers.");
    }

    return bound;
  }

  private static RealBound getRealBound(final JsonObject object, final String name, String error) {
    final JsonArray array = getJsonArray(object, name, error);
    if (array.size() != 2) {
      throw new ParseException(error + "Expected two doubles, got " + array.size() + ". ");
    }

    RealBound bound = null;
    try {
      bound = new RealBound(array.get(0).getAsDouble(), array.get(1).getAsDouble());
    } catch (ClassCastException | IllegalStateException exception) {
      throw new ParseException(error + "Didn't get two valid doubles.");
    }

    return bound;
  }

  public int getModelCount() {
    return modelCount;
  }

  public boolean getCopyInputs() {
    return copyInputs;
  }

  public Map<String, IntegerDistribution> getDiscreteDistributions() {
    return discreteDistributions;
  }

  public Map<String, RealDistribution> getRealDistributions() {
    return realDistributions;
  }

  private static class DiscreteBound {

    private final int leftBound;

    private final int rightBound;

    public DiscreteBound(int leftBound, int rightBound) {
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

  private static class RealBound {

    private final double leftBound;

    private final double rightBound;

    public RealBound(double leftBound, double rightBound) {
      super();
      this.leftBound = leftBound;
      this.rightBound = rightBound;
    }

    public double getLeftBound() {
      return leftBound;
    }

    public double getRightBound() {
      return rightBound;
    }

  }

}
