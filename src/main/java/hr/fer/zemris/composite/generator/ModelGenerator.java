package hr.fer.zemris.composite.generator;

import hr.fer.zemris.composite.generator.distribution.IntegerDistributionLimiter;
import hr.fer.zemris.composite.generator.model.AbstractNode;
import hr.fer.zemris.composite.generator.model.Dataset;
import hr.fer.zemris.composite.generator.model.Model;
import hr.fer.zemris.composite.generator.model.NodeType;
import hr.fer.zemris.composite.generator.model.nodes.InputNode;
import hr.fer.zemris.composite.generator.model.nodes.OutputNode;
import hr.fer.zemris.composite.generator.random.RandomProvider;
import hr.fer.zemris.composite.generator.random.RandomUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.distribution.IntegerDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

public class ModelGenerator {

  private final int modelCount;

  private final boolean copyInputs;

  private final Map<String, IntegerDistribution> discreteDistributions;

  private final Map<String, RealDistribution> realDistributions;

  private int idCount;

  public ModelGenerator(final int modelCount, final boolean copyInputs,
      final Map<String, IntegerDistribution> discreteDistributions,
      final Map<String, RealDistribution> realDistributions) {
    super();

    this.idCount = 0;

    this.modelCount = modelCount;
    this.copyInputs = copyInputs;
    this.discreteDistributions = discreteDistributions;
    this.realDistributions = realDistributions;
  }

  public Dataset generate() {
    // 1
    final int n = discreteDistributions.get("d1").sample();

    final List<InputNode> inputs = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      inputs.add(new InputNode(nextId(), realDistributions.get("p1").sample()));
    }

    final IntegerDistribution mDistribution = new IntegerDistributionLimiter(discreteDistributions.get("d2"), 0, n);

    final List<Model> models = new ArrayList<>();
    for (int i = 0; i < modelCount; i++) {
      models.add(generateModel(inputs, mDistribution));
    }

    return new Dataset(models);
  }

  private Model generateModel(final List<InputNode> datasetInputs, final IntegerDistribution mDistribution) {
    // 2
    final List<InputNode> originals = RandomUtilities.choose(datasetInputs, mDistribution.sample());

    final List<InputNode> inputs = new ArrayList<>();
    for (int i = 0; i < originals.size(); i++) {
      inputs.add(copyInputs ? originals.get(i).clone() : originals.get(i));
    }

    // 3
    final int k = discreteDistributions.get("d3").sample();

    final List<Map<AbstractNode, Integer>> levelEdges = new ArrayList<>();
    for (int i = 0; i < k; i++) {
      levelEdges.add(new HashMap<AbstractNode, Integer>());
    }

    List<AbstractNode> nodes = null;
    for (int i = 0; i < k - 1; i++) {
      if (i == 0) {
        nodes = new ArrayList<AbstractNode>(inputs);
      } else {
        // 6
        final List<AbstractNode> previousLevelNodes = nodes;
        nodes = createNodes(discreteDistributions.get("d11").sample(), i);
        // 7
        nodes = connectNodes(nodes, i, k, levelEdges, previousLevelNodes);
      }
      // 4, 5
      /*
       * ako je na k - 2 razini, to je razina neposredno ispod izlaznog cvora, a oni svi moraju biti
       * jednom vezom povezani s izlaznim cvorom pa nije potrebno generirati veze
       */
      if (i < k - 2) {
        createEdges(nodes, i, k, levelEdges);
      }
    }

    // 8
    final OutputNode output = new OutputNode(nextId());
    // sve s K - 2 razine povezi s izlaznim jednom vezom
    for (final AbstractNode parent : nodes) {
      output.addParent(parent);
    }
    for (final AbstractNode parent : levelEdges.get(k - 1).keySet()) {
      output.addParent(parent);
    }

    return new Model(inputs, output);
  }

  private List<AbstractNode> connectNodes(final List<AbstractNode> nodes, final int currentLevel, final int depthK,
      final List<Map<AbstractNode, Integer>> levelEdges, final List<AbstractNode> previousLevelNodes) {

    final int l = nodes.size();
    final int p = calculateNumberOfEdges(levelEdges.get(currentLevel), l);

    if (l == p) {

      lEqualP(nodes, levelEdges, currentLevel, l);
    } else if (l < p) {

      final IntegerDistribution behaviorOfGenerator = discreteDistributions.get("d12");
      switch (behaviorOfGenerator.sample()) {
        case 1:
          nodes.addAll(createNodes(p - l, currentLevel));
          lEqualP(nodes, levelEdges, currentLevel, p);
          break;
        case 2:
          final Map<AbstractNode, Integer> parents = levelEdges.get(currentLevel);
          for (final AbstractNode parent : parents.keySet()) {
            final int nodesToChoose = RandomProvider.getRandom().nextInt(parents.get(parent));
            final List<AbstractNode> choosed = RandomUtilities.choose(nodes, nodesToChoose);
            for (int i = 0; i < nodesToChoose; i++) {
              choosed.get(i).getParents().add(parent);
              parent.getChildren().add(choosed.get(i));
            }
          }
          break;
        case 3:
          final List<AbstractNode> candidates = new ArrayList<>();
          for (final AbstractNode parent : levelEdges.get(currentLevel).keySet()) {
            for (int i = levelEdges.get(currentLevel).get(parent); i >= 0; i--) {
              candidates.add(parent);
            }
          }
          final Set<AbstractNode> connected = new HashSet<>();
          final List<AbstractNode> choosed = RandomUtilities.choose(candidates, l);
          for (int i = 0; i < l; i++) {
            nodes.get(i).getParents().add(choosed.get(i));
            choosed.get(i).getChildren().add(nodes.get(i));
            connected.add(choosed.get(i));
          }

          // provjerit za cvorove koji nisu u connected je li imaju neku vezu prema gore, ako nemaju
          // onda ih izbacit iz modela

      }
    } else {

      final IntegerDistribution behaviorOfGenerator =
          new IntegerDistributionLimiter(discreteDistributions.get("d13"), 0, 2);
      switch (behaviorOfGenerator.sample()) {
        case 1:

          final int sizeOfPreviousNodes = previousLevelNodes.size();
          // stvori novih l-p veza
          for (int i = l - p; i >= 0; i--) {
            putEdgeInLevelEdges(levelEdges, currentLevel,
                previousLevelNodes.get(RandomProvider.getRandom().nextInt(sizeOfPreviousNodes)));
          }
          lEqualP(nodes, levelEdges, currentLevel, l);
          break;
        case 2:

          final Set<AbstractNode> leftNodes = new HashSet<>();
          final Map<AbstractNode, Integer> parents = levelEdges.get(currentLevel);
          for (final AbstractNode parent : parents.keySet()) {
            final List<AbstractNode> choosed = RandomUtilities.choose(nodes, parents.get(parent));
            for (int i = parents.get(parent); i >= 0; i--) {
              leftNodes.add(choosed.get(i));
              choosed.get(i).getParents().add(parent);
              parent.getChildren().add(choosed.get(i));
            }
          }
          // vrati samo one koji imaju roditelja
          return new ArrayList<>(leftNodes);
      }
    }

    return nodes;
  }

  /**
   * Obraduje slucaj kada je L == P
   * 
   * @param nodes cvorovi koji su na trenutnoj razini, spajaju se s roditeljima koji pokazuju na
   *          trenutnu razinu
   * @param levelEdges veze
   * @param currentLevel
   * @param numberOfNodes
   */
  private void lEqualP(final List<AbstractNode> nodes, final List<Map<AbstractNode, Integer>> levelEdges,
      final int currentLevel, int numberOfNodes) {

    final List<AbstractNode> children = new LinkedList<>(nodes);
    final Map<AbstractNode, Integer> parents = levelEdges.get(currentLevel);
    for (final AbstractNode parent : parents.keySet()) {
      for (int i = 0; i < parents.get(parent); i++) {
        final AbstractNode child = children.remove(RandomProvider.getRandom().nextInt(numberOfNodes--));
        child.getParents().add(parent);
        parent.getChildren().add(child);
      }
    }
  }

  private int calculateNumberOfEdges(final Map<AbstractNode, Integer> map, final int numberOfChildrenNodes) {
    int counter = 0;
    for (final AbstractNode node : map.keySet()) {
      int numberOfEdges = map.get(node);
      if (numberOfEdges > numberOfChildrenNodes) {
        numberOfEdges = numberOfChildrenNodes;
      }
      counter += numberOfEdges;
    }
    return counter;
  }

  private void createEdges(final List<AbstractNode> nodes, final int currentLevel, final int depthK,
      final List<Map<AbstractNode, Integer>> levelEdges) {

    final IntegerDistribution targetLevelDistribution =
        new IntegerDistributionLimiter(discreteDistributions.get("d9"), 1, depthK - currentLevel);

    for (final AbstractNode node : nodes) {
      // 4
      final int edgeCount = discreteDistributions.get(node.getType().getDistributionName()).sample();
      // 5
      for (int j = 0; j < edgeCount; j++) {
        final int targetLevel = currentLevel + targetLevelDistribution.sample();
        putEdgeInLevelEdges(levelEdges, targetLevel, node);
      }
      putEdgeInLevelEdges(levelEdges, currentLevel + 1, node);
    }
  }

  /**
   * Stvara novu vezu.
   * 
   * @param levelEdges lista koja cuva sve trenutne veze
   * @param targetLevel razina na koju pokazuje veza
   * @param node roditelj veze
   */
  private void putEdgeInLevelEdges(final List<Map<AbstractNode, Integer>> levelEdges, final int targetLevel,
      final AbstractNode node) {
    final Map<AbstractNode, Integer> targetEdges = levelEdges.get(targetLevel);

    int previousValue = 0;
    if (targetEdges.containsKey(node)) {
      previousValue = targetEdges.get(node);
    }

    targetEdges.put(node, previousValue + 1);
  }

  private List<AbstractNode> createNodes(final int numberOfNodes, final int level) {
    final List<AbstractNode> nodes = new ArrayList<>(numberOfNodes);

    final IntegerDistribution nodeTypeDistribution = discreteDistributions.get("d10");

    for (int i = 0; i < numberOfNodes; i++) {
      nodes.add(newInstance(level));
    }
    return nodes;
  }

  private AbstractNode newInstance(final int level) {
    final NodeType nodeType = NodeType.get(discreteDistributions.get("d10").sample());

    final int id = nextId();

    final AbstractNode node = null; // TODO makni null
    switch (nodeType) {
      case BRANCH:
        break;

      case SEQUENCE:
        break;

      case PARALLEL:
        break;

      case LOOP:
        break;

      default:
        // TODO exception
        break;
    }

    node.setWeight(realDistributions.get("p2").sample());

    return node;
  }

  private int nextId() {
    return idCount++;
  }

}
