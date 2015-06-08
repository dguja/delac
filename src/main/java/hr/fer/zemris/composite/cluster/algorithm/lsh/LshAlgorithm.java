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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LshAlgorithm implements IAlgorithm {
  
  /**
   * Number of hashing functions.
   */
  private static final int FUNCTION_COUNT = 5;
  
  /**
   * Vector similarity threshold. Lower threshold produces more false positives and less false
   * negatives - more precise, but slower.
   */
  private static final double BANDING_THRESHOLD = .5;
  
  /**
   * Ratio between parameter used in finding candidate pairs and actual parameter of distance
   * measure. Lower parameter produces more false positives and less false negatives - more precise,
   * but slower.
   */
  private static final double PARAMETER_RATIO = .5;

  /**
   * Parameter to use for clustering.
   */
  private static final double PARAMETER = .12875;

  /**
   * Maximum ratio of clusterables belonging to no buckets.
   */
  private static final double RESIDUAL_RATIO = .025;
  
  /**
   * Number of repetitions for best parameter.
   */
  private static final int TEST_COUNT = 100;
  
  /**
   * Number of iterations in binary search.
   */
  private static final int ITERATION_COUNT = 15;

  /**
   * Number of repeated calculations for each step of binary search.
   */
  private static final int REPETITION_COUNT = 50;
  
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
  public List<ICluster> cluster(final List<IClusterable> clusterables, final int k) {
    return clusterRepeat(toLshClusterables(clusterables), k, PARAMETER);
  }

  public List<ICluster> clusterRepeat(final List<IClusterable> clusterables, final int k, final double parameter) {
    double minQuality = Double.MAX_VALUE;
    List<ICluster> result = null;
    
    for (int i = 0; result == null || i < TEST_COUNT; i++) {
      final List<ICluster> current = clusterFixed(clusterables, parameter);
      
      System.out.printf("%2d", current.size());
      
      if (current.size() == k) {
        final double currentQuality = qualityMeasure.measure(current);
        
        System.out.printf(", %.5f", +currentQuality);
        
        if (currentQuality < minQuality) {
          minQuality = currentQuality;
          result = current;
        }
      }
      
      System.out.println();
    }
    
    return result;
  }
  
  public List<ICluster> clusterSearch(final List<IClusterable> clusterables, final int k) {
    final List<IClusterable> lshClusterables = toLshClusterables(clusterables);

    final int dimension = lshClusterables.get(0).getDimension();
    final double diameter = calculateDiameter(lshClusterables, dimension);
    
    double leftBound = 0;
    double rightBound = diameter;
    
    for (int i = 0; i < ITERATION_COUNT; i++) {
      final double middle = (leftBound + rightBound) / 2;
      
      int sizeSum = 0;
      
      for (int j = 0; j < REPETITION_COUNT; j++) {
        final List<ICluster> result = clusterFixed(lshClusterables, middle);
        sizeSum += result.size();
        
        System.out.printf("%.5f, %2d\n", middle, result.size());
      }
      
      if (sizeSum > REPETITION_COUNT * k) {
        leftBound = middle;
      } else {
        rightBound = middle;
      }
    }
    
    return clusterRepeat(lshClusterables, k, leftBound);
  }

  private List<ICluster> clusterFixed(final List<IClusterable> clusterables, final double parameter) {
    final Map<IClusterable, ClusterableData> clusterBucketLists =
        clusterables.stream().collect(
            Collectors.toMap(Function.identity(), ClusterableData::new, (a, b) -> b, HashMap::new));
    
    final BucketStorage storage = computeBucketStorage(clusterables, parameter / PARAMETER_RATIO);
    storage.getBuckets().forEach(
        bucket -> bucket.forEach(clusterable -> clusterBucketLists.get(clusterable).add(bucket)));
    
    final List<IClusterable> sorted =
        clusterBucketLists.values().stream().sorted().map(ClusterableData::getClusterable).collect(Collectors.toList());
    final int clusterablesSize = clusterables.size();

    final List<IClusterable> centers = new ArrayList<>();
    final Set<IClusterable> used = new HashSet<>();
    
    for (final IClusterable center : sorted) {
      if (clusterablesSize - used.size() < RESIDUAL_RATIO * clusterablesSize) {
        break;
      }
      
      if (!used.contains(center)) {
        centers.add(center);

        for (final Set<IClusterable> bucket : clusterBucketLists.get(center).bucketList) {
          for (final Iterator<IClusterable> iterator = bucket.iterator(); iterator.hasNext();) {
            final IClusterable other = iterator.next();
            
            if (distanceMeasure.measure(center, other) < parameter) {
              if (!used.contains(other)) {
                used.add(other);
              }
              
              iterator.remove();
            }
          }
        }
      }
    }

    final Map<IClusterable, List<IClusterable>> clusters =
        centers.stream().collect(
            Collectors.toMap(Function.identity(), x -> new ArrayList<IClusterable>(), (a, b) -> b, HashMap::new));
    
    for (final IClusterable clusterable : clusterables) {
      double minDistance = Double.MAX_VALUE;
      IClusterable best = null;

      for (final IClusterable center : centers) {
        final double current = distanceMeasure.measure(clusterable, center);
        if (current < minDistance) {
          minDistance = current;
          best = center;
        }
      }

      clusters.get(best).add(clusterable);
    }
    
    return clusters.values().stream().map(Cluster::new).collect(Collectors.toList());
  }
  
  private static List<IClusterable> toLshClusterables(final List<IClusterable> clusterables) {
    return clusterables.stream().map(LshClusterable::new).collect(Collectors.toList());
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
  
  private BucketStorage computeBucketStorage(final List<IClusterable> clusterables, final double parameter) {
    final int bandCount = calculateBandCount(BANDING_THRESHOLD, FUNCTION_COUNT);
    final int dimension = clusterables.get(0).getDimension();

    final IHashCalculator calculator = producer.produce(FUNCTION_COUNT, dimension, parameter, new Random());
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
  
  private static class ClusterableData implements Comparable<ClusterableData> {
    
    private final IClusterable clusterable;
    
    private final List<Set<IClusterable>> bucketList = new ArrayList<>();
    
    private int approximateSize;

    public ClusterableData(final IClusterable clusterable) {
      super();
      
      this.clusterable = clusterable;
    }
    
    public void add(final Set<IClusterable> bucket) {
      bucketList.add(bucket);
      approximateSize += bucket.size();
    }
    
    @Override
    public int compareTo(final ClusterableData other) {
      return -(approximateSize - other.approximateSize);
    }
    
    public IClusterable getClusterable() {
      return clusterable;
    }
    
  }
  
  @FunctionalInterface
  private static interface ICalculatorProducer {
    
    IHashCalculator produce(int functionCount, int dimension, double parameter, Random random);
    
  }
  
}
