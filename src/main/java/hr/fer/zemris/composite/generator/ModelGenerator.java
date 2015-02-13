package hr.fer.zemris.composite.generator;

import hr.fer.zemris.composite.generator.distribution.IntegerDistributionLimiter;
import hr.fer.zemris.composite.generator.exception.GeneratorException;
import hr.fer.zemris.composite.generator.model.AbstractNode;
import hr.fer.zemris.composite.generator.model.Dataset;
import hr.fer.zemris.composite.generator.model.Model;
import hr.fer.zemris.composite.generator.model.NodeType;
import hr.fer.zemris.composite.generator.model.nodes.BranchNode;
import hr.fer.zemris.composite.generator.model.nodes.InputNode;
import hr.fer.zemris.composite.generator.model.nodes.LoopNode;
import hr.fer.zemris.composite.generator.model.nodes.OutputNode;
import hr.fer.zemris.composite.generator.model.nodes.ParallelNode;
import hr.fer.zemris.composite.generator.model.nodes.SequenceNode;
import hr.fer.zemris.composite.generator.random.RandomUtilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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

    this.modelCount = modelCount;
    this.copyInputs = copyInputs;
    this.discreteDistributions = discreteDistributions;
    this.realDistributions = realDistributions;
  }

  public Dataset generate() {
    idCount = 0;

    // 1
    final int inputNodeCount = discreteDistributions.get("inputNodeCount").sample();

    final List<InputNode> inputs = new ArrayList<>();
    for (int i = 0; i < inputNodeCount; i++) {
      inputs.add(new InputNode(nextId(), realDistributions.get("initialReliability").sample()));
    }

    final IntegerDistribution usedInputCountDistribution =
        new IntegerDistributionLimiter(discreteDistributions.get("usedInputCount"), 0, inputNodeCount);

    final List<Model> models = new ArrayList<>();
    for (int i = 0; i < modelCount; i++) {
      models.add(generateModel(inputs, usedInputCountDistribution));
    }

    return new Dataset(models);
  }

  private Model
      generateModel(final List<InputNode> datasetInputs, final IntegerDistribution usedInputCountDistribution) {
    // TODO test levelNumber distribution
    // TODO test levelNodeCount distribution

    // 2
    final List<InputNode> originalInputs = RandomUtilities.choose(datasetInputs, usedInputCountDistribution.sample());

    final List<InputNode> inputs = new ArrayList<>();
    for (int i = 0; i < originalInputs.size(); i++) {
      inputs.add(copyInputs ? originalInputs.get(i).clone() : originalInputs.get(i));
    }

    // 3
    final int levelCount = discreteDistributions.get("levelCount").sample();

    final List<Map<AbstractNode, Integer>> edges = new ArrayList<>();
    for (int i = 0; i < levelCount; i++) {
      edges.add(new HashMap<AbstractNode, Integer>());
    }

    List<AbstractNode> levelNodes = null;
    final List<AbstractNode> nodes = new ArrayList<>();

    for (int i = 0; i < levelCount - 1; i++) {
      if (i == 0) {
        levelNodes = new ArrayList<AbstractNode>(inputs);
      } else {
        // 6
        final List<AbstractNode> previousLevelNodes = levelNodes;
        levelNodes = createNodes(discreteDistributions.get("levelNodeCount").sample(), i);

        // 7
        connectNodes(levelNodes, i, levelCount, edges.get(i), previousLevelNodes);
      }

      nodes.addAll(levelNodes);

      // 4, 5
      // ako je na k - 2 razini, to je razina neposredno ispod izlaznog cvora, a oni svi moraju biti
      // jednom vezom povezani s izlaznim cvorom pa nije potrebno generirati veze
      if (i < levelCount - 2) {
        createEdges(levelNodes, i, levelCount, edges);
      }
    }

    // 8
    final OutputNode output = new OutputNode(nextId(), levelCount - 1);

    // sve s K - 2 razine povezi s izlaznim jednom vezom
    for (final AbstractNode parent : levelNodes) {
      output.addParent(parent);
    }

    for (final AbstractNode parent : edges.get(levelCount - 1).keySet()) {
      output.addParent(parent);
    }

    for (int i = nodes.size(); i >= 0; i--) {
      final AbstractNode node = nodes.get(i);
      if (node.getChildren().isEmpty()) {
        node.clearParents();
      }
    }

    return new Model(inputs, output);
  }

  private void connectNodes(final List<AbstractNode> nodes, final int currentLevel, final int levelNumber,
      final Map<AbstractNode, Integer> levelEdges, final List<AbstractNode> previousLevelNodes) {
    // number L
    final int nodeCount = nodes.size();
    // number P
    final int edgeCount = calculateEdgeCount(levelEdges, nodeCount);

    if (nodeCount == edgeCount) {
      pairNodes(nodes, levelEdges, nodeCount);
    } else if (nodeCount < edgeCount) {
      final int generatorBehavior = discreteDistributions.get("generatorBehaviorLesser").sample();

      switch (generatorBehavior) {
        case 1:
          nodes.addAll(createNodes(edgeCount - nodeCount, currentLevel));
          pairNodes(nodes, levelEdges, edgeCount);

          break;

        case 2:
          final Set<AbstractNode> connectedChildren = new HashSet<>();

          for (final AbstractNode parent : levelEdges.keySet()) {
            final int choosedEdges = RandomUtilities.getRandomInt(levelEdges.get(parent));
            final List<AbstractNode> choosedChildren = RandomUtilities.choose(nodes, choosedEdges);

            connectedChildren.addAll(choosedChildren);
            for (int i = 0; i < choosedEdges; i++) {
              choosedChildren.get(i).addParent(parent);
            }
          }

          setAll(nodes, connectedChildren);

          break;

        case 3:
          final List<AbstractNode> parents = new ArrayList<>();

          for (final Map.Entry<AbstractNode, Integer> entry : levelEdges.entrySet()) {
            for (int i = entry.getValue(); i >= 0; i--) {
              parents.add(entry.getKey());
            }
          }

          final List<AbstractNode> choosedParents = RandomUtilities.choose(parents, nodeCount);
          for (int i = 0; i < nodeCount; i++) {
            nodes.get(i).addParent(choosedParents.get(i));
          }

          break;

        default:
          throw new GeneratorException("'generatorBehaviorLesser' distribution returned invalid value: "
              + generatorBehavior + ".");
      }
    } else {
      final int generatorBehavior = discreteDistributions.get("generatorBehaviorGreater").sample();

      switch (generatorBehavior) {
        case 1:
          final int previousNodeCount = previousLevelNodes.size();

          // stvori novih l-p veza
          for (int i = nodeCount - edgeCount; i >= 0; i--) {
            addEdge(levelEdges, previousLevelNodes.get(RandomUtilities.getRandomInt(previousNodeCount)));
          }

          pairNodes(nodes, levelEdges, nodeCount);

          break;

        case 2:
          setAll(nodes, pairNodes(nodes, levelEdges, edgeCount));

          break;

        default:
          throw new GeneratorException("'generatorBehaviorGreater' distribution returned invalid value: "
              + generatorBehavior + ".");
      }
    }
  }

  /**
   * Obraduje slucaj kada je L == P
   * 
   * @param nodes cvorovi koji su na trenutnoj razini, spajaju se s roditeljima koji pokazuju na
   *          trenutnu razinu
   * @param levelEdges veze
   * @param currentLevel
   * @param nodeCount
   * @return
   */
  private List<AbstractNode> pairNodes(final List<AbstractNode> nodes, final Map<AbstractNode, Integer> levelEdges,
      final int nodeCount) {
    final List<AbstractNode> choosed = RandomUtilities.choose(nodes, nodeCount);
    int index = 0;

    for (final AbstractNode parent : levelEdges.keySet()) {
      for (int i = levelEdges.get(parent); i >= 0; i--) {
        choosed.get(index).addParent(parent);
        index++;
      }
    }

    return choosed;
  }

  private int calculateEdgeCount(final Map<AbstractNode, Integer> levelEdges, final int childrenNodeCount) {
    int totalCount = 0;

    for (final AbstractNode parent : levelEdges.keySet()) {
      final int count = Math.min(levelEdges.get(parent), childrenNodeCount);
      levelEdges.put(parent, count);

      totalCount += count;
    }

    return totalCount;
  }

  private void createEdges(final List<AbstractNode> nodes, final int currentLevel, final int levelCount,
      final List<Map<AbstractNode, Integer>> edges) {
    final IntegerDistribution targetLevelDistribution =
        new IntegerDistributionLimiter(discreteDistributions.get("targetLevel"), 1, levelCount - currentLevel);

    for (final AbstractNode node : nodes) {
      // 4
      final int edgeCount = discreteDistributions.get(node.getType().getDistributionName()).sample();

      // 5
      for (int j = 0; j < edgeCount; j++) {
        final int targetLevel = currentLevel + targetLevelDistribution.sample();
        addEdge(edges.get(targetLevel), node);
      }

      addEdge(edges.get(currentLevel + 1), node);
    }
  }

  /**
   * Stvara novu vezu.
   * 
   * @param levelEdges lista koja cuva sve trenutne veze
   * @param targetLevel razina na koju pokazuje veza
   * @param node roditelj veze
   */
  private void addEdge(final Map<AbstractNode, Integer> levelEdges, final AbstractNode node) {
    int previousValue = 0;

    if (levelEdges.containsKey(node)) {
      previousValue = levelEdges.get(node);
    }

    levelEdges.put(node, previousValue + 1);
  }

  private List<AbstractNode> createNodes(final int nodeCount, final int level) {
    final List<AbstractNode> nodes = new ArrayList<>(nodeCount);

    for (int i = 0; i < nodeCount; i++) {
      nodes.add(newInstance(level));
    }

    return nodes;
  }

  private AbstractNode newInstance(final int level) {
    final NodeType nodeType = NodeType.get(discreteDistributions.get("nodeType").sample());
    final int id = nextId();

    final AbstractNode node;
    switch (nodeType) {
      case BRANCH:
        node = new BranchNode(id, level, realDistributions.get("branchProbability"));
        break;

      case SEQUENCE:
        node = new SequenceNode(id, level);
        break;

      case PARALLEL:
        node = new ParallelNode(id, level, discreteDistributions.get("paralleParameter"));
        break;

      case LOOP:
        node = new LoopNode(id, level);
        break;

      default:
        throw new GeneratorException("'nodeType' distribution returned invalid node type: " + nodeType + ".");
    }

    node.setWeight(realDistributions.get("nodeWeight").sample());

    return node;
  }

  private int nextId() {
    return idCount++;
  }

  private static <T> void setAll(final Collection<T> destination, final Collection<T> source) {
    destination.clear();
    destination.addAll(source);
  }

}
