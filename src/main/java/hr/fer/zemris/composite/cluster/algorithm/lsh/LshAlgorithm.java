package hr.fer.zemris.composite.cluster.algorithm.lsh;

import hr.fer.zemris.composite.cluster.ICluster;
import hr.fer.zemris.composite.cluster.algorithm.IAlgorithm;
import hr.fer.zemris.composite.cluster.algorithm.lsh.calculators.EuclideanCalculator;
import hr.fer.zemris.composite.cluster.clusterable.IClusterable;
import hr.fer.zemris.composite.cluster.distance.DistanceType;
import hr.fer.zemris.composite.cluster.distance.IDistanceMeasure;
import hr.fer.zemris.composite.cluster.quality.IQualityMeasure;
import hr.fer.zemris.composite.cluster.quality.QualityType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LshAlgorithm implements IAlgorithm {

  /**
   * Number of hashing functions.
   */
  private static final int FUNCTION_COUNT = 1;

  /**
   * Number of iterations in binary search.
   */
  private static final int ITERATION_COUNT = 10;

  /**
   * Vector similarity threshold. Lower threshold produces more false positives and less false
   * negatives - more precise, but slower.
   */
  private static final double BANDING_THRESHOLD = .1;
  
  /**
   * Ratio between parameter used in finding candidate pairs and actual parameter of distance
   * measure. Lower parameter produces more false positives and less false negatives - more precise,
   * but slower. Should be more than 1.
   */
  private static final double PARAMETER_RATIO = 2;
  
  /**
   * Maximum number of repetitions for greater cluster count in binary search.
   */
  private static final int REPETITION_COUNT = 5;
  
  /**
   * Number of repetitions for best parameter.
   */
  private static final int TEST_COUNT = 100;
  
  // TODO remove this
  private static final int CLUSTER_COUNT = 11;

  private ICalculatorProducer producer;

  private IDistanceMeasure distanceMeasure;

  private IQualityMeasure qualityMeasure;

  public LshAlgorithm(final DistanceType distanceType, final QualityType qualityType) {
    super();

    setDistanceType(distanceType);
    setQualityType(qualityType);
  }

  @Override
  public void setDistanceType(final DistanceType distanceType) {
    distanceMeasure = distanceType.getDistanceMeasure();

    switch (distanceType) {
      case EUCLID:
        producer = EuclideanCalculator::new;
        break;

      default:
        throw new UnsupportedOperationException();
    }
  }

  @Override
  public void setQualityType(final QualityType qualityType) {
    qualityMeasure = qualityType.getQualityMeasure();
  }

  @Override
  public List<ICluster> cluster(final List<IClusterable> clusterables) {
    final int dimension = clusterables.get(0).getDimension();
    final double diameter = calculateDiameter(clusterables, dimension);
    
    double leftBound = 0;
    double rightBound = diameter;
    
    for (int i = 0; i < ITERATION_COUNT; i++) {
      final double middle = (leftBound + rightBound) / 2;

      int size = CLUSTER_COUNT + 1;

      for (int j = 0; j < REPETITION_COUNT; j++) {
        final List<ICluster> result = clusterFixed(clusterables, middle);
        size = result.size();

        System.out.printf("p = %.3f, k = %2d\n", middle, size);
        
        if (size <= CLUSTER_COUNT) {
          break;
        }
      }

      if (size > CLUSTER_COUNT) {
        leftBound = middle;
      } else {
        rightBound = middle;
      }
    }

    double minQuality = Double.MAX_VALUE;
    List<ICluster> result = null;

    for (int i = 0; result == null || i < TEST_COUNT; i++) {
      final List<ICluster> current = clusterFixed(clusterables, leftBound);

      System.out.printf("k = %2d", current.size());

      if (current.size() == CLUSTER_COUNT) {
        final double currentQuality = qualityMeasure.measure(current);

        System.out.printf(", q = %.3f", currentQuality);

        if (currentQuality < minQuality) {
          minQuality = currentQuality;
          result = current;
        }
      }
      
      System.out.println();
    }
    
    return result;
  }

  private static double calculateDiameter(final List<IClusterable> clusterables, final int dimension) {
    final double[] minV = new double[dimension];
    final double[] maxV = new double[dimension];

    for (int i = 0; i < dimension; i++) {
      minV[i] = Double.MAX_VALUE;
      maxV[i] = Double.MIN_VALUE;
    }

    for (final IClusterable clusterable : clusterables) {
      for (int i = 0; i < dimension; i++) {
        final double component = clusterable.get(i);
        
        minV[i] = Math.min(minV[i], component);
        maxV[i] = Math.max(maxV[i], component);
      }
    }
    
    double diameter = 0;
    for (int i = 0; i < dimension; i++) {
      diameter = Math.max(diameter, maxV[i] - minV[i]);
    }
    
    return diameter;
  }

  private List<ICluster> clusterFixed(final List<IClusterable> clusterables, final double parameter) {
    final BucketStorage storage = computeBucketStorage(clusterables, parameter);
    
    final Map<IClusterable, List<Set<IClusterable>>> clusterBucketLists = new HashMap<>();
    for (final IClusterable clusterable : clusterables) {
      clusterBucketLists.put(clusterable, new ArrayList<>());
    }

    final List<Set<IClusterable>> buckets = storage.getBuckets();
    for (final Set<IClusterable> bucket : buckets) {
      for (final IClusterable clusterable : bucket) {
        clusterBucketLists.get(clusterable).add(bucket);
      }
    }
    
    final List<Cluster> clusters = new ArrayList<>();
    final Set<IClusterable> used = new HashSet<>();
    
    final List<IClusterable> permutated = new ArrayList<>(clusterables);
    Collections.shuffle(permutated);

    final double maxDistance = parameter * PARAMETER_RATIO;

    for (final IClusterable center : permutated) {
      if (!used.contains(center)) {
        final Set<IClusterable> cluster = new HashSet<>();
        cluster.add(center);

        for (final Set<IClusterable> bucket : clusterBucketLists.get(center)) {
          for (final Iterator<IClusterable> iterator = bucket.iterator(); iterator.hasNext();) {
            final IClusterable other = iterator.next();
            
            if (distanceMeasure.measure(center, other) < maxDistance) {
              if (!used.contains(other)) {
                cluster.add(other);
                used.add(other);
              }

              iterator.remove();
            }
          }
        }
        
        clusters.add(new Cluster(cluster));
      }
    }
    
    return clusters.stream().map(ICluster.class::cast).collect(Collectors.toList());
  }
  
  private BucketStorage computeBucketStorage(final List<IClusterable> clusterables, final double parameter) {
    final int bandCount = calculateBandCount(BANDING_THRESHOLD, FUNCTION_COUNT);
    final int dimension = clusterables.get(0).getDimension();

    final IHashCalculator calculator = producer.produce(FUNCTION_COUNT, dimension, parameter);
    final BucketStorage storage = new BucketStorage(bandCount, calculator);

    clusterables.forEach(storage::add);
    return storage;
  }

  private static int calculateBandCount(final double threshold, final int functionCount) {
    int result = 1;

    int power;
    for (power = 1; power < functionCount; power <<= 1) {}

    for (power >>= 1; power > 0; power >>= 1) {
      final int current = result + power;

      if (calculateThreshold(current, functionCount) > threshold) {
        result = current;
      }
    }

    return result;
  }

  private static double calculateThreshold(final int bandCount, final int count) {
    return Math.pow(1. / bandCount, (double) bandCount / count);
  }

  @FunctionalInterface
  private interface ICalculatorProducer {

    IHashCalculator produce(int functionCount, int dimension, double parameter);

  }
  
}
