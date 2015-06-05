package hr.fer.zemris.composite.cluster.demo;

import hr.fer.zemris.composite.cluster.ICluster;
import hr.fer.zemris.composite.cluster.algorithm.bfr.Cluster;
import hr.fer.zemris.composite.cluster.clusterable.Vector;
import hr.fer.zemris.composite.cluster.quality.IQualityMeasure;
import hr.fer.zemris.composite.cluster.quality.QualityType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class RealDemo {

  public static void main(String[] args) throws IOException {
    Cluster[] clusters = new Cluster[20];

    for (String line : Files.readAllLines(Paths.get(Constants.TEST + ".mod"))) {
      String[] parts = line.split(",");

      Vector point = new Vector(parts.length - 1);
      for (int i = 0; i < parts.length - 1; ++i)
        point.set(i, Double.parseDouble(parts[i]));

      Cluster cluster = clusters[Integer.parseInt(parts[parts.length - 1])];
      cluster = Cluster.merge(cluster, new Cluster(point));
      clusters[Integer.parseInt(parts[parts.length - 1])] = cluster;
    }

    List<ICluster> newClusters = new ArrayList<>();

    for (int i = 0; i < 15; ++i) {
      if (clusters[i] != null) {
        newClusters.add(clusters[i]);
      }
    }
    
    int sum = 0;
    for (ICluster cluster : newClusters) {
      sum += cluster.getN();
    }

    System.out.printf("BROJ TOCAKA: %d, BROJ KLASTERA: %d\n", sum, newClusters.size());
    
    IQualityMeasure qualityMeasure = Constants.QUALITY_TYPE.getQualityMeasure();

    double resultQuality = qualityMeasure.measure(newClusters);
    System.out.println("Quality = " + resultQuality);

  }
}
