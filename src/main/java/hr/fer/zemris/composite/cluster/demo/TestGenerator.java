package hr.fer.zemris.composite.cluster.demo;

import hr.fer.zemris.composite.cluster.ICluster;
import hr.fer.zemris.composite.cluster.algorithm.lsh.Cluster;
import hr.fer.zemris.composite.cluster.clusterable.IClusterable;
import hr.fer.zemris.composite.cluster.clusterable.Vector;
import hr.fer.zemris.composite.cluster.quality.QualityType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class TestGenerator {
  
  private static final int DIMENSION = 4;
  
  private static final ClusterConfiguration[] CLUSTER_CONFIGURATIONS = ClusterConfiguration
      .parseArray("0,0,0,0|800|.2;0,1,0,1|500|.18;1,1,0,0|500|.2;0,0,1,1|250|.2;1,1,1,1|400|.25");

  private static final double SPACE_SIZE = 1;

  private static final double CENTROID_MOVE = .2;
  
  private static final double DEVIATION_RATIO = .5;

  private static final Path DATASET_FILE = Paths.get("data/check.txt");

  private static final Long SEED = 1433765110095l;

  public static void main(final String[] args) throws IOException {
    final long seed = SEED == null ? System.currentTimeMillis() : SEED;
    final Random random = new Random(seed);
    
    System.out.println("Seed: " + seed);

    final Vector spaceCenter = new Vector(DIMENSION);
    for (int i = 0; i < DIMENSION; i++) {
      spaceCenter.set(i, .5);
    }

    final List<ICluster> clusters = new ArrayList<>();
    for (final ClusterConfiguration configuration : CLUSTER_CONFIGURATIONS) {
      final Vector centroid = new Vector(DIMENSION);
      
      for (int i = 0; i < DIMENSION; i++) {
        final int current = configuration.coordinates[i];
        centroid.set(i, current == 0 ? CENTROID_MOVE : 1 - CENTROID_MOVE);
      }

      final double size = configuration.size;
      final double deviation = size * DEVIATION_RATIO;
      
      final List<IClusterable> clusterables = new ArrayList<>();

      for (int i = 0; i < configuration.count; i++) {
        final Vector current = new Vector(DIMENSION);

        do {
          for (int j = 0; j < DIMENSION; j++) {
            current.set(j, centroid.get(j) + (deviation * random.nextGaussian()));
          }
        } while (distance(current, centroid) > size || distance(current, spaceCenter) > .5);
        
        final Vector scaled = new Vector(DIMENSION);
        for (int j = 0; j < DIMENSION; j++) {
          scaled.set(j, SPACE_SIZE * current.get(j));
        }

        clusterables.add(scaled);
      }
      
      clusters.add(new Cluster(clusterables));
    }
    
    System.out.println("Squared dist sum: " + QualityType.SQUARED_DIST_SUM.getQualityMeasure().measure(clusters));
    System.out.println("DB index: " + QualityType.DB_INDEX.getQualityMeasure().measure(clusters));
    
    final List<IClusterable> clusterables =
        clusters.stream().map(ICluster::getPoints).flatMap(List::stream).collect(Collectors.toList());

    final List<String> lines = clusterables.stream().map(TestGenerator::clusterableString).collect(Collectors.toList());
    Files.write(DATASET_FILE, lines);
  }

  private static double distance(final IClusterable first, final IClusterable second) {
    double max = 0;

    for (int i = 0; i < DIMENSION; i++) {
      max = Math.max(max, Math.abs(first.get(i) - second.get(i)));
    }

    return max;
  }
  
  private static String clusterableString(final IClusterable clusterable) {
    final StringBuilder builder = new StringBuilder();
    
    for (int i = 0; i < DIMENSION; i++) {
      builder.append(clusterable.get(i));
      
      if (i < DIMENSION - 1) {
        builder.append(' ');
      }
    }
    
    return builder.toString();
  }
  
  private static class ClusterConfiguration {
    
    private final Integer[] coordinates;
    
    private final int count;
    
    private final double size;
    
    public ClusterConfiguration(final Integer[] coordinates, final int count, final double size) {
      super();
      
      this.coordinates = coordinates;
      this.count = count;
      this.size = size;
    }

    public static ClusterConfiguration parse(final String text) {
      final String[] parts = text.split("\\|");
      return new ClusterConfiguration(parseCoordinates(parts[0]), Integer.parseInt(parts[1]),
          Double.parseDouble(parts[2]));
    }

    public static ClusterConfiguration[] parseArray(final String text) {
      return Arrays.stream(text.split(";")).map(ClusterConfiguration::parse).toArray(ClusterConfiguration[]::new);
    }
    
    private static Integer[] parseCoordinates(final String text) {
      return Arrays.stream(text.split(",")).map(Integer::parseInt).toArray(Integer[]::new);
    }
    
  }
  
}
