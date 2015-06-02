package hr.fer.zemris.composite.cluster;

import hr.fer.zemris.composite.cluster.clusterable.IClusterable;
import hr.fer.zemris.composite.cluster.reliability.ReliabilityImpactCalculator;
import hr.fer.zemris.composite.generator.ConfigParser;
import hr.fer.zemris.composite.generator.ModelGenerator;
import hr.fer.zemris.composite.generator.exception.GeneratorException;
import hr.fer.zemris.composite.generator.exception.ParseException;
import hr.fer.zemris.composite.generator.model.Dataset;
import hr.fer.zemris.composite.generator.random.RandomProvider;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

  private static final String CONFIG_FILE = "dataset.json";

  public static void main(final String[] args) throws IOException {
    final long millis = 1433259412646L;//System.currentTimeMillis();
    RandomProvider.getRandom().setSeed(millis);

    System.err.println("Using generator seed: " + millis + ".");

    final ConfigParser parser = parseConfiguration();

    final ModelGenerator generator =
        new ModelGenerator(parser.getModelCount(), parser.getCopyInputs(),
            parser.getDiscreteDistributions(), parser.getRealDistributions());

    final Dataset dataset = generateDataset(generator);

    List<IClusterable> vectors = new ArrayList<>(ReliabilityImpactCalculator.calculate(dataset));

    StringBuilder sb = new StringBuilder();
    for (IClusterable vector : vectors) {
      for (int i = 0; i < vector.getDimension(); ++i) {
        double value = vector.get(i);
        sb.append(value + " ");
      }
      sb.append("\n");
    }

    Files.write(Paths.get("data/dataset.txt"), sb.toString().getBytes());
  }

  private static ConfigParser parseConfiguration() throws IOException, FileNotFoundException {
    final ConfigParser parser = new ConfigParser();

    System.err.println("Parsing configuration file \"" + CONFIG_FILE + "\"...");

    try (final InputStream inputStream = new FileInputStream(CONFIG_FILE)) {
      parser.parse(inputStream);
    } catch (final ParseException exception) {
      exit(exception.getMessage());
    }

    return parser;
  }

  private static Dataset generateDataset(final ModelGenerator generator) {
    System.err.println("Generating dataset...");

    try {
      return generator.generate();
    } catch (final GeneratorException exception) {
      exit(exception.getMessage());
    }

    return null;
  }

  private static void exit(final String message) {
    System.err.println(message);
    System.exit(1);
  }

}
