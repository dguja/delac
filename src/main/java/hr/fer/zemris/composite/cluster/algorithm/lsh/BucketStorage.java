package hr.fer.zemris.composite.cluster.algorithm.lsh;

import hr.fer.zemris.composite.cluster.clusterable.IClusterable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BucketStorage {
  
  private final List<Map<Signature, Set<IClusterable>>> storage = new ArrayList<>();

  private final IHashCalculator calculator;
  
  public BucketStorage(final int bandCount, final IHashCalculator calculator) {
    super();

    for (int i = 0; i < bandCount; i++) {
      storage.add(new HashMap<>());
    }

    this.calculator = calculator;
  }
  
  public void add(final IClusterable clusterable) {
    final Signature signature = calculator.hashFunction(clusterable);
    final int bandSize = calculator.getFunctionCount() / storage.size();

    for (int i = 0; i < storage.size(); i++) {
      final Signature fraction = signature.subSignature(i, bandSize);
      final Map<Signature, Set<IClusterable>> fractionBuckets = storage.get(i);

      Set<IClusterable> bucket = fractionBuckets.get(fraction);

      if (bucket == null) {
        bucket = new HashSet<>();
        fractionBuckets.put(fraction, bucket);
      }

      bucket.add(clusterable);
    }
  }
  
  public List<Set<IClusterable>> getBuckets() {
    return storage.stream().map(Map::values).flatMap(Collection::stream).collect(Collectors.toList());
  }

  @Override
  public String toString() {
    return storage.toString();
  }
  
}
