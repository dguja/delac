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
import java.util.Collections;
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
    if (inputNodeCount < 1) {
      throw new GeneratorException("'inputNodeCount' distribution returned a value lesser than 1: " + inputNodeCount
          + ".");
    }

    final List<InputNode> inputs = new ArrayList<>();
    for (int i = 0; i < inputNodeCount; i++) {
      final double initialReliability = realDistributions.get("initialReliability").sample();
      if (initialReliability < 0 || initialReliability > 1) {
        throw new GeneratorException("'initialReliability' distribution returned a value outside [0, 1]: "
            + inputNodeCount + ".");
      }

      inputs.add(new InputNode(nextId(), initialReliability));
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

    // 2
    final int usedInputCount = usedInputCountDistribution.sample();
    if (usedInputCount < 1) {
      throw new GeneratorException("'usedInputCount' distribution returned a value lesser than 1: " + usedInputCount
          + ".");
    }

    final List<InputNode> originalInputs = RandomUtilities.choose(datasetInputs, usedInputCount);

    final List<InputNode> inputs = new ArrayList<>();
    for (int i = 0; i < originalInputs.size(); i++) {
      inputs.add(copyInputs ? originalInputs.get(i).clone() : originalInputs.get(i));
    }

    // 3
    final int levelCount = discreteDistributions.get("levelCount").sample();
    if (levelCount < 2) {
      throw new GeneratorException("'levelCount' distribution returned a value lesser than 2: " + levelCount + ".");
    }

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

        final int levelNodeCount = discreteDistributions.get("levelNodeCount").sample();
        if (levelNodeCount < 1) {
          throw new GeneratorException("'levelNodeCount' distribution returned a value lesser than 1: "
              + levelNodeCount + ".");
        }

        levelNodes = createNodes(levelNodeCount, i);

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

    for (int i = nodes.size() - 1; i >= 0; i--) {
      final AbstractNode node = nodes.get(i);
      if (node.getChildren().isEmpty()) {
        node.clearParents();

        if (node.getLevel() == 0) {
          inputs.remove(node);
        }
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
      final List<AbstractNode> parents = linearizeEdges(levelEdges);

      final int generatorBehavior = discreteDistributions.get("generatorBehaviorLesser").sample();
      switch (generatorBehavior) {
        case 1:
          nodes.addAll(createNodes(edgeCount - nodeCount, currentLevel));
          pairNodes(nodes, levelEdges, edgeCount);

          break;

        case 2:
          final List<AbstractNode> loops = new ArrayList<>();
          for (final AbstractNode node : nodes) {
            if (node.getType() == NodeType.LOOP) {
              loops.add(node);
            }
          }

          final List<AbstractNode> loopParents = RandomUtilities.choose(parents, loops.size());
          for (int i = 0; i < loops.size(); i++) {
            loops.get(i).addParent(loopParents.get(i));
          }

          for (final AbstractNode node : loopParents) {
            parents.remove(parents.indexOf(node));
          }

          final List<AbstractNode> otherNodes = new ArrayList<>(nodes);
          otherNodes.removeAll(loops);

          if (otherNodes.isEmpty()) {
            break;
          }

          List<Integer> indices = new ArrayList<>();
          for (int i = 0; i < parents.size() - 1; i++) {
            indices.add(i);
          }

          indices = RandomUtilities.choose(indices, otherNodes.size() - 1);

          indices.add(-1);
          Collections.sort(indices);
          indices.add(parents.size() - 1);

          for (int i = 0; i < otherNodes.size(); i++) {
            final Set<AbstractNode> currentParents = new HashSet<>();

            for (int j = indices.get(i) + 1; j <= indices.get(i + 1); j++) {
              currentParents.add(parents.get(j));
            }

            for (final AbstractNode parent : currentParents) {
              otherNodes.get(i).addParent(parent);
            }
          }

          break;

        case 3:
          final List<AbstractNode> choosedParents = RandomUtilities.choose(parents, nodeCount);
          for (int i = 0; i < nodeCount; i++) {
            nodes.get(i).addParent(choosedParents.get(i));
          }

          break;

        default:
          throw new GeneratorException("'generatorBehaviorLesser' distribution returned value not in {1, 2, 3}: "
              + generatorBehavior + ".");
      }
    } else {
      final int generatorBehavior = discreteDistributions.get("generatorBehaviorGreater").sample();

      switch (generatorBehavior) {
        case 1:
          final int previousNodeCount = previousLevelNodes.size();

          // stvori novih l-p veza
          for (int i = nodeCount - edgeCount - 1; i >= 0; i--) {
            addEdge(levelEdges, previousLevelNodes.get(RandomUtilities.getRandomInt(previousNodeCount)));
          }

          pairNodes(nodes, levelEdges, nodeCount);

          break;

        case 2:
          setAll(nodes, pairNodes(nodes, levelEdges, edgeCount));

          break;

        default:
          throw new GeneratorException("'generatorBehaviorGreater' distribution returned a value not in {1, 2}: "
              + generatorBehavior + ".");
      }
    }
  }

  private List<AbstractNode> linearizeEdges(final Map<AbstractNode, Integer> levelEdges) {
    final List<AbstractNode> parents = new ArrayList<>();

    for (final Map.Entry<AbstractNode, Integer> entry : levelEdges.entrySet()) {
      for (int i = entry.getValue() - 1; i >= 0; i--) {
        parents.add(entry.getKey());
      }
    }
    return parents;
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
      for (int i = levelEdges.get(parent) - 1; i >= 0; i--) {
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
        new IntegerDistributionLimiter(discreteDistributions.get("targetLevel"), 1, levelCount - currentLevel - 1);

    for (final AbstractNode node : nodes) {
      // 4
      final int edgeCount = discreteDistributions.get(node.getType().getDistributionName()).sample();

      // 5
      for (int j = 0; j < edgeCount; j++) {
        final int targetLevelRelative = targetLevelDistribution.sample();
        if (targetLevelRelative < 1) {
          throw new GeneratorException("'targetLevel' distribution returned a value lesser than 1: "
              + targetLevelRelative + ".");
        }

        final int targetLevel = currentLevel + targetLevelRelative;
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
        node = new ParallelNode(id, level, discreteDistributions.get("parallelParameter"));
        break;

      case LOOP:
        node = new LoopNode(id, level, discreteDistributions.get("repetitionCount").sample());
        break;

      default:
        throw new GeneratorException("'nodeType' distribution returned a value not in {1, 2, 3, 4}: " + nodeType + ".");
    }

    final double weight = realDistributions.get("nodeWeight").sample();
    if (weight < 0 || weight > 1) {
      throw new GeneratorException("'nodeWeight' distribution returned a value outside [0, 1]: " + weight + ".");
    }

    node.setWeight(weight);

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
