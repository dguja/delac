package hr.fer.zemris.composite.cluster.demo;

import hr.fer.zemris.composite.cluster.ICluster;
import hr.fer.zemris.composite.cluster.algorithm.lsh.LshAlgorithm;
import hr.fer.zemris.composite.cluster.clusterable.IClusterable;
import hr.fer.zemris.composite.cluster.clusterable.Vector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LshDemo {

  public static void main(final String[] args) throws IOException {
    final List<IClusterable> vectors = getVectors(Constants.TEST + ".txt");
    final LshAlgorithm algorithm = new LshAlgorithm(Constants.DISTANCE_TYPE, Constants.QUALITY_TYPE);

    final List<ICluster> clusters = algorithm.clusterSearch(vectors, Constants.CLUSTER_NUM);
    System.out.println("k = " + clusters.size() + ", q = "
        + Constants.QUALITY_TYPE.getQualityMeasure().measure(clusters));

    for (final ICluster cluster : clusters) {
      System.out.println(cluster.getN());
    }

    // for (final ICluster cluster : clusters) {
    // System.out.println("Grupa:");
    //
    // for (final IClusterable vector : cluster.getPoints()) {
    // System.out.println("  " + vector);
    // }
    //
    // System.out.println();
    // }
  }

  private static List<IClusterable> getVectors(final String filename) throws IOException {
    final List<String> lines = Files.readAllLines(Paths.get(filename));
    final List<IClusterable> vectors = new ArrayList<>();

    for (final String line : lines) {
      final String[] parts = line.split("\\s+");
      final double[] values = new double[parts.length];

      for (int i = 0; i < parts.length; i++) {
        values[i] = Double.parseDouble(parts[i]);
      }

      vectors.add(new Vector(values));
    }

    return vectors;
  }
}
