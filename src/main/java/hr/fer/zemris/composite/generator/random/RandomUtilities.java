package hr.fer.zemris.composite.generator.random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomUtilities {

  public static <T> List<T> choose(final List<T> list, final int n) {
    final List<T> result = new ArrayList<>(list);

    if (list.isEmpty()) {
      return result;
    }

    Collections.shuffle(result, RandomProvider.getRandom());

    for (int i = list.size() - 1; i >= n; i--) {
      result.remove(i);
    }

    return result;
  }

  public static int getRandomInt(final int rightBound) {
    return RandomProvider.getRandom().nextInt(rightBound);
  }

}
