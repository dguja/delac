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
import java.util.Random;

public class KMeansDemo {

  private static final String TEST_FILENAME = "data/texture.txt";

  public static void main(String[] args) throws IOException {
    List<IClusterable> vectors = getVectors(TEST_FILENAME);
    IQualityMeasure qualityMeasure = QualityType.DB_INDEX.getQualityMeasure();
    
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
      System.out.println("Broj klastera:" + i + " Quality = " + resultQuality);
      for (ICluster cluster : clusters) {
        System.out.println("Velicina klastera: " + cluster.getN());
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
