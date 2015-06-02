package hr.fer.zemris.composite.cluster.demo;

import hr.fer.zemris.composite.cluster.ICluster;
import hr.fer.zemris.composite.cluster.algorithm.IAlgorithm;
import hr.fer.zemris.composite.cluster.algorithm.bfr.BFR;
import hr.fer.zemris.composite.cluster.algorithm.hierarchical.Hierarchical;
import hr.fer.zemris.composite.cluster.algorithm.simplebfr.SimpleBFR;
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

public class Demo {

  public static void main(String[] args) throws IOException {
    List<IClusterable> vectors = getVectors("data/dataset.txt");

    IQualityMeasure qualityMeasure = QualityType.SQUARED_DIST_SUM.getQualityMeasure();

    System.out.println("SimpleBFR algoritam");
    IAlgorithm algorithm = new SimpleBFR(DistanceType.EUCLID, QualityType.SQUARED_DIST_SUM);
    List<ICluster> clusters = algorithm.cluster(vectors);
    double resultQuality = qualityMeasure.measure(clusters);
    System.out.println("Quality = " + resultQuality);
    for (ICluster cluster : clusters) {
      System.out.println("Grupa:");
      for (IClusterable vector : cluster.getPoints()) {
        System.out.println("  " + vector);
      }
      System.out.println();
    }

    System.out.println("BFR algoritam");
    algorithm = new BFR(DistanceType.EUCLID, QualityType.SQUARED_DIST_SUM);
    clusters = algorithm.cluster(vectors);
    resultQuality = qualityMeasure.measure(clusters);
    System.out.println("Quality = " + resultQuality);
    for (ICluster cluster : clusters) {
      System.out.println("Grupa:");
      for (IClusterable vector : cluster.getPoints()) {
        System.out.println("  " + vector);
      }
      System.out.println();
    }

    System.out.println("Hierachical algoritam");
    algorithm = new Hierarchical(DistanceType.EUCLID, QualityType.SQUARED_DIST_SUM);
    clusters = algorithm.cluster(vectors);
    resultQuality = qualityMeasure.measure(clusters);
    System.out.println("Quality = " + resultQuality);
    for (ICluster cluster : clusters) {
      System.out.println("Grupa:");
      for (IClusterable vector : cluster.getPoints()) {
        System.out.println("  " + vector);
      }
      System.out.println();
    }
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
