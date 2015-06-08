package hr.fer.zemris.composite.cluster.demo;

import hr.fer.zemris.composite.cluster.ICluster;
import hr.fer.zemris.composite.cluster.algorithm.IAlgorithm;
import hr.fer.zemris.composite.cluster.algorithm.kmeans.KMeans;
import hr.fer.zemris.composite.cluster.clusterable.IClusterable;
import hr.fer.zemris.composite.cluster.clusterable.Vector;
import hr.fer.zemris.composite.cluster.distance.DistanceType;
import hr.fer.zemris.composite.cluster.quality.IQualityMeasure;
import hr.fer.zemris.composite.cluster.quality.QualityType;
import hr.fer.zemris.composite.cluster.reliability.ReliabilityImpactCalculator;
import hr.fer.zemris.composite.generator.ConfigParser;
import hr.fer.zemris.composite.generator.ModelGenerator;
import hr.fer.zemris.composite.generator.exception.GeneratorException;
import hr.fer.zemris.composite.generator.exception.ParseException;
import hr.fer.zemris.composite.generator.model.Dataset;
import hr.fer.zemris.composite.generator.model.DirectionType;
import hr.fer.zemris.composite.generator.model.Model;
import hr.fer.zemris.composite.generator.random.RandomProvider;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KMeansDemo {
  
  private static String CONFIG_FILE = "dataset.json";

  private static String OUTPUT_DIRECTORY = "other/output/";

  private static final String TEST_FILENAME = "data/dataset.txt";

  public static void main(String[] args) throws IOException {
    
    final long millis = System.currentTimeMillis();
    RandomProvider.getRandom().setSeed(millis);

    System.err.println("Using generator seed: " + millis + ".");

    final ConfigParser parser = parseConfiguration();

    final ModelGenerator generator =
        new ModelGenerator(parser.getModelCount(), parser.getCopyInputs(), parser.getDiscreteDistributions(),
            parser.getRealDistributions());

    final Dataset dataset = generateDataset(generator);
    
    System.err.println("Finished.");

//    for (final Model model : dataset.getModels()) {
//      model.getOutput().calculateReliability(DirectionType.PARENT);
//    }
    
    System.err.println("Racunam vektore pouzdanosti...");
    List<Vector> impacts = ReliabilityImpactCalculator.calculate(dataset);
    storeVectors(impacts);
    System.err.println("Vektori pouzdanosti pohranjeni.");
    
    List<IClusterable> vectors = new ArrayList<>(impacts);
    
    IQualityMeasure qualityMeasure = QualityType.SQUARED_DIST_SUM.getQualityMeasure();
    IQualityMeasure qualityMeasure2 = QualityType.DB_INDEX.getQualityMeasure();
    
//    Random rand = new Random();
//    List<IClusterable> vectors = new ArrayList<>();
//    for(int i = 0; i < 5000; i++) {
//      double[] values = new double[2];
//      values[0] = rand.nextDouble() * 2;
//      values[1] = rand.nextDouble() * 2;
//      vectors.add(new Vector(values));
//    }
//    
//    for(int i = 0; i < 3000; i++) {
//      double[] values = new double[2];
//      values[0] = rand.nextDouble() * 2;
//      values[1] = rand.nextDouble() * (5 - 3) + 3;
//      vectors.add(new Vector(values));
//    }
//    
//    for(int i = 0; i < 4000; i++) {
//      double[] values = new double[2];
//      values[0] = rand.nextDouble() * (5 - 3) + 3;
//      values[1] = rand.nextDouble() * (5 - 3) + 3;
//      vectors.add(new Vector(values));
//    }
//    
//    for(int i = 0; i < 4500; i++) {
//      double[] values = new double[2];
//      values[0] = rand.nextDouble() * (5 - 3) + 3;
//      values[1] = rand.nextDouble() * 2;
//      vectors.add(new Vector(values));
//    }
    
    for(int i = 1; i < 15; i++) {
      IAlgorithm algorithm = new KMeans(DistanceType.EUCLID, Constants.QUALITY_TYPE, 300);
      List<ICluster> clusters = algorithm.cluster(vectors, i);
      double resultQuality = qualityMeasure.measure(clusters);
      double resultQuality2 = qualityMeasure2.measure(clusters);
      System.out.println("Broj klastera: " + i);
      System.out.println("SQUARED DIST SUM = " + resultQuality);
      System.out.println("DB INDEX = " + resultQuality2);
      int clusterCount = 1;
      for (ICluster cluster : clusters) {
        System.out.println("Velicina " + clusterCount + ". klastera: " + cluster.getN());
        ++clusterCount;
      }
      System.out.println();
    }
    
//    IAlgorithm algorithm = new KMeans(DistanceType.EUCLID, Constants.QUALITY_TYPE, 300);
//    List<ICluster> clusters = algorithm.cluster(vectors, 4);
//    double resultQuality = qualityMeasure.measure(clusters);
//    
    
//     for (ICluster cluster : clusters) {
//     System.out.println("Grupa:");
//     for (IClusterable vector : cluster.getPoints()) {
//     System.out.println("  " + vector);
//     }
//     System.out.println();
//     }

//    int sum = 0;
//    for (ICluster cluster : clusters) {
//      System.out.println("Velicina klastera: " + cluster.getN());
//      sum += cluster.getN();
//    }
//
//    System.out.printf("BROJ TOCAKA: %d, BROJ KLASTERA: %d\n", sum, clusters.size());

//    KMeans.calulateOptimalK(DistanceType.NORMED_EUCLID, QualityType.SQUARED_DIST_SUM, vectors);
  
    
    
  }

  private static void storeVectors(List<Vector> vectors) throws IOException {
    StringBuilder sb = new StringBuilder();
    for(Vector v : vectors) {
      for(int i = 0; i < v.getDimension(); ++i) {
        sb.append(v.get(i));
      }
      sb.append("\n");
    }
    Files.write(Paths.get("data/dataset.txt"), sb.toString().getBytes());
  }

  private static List<IClusterable> getVectors(String filename) throws IOException {
    List<String> lines = Files.readAllLines(Paths.get(filename));
    List<IClusterable> vectors = new ArrayList<>();
    for (String line : lines) {
      String[] parts = line.split("\\s+");
      double[] values = new double[parts.length];
      for (int i = 0; i < parts.length; i++) {
        values[i] = Double.parseDouble(parts[i]);
      }
      vectors.add(new Vector(values));
    }
    return vectors;
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
  
  private static void exit(final String message) {
    System.err.println(message);
    System.exit(1);
  }
  
}
