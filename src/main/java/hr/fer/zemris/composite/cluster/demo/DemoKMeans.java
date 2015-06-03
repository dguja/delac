package hr.fer.zemris.composite.cluster.demo;

import hr.fer.zemris.composite.cluster.ICluster;
import hr.fer.zemris.composite.cluster.algorithm.IAlgorithm;
import hr.fer.zemris.composite.cluster.algorithm.kmeans.KMeans;
import hr.fer.zemris.composite.cluster.clusterable.IClusterable;
import hr.fer.zemris.composite.cluster.clusterable.Vector;
import hr.fer.zemris.composite.cluster.distance.DistanceType;
import hr.fer.zemris.composite.cluster.quality.IQualityMeasure;
import hr.fer.zemris.composite.cluster.quality.QualityType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DemoKMeans {

  public static void main(String[] args) throws IOException {
    List<IClusterable> vectors = getVectors(Constants.TEST + ".txt");
    IQualityMeasure qualityMeasure = QualityType.SQUARED_DIST_SUM.getQualityMeasure();
    IAlgorithm algorithm = new KMeans(DistanceType.EUCLID, QualityType.SQUARED_DIST_SUM, 7, 300);
    List<ICluster> clusters = algorithm.cluster(vectors);
    double resultQuality = qualityMeasure.measure(clusters);
    System.out.println("Quality = " + resultQuality);
//    for (ICluster cluster : clusters) {
//      System.out.println("Grupa:");
//      for (IClusterable vector : cluster.getPoints()) {
//        System.out.println("  " + vector);
//      }
//      System.out.println();
//    }
    
    int sum = 0;
    for (ICluster cluster : clusters) {
      sum += cluster.getN();
    }
    
    System.out.printf("BROJ TOCAKA: %d, BROJ KLASTERA: %d\n", sum, clusters.size());
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
