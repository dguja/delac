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
import java.util.Set;
import java.util.stream.Collectors;

public class LshAlgorithm implements IAlgorithm {
  
  /**
   * Number of hashing functions.
   */
  private static final int FUNCTION_COUNT = 10;
  
  /**
   * Number of iterations in ternary search.
   */
  private static final int ITERATION_COUNT = 50;
  
  /**
   * Vector similarity threshold. Lower threshold produces more false positives and less false
   * negatives - more precise, but slower.
   */
  private static final double BANDING_THRESHOLD = .5;

  /**
   * Ratio between parameter used in finding candidate pairs and actual parameter of distance
   * measure. Lower parameter produces more false positives and less false negatives - more precise,
   * but slower. Should be less than 1.
   */
  private static final double PARAMETER_RATIO = .5;
  
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
      final double leftParameter = (2 * leftBound + rightBound) / 3;
      final double rightParameter = (leftBound + 2 * rightBound) / 3;
      
      final List<ICluster> leftResult = clusterFixed(clusterables, leftParameter);
      final List<ICluster> rightResult = clusterFixed(clusterables, rightParameter);

      if (qualityMeasure.measure(leftResult) < qualityMeasure.measure(rightResult)) {
        rightBound = rightParameter;
      } else {
        leftBound = leftParameter;
      }
    }

    return clusterFixed(clusterables, leftBound);
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

    final double maxDistance = parameter * PARAMETER_RATIO;
    
    for (final IClusterable center : clusterables) {
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