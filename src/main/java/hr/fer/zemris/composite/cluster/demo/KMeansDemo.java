package hr.fer.zemris.composite.cluster.demo;

import hr.fer.zemris.composite.cluster.ICluster;
import hr.fer.zemris.composite.cluster.algorithm.IAlgorithm;
import hr.fer.zemris.composite.cluster.algorithm.kmeans.KMeans;
import hr.fer.zemris.composite.cluster.clusterable.IClusterable;
import hr.fer.zemris.composite.cluster.clusterable.Vector;
import hr.fer.zemris.composite.cluster.quality.IQualityMeasure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class KMeansDemo {

  private static final String TEST_FILENAME = "data/texture.txt";

  public static void main(String[] args) throws IOException {
    List<IClusterable> vectors = getVectors(TEST_FILENAME);
    IQualityMeasure qualityMeasure = Constants.QUALITY_TYPE.getQualityMeasure();
    IAlgorithm algorithm = new KMeans(Constants.DISTANCE_TYPE, Constants.QUALITY_TYPE, 300);
    List<ICluster> clusters = algorithm.cluster(vectors, Constants.CLUSTER_NUM);
    double resultQuality = qualityMeasure.measure(clusters);
    System.out.println("Quality = " + resultQuality);
    // for (ICluster cluster : clusters) {
    // System.out.println("Grupa:");
    // for (IClusterable vector : cluster.getPoints()) {
    // System.out.println("  " + vector);
    // }
    // System.out.println();
    // }

    int sum = 0;
    for (ICluster cluster : clusters) {
      sum += cluster.getN();
    }

    System.out.printf("BROJ TOCAKA: %d, BROJ KLASTERA: %d\n", sum, clusters.size());

//    KMeans.calulateOptimalK(DistanceType.NORMED_EUCLID, QualityType.SQUARED_DIST_SUM, vectors);
  
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
}
