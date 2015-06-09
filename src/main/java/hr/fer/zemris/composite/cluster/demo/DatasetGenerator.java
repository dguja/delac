package hr.fer.zemris.composite.cluster.demo;

import hr.fer.zemris.composite.cluster.clusterable.Vector;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DatasetGenerator {

  private static final String CONFIG_FILE = "dataset.json";

  private static Integer NUM_DIMENSIONS = 5;

  private static final double LEFT_BOUND = 0.1;

  private static final double RIGHT_BOUND = 0.4;

  public static void main(final String[] args) throws IOException, ClassNotFoundException {
    final long millis = System.currentTimeMillis();//1433773091091L; // 1433757802806L;
    RandomProvider.getRandom().setSeed(millis);

    System.err.println("Using generator seed: " + millis + ".");

    final ConfigParser parser = parseConfiguration();

    final ModelGenerator generator =
        new ModelGenerator(parser.getModelCount(), parser.getCopyInputs(),
            parser.getDiscreteDistributions(), parser.getRealDistributions());

    final Dataset dataset = generateDataset(generator);
    System.err.println("Dataset generated.");

    System.err.println("Calculating reliability vectors...");
    List<Vector> allVectors = ReliabilityImpactCalculator.calculate(dataset);
    System.err.println("Finished calculating.");

    System.err.println("Removing vectors...");
    List<Vector> vectors = new ArrayList<>();
    List<Integer> dimensions = new ArrayList<>();
    for (int i = 0; i < dataset.getInputNodeCount(); ++i) {
      dimensions.add(i);
    }

    double[] minValue = new double[dataset.getInputNodeCount()];
    double[] maxValue = new double[dataset.getInputNodeCount()];

    Arrays.fill(minValue, Double.MAX_VALUE);
    Arrays.fill(maxValue, Double.MIN_VALUE);

    for (Vector vector : allVectors) {
      for (int i = 0; i < dimensions.size(); ++i) {
        minValue[i] = Math.min(minValue[i], vector.get(i));
        maxValue[i] = Math.max(maxValue[i], vector.get(i));
      }
    }

    Collections.sort(dimensions, new Comparator<Integer>() {

      @Override
      public int compare(Integer i, Integer j) {
        return -Double.compare(maxValue[i] - minValue[i], maxValue[j] - minValue[j]);
      }

    });

    int[][] count = new int[dataset.getInputNodeCount()][20];

    NUM_DIMENSIONS = dataset.getInputNodeCount();
    for (Vector vector : allVectors) {
      boolean good = true;
      for (int i = 0; i < dimensions.size(); ++i) {
        int dimension = dimensions.get(i);
        double value = vector.get(dimension);

        double lo = minValue[dimension] + (maxValue[dimension] - minValue[dimension]) * LEFT_BOUND;
        double hi = minValue[dimension] + (maxValue[dimension] - minValue[dimension]) * RIGHT_BOUND;

        double range = maxValue[dimension] - minValue[dimension];
        ++count[i][(int) (((value - minValue[dimension]) / range - (1E-11)) * 20)];

        if (i < NUM_DIMENSIONS) {
          if (value > lo && value < hi) {
            good = false;
          }
        }

        if (i >= NUM_DIMENSIONS) {
          lo = minValue[dimension] + (maxValue[dimension] - minValue[dimension]) * 0.3;
          hi = minValue[dimension] + (maxValue[dimension] - minValue[dimension]) * 0.7;

          if (value < lo || value >= hi) {
            good = false;
          }
        }
      }

      if (good) {
        vectors.add(vector);
      }
    }
    System.err.println("Vectors removed.");

    System.out.printf("          ");
    for (int i = 0; i < 20; ++i) {
      System.out.printf("%5d ", i);
    }
    System.out.println();
    for (int i = 0; i < dataset.getInputNodeCount(); ++i) {
      int dimension = i;//dimensions.get(i);
      System.out.printf("%5f: ", maxValue[dimension] - minValue[dimension]);
      for (int j = 0; j < 20; ++j) {
        System.out.printf("%5d ", count[dimension][j]);
      }
      System.out.println();

    }

    int[] countMask = new int[1 << NUM_DIMENSIONS];

    for (Vector vector : vectors) {
      int mask = 0;
      for (int dimension = 0; dimension < NUM_DIMENSIONS; ++dimension) {
        double value = vector.get(dimension);

        double lo = minValue[dimension] + (maxValue[dimension] - minValue[dimension]) * LEFT_BOUND;
        double hi = minValue[dimension] + (maxValue[dimension] - minValue[dimension]) * RIGHT_BOUND;

        if (value > hi) {
          mask |= (1 << dimension);
        }
      }
      ++countMask[mask];
    }

    System.out.println();
    for (int mask = 0; mask < (1 << NUM_DIMENSIONS); ++mask) {
      if (countMask[mask] == 0) {
        continue;
      }

      System.out.printf("MASKA %d %d\n", mask, countMask[mask]);
    }

    StringBuilder sb = new StringBuilder();
    for (Vector vector : vectors) {
      for (int i = 0; i < vector.getDimension(); ++i) {
        sb.append(vector.get(i));
        sb.append(' ');
      }
      sb.append('\n');
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
