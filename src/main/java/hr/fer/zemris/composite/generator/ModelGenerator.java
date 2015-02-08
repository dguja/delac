package hr.fer.zemris.composite.generator;

import hr.fer.zemris.composite.generator.distribution.DiscreteDistributionLimiter;
import hr.fer.zemris.composite.generator.distribution.IDiscreteDistribution;
import hr.fer.zemris.composite.generator.distribution.IRealDistribution;
import hr.fer.zemris.composite.generator.model.AbstractNode;
import hr.fer.zemris.composite.generator.model.Model;
import hr.fer.zemris.composite.generator.model.NodeType;
import hr.fer.zemris.composite.generator.model.nodes.InputNode;
import hr.fer.zemris.composite.generator.random.RandomProvider;
import hr.fer.zemris.composite.generator.random.RandomUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ModelGenerator {

  private final int modelCount;

  private final boolean copyInputs;

  private final Map<String, IDiscreteDistribution> discreteDistributions;

  private final Map<String, IRealDistribution> realDistributions;

  private int idCount;

  public ModelGenerator(final int modelCount, final boolean copyInputs,
      final Map<String, IDiscreteDistribution> discreteDistributions,
      final Map<String, IRealDistribution> realDistributions) {
    super();

    this.idCount = 0;

    this.modelCount = modelCount;
    this.copyInputs = copyInputs;
    this.discreteDistributions = discreteDistributions;
    this.realDistributions = realDistributions;
  }

  public List<Model> generate() {
    // 1
    final int n = discreteDistributions.get("d1").sample();

    final List<InputNode> inputs = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      inputs.add(new InputNode(nextId(), realDistributions.get("p1").sample()));
    }

    final IDiscreteDistribution mDistribution =
        new DiscreteDistributionLimiter(discreteDistributions.get("d2"), 0, n + 1);

    final List<Model> models = new ArrayList<>();
    for (int i = 0; i < modelCount; i++) {
      models.add(generateModel(inputs, mDistribution));
    }

    return models;
  }

  private Model generateModel(final List<InputNode> datasetInputs, final IDiscreteDistribution mDistribution) {
    // 2
    final List<InputNode> originals = RandomUtilities.choose(datasetInputs, mDistribution.sample());

    final List<AbstractNode> inputs = new ArrayList<>();
    for (int i = 0; i < originals.size(); i++) {
      inputs.add(copyInputs ? originals.get(i).clone() : originals.get(i));
    }

    // 3
    final int k = discreteDistributions.get("d3").sample();

    final List<Map<AbstractNode, Integer>> levelEdges = new ArrayList<>();
    for (int i = 0; i < k; i++) {
      levelEdges.add(new HashMap<AbstractNode, Integer>());
    }

    for (int i = 0; i < k; i++) {
      List<AbstractNode> nodes = null;
      if (i == 0) {
        nodes = inputs;
      } else {
        // 6
        List<AbstractNode> previousLevelNodes = nodes;
        nodes = createNodes(discreteDistributions.get("d11").sample());
        // 7
        nodes = connectNodes(nodes, i, k, levelEdges, previousLevelNodes);
      }
      // 4, 5
      createEdges(nodes, i, k, levelEdges);
    }
    return null; // TODO

  }

  private List<AbstractNode> connectNodes(List<AbstractNode> nodes, int currentLevel, int depthK,
      List<Map<AbstractNode, Integer>> levelEdges, List<AbstractNode> previousLevelNodes) {

    int l = nodes.size();
    int p = calculateNumberOfEdges(levelEdges.get(currentLevel));

    if (l == p) {
      
      lEqualP(nodes, levelEdges, currentLevel, l);
    } else if (l < p) {
      
      IDiscreteDistribution behaviorOfGenerator =
          new DiscreteDistributionLimiter(discreteDistributions.get("d12"), 0, 3);
      switch (behaviorOfGenerator.sample()) {
        case 1:
          nodes.addAll(createNodes(p - l));
          lEqualP(nodes, levelEdges, currentLevel, p);
          break;
        case 2:
          Map<AbstractNode, Integer> parents = levelEdges.get(currentLevel);
          for (AbstractNode parent : parents.keySet()) {
            int nodesToChoose = l;
            List<AbstractNode> children = new LinkedList<>(nodes);
            for (int i = parents.get(parent); i >= 0; i--) {

              // ako roditelj ima vise veza nego djece na koje se moze povezati, odbaci visak veza
              if (i > l) {
                i = l;
              }

              AbstractNode child = children.remove(RandomProvider.getRandom().nextInt(nodesToChoose--));
              child.getParents().add(parent);
              parent.getChildren().add(child);
            }
          }
          break;
        case 3:

      }
    } else {
      
      IDiscreteDistribution behaviorOfGenerator =
          new DiscreteDistributionLimiter(discreteDistributions.get("d13"), 0, 2);
      switch (behaviorOfGenerator.sample()) {
        case 1:
          
          int sizeOfPreviousNodes = previousLevelNodes.size();
          // stvori novih l-p veza
          for (int i = l - p; i >= 0; i--) {
            putEdgeInLevelEdges(levelEdges, currentLevel,
                previousLevelNodes.get(RandomProvider.getRandom().nextInt(sizeOfPreviousNodes)));
          }
          lEqualP(nodes, levelEdges, currentLevel, l);
          break;
        case 2:
          
          Set<AbstractNode> newNodes = new HashSet<>();
          Map<AbstractNode, Integer> parents = levelEdges.get(currentLevel);
          for(AbstractNode parent : parents.keySet()) {
            List<AbstractNode> children = new LinkedList<>(nodes);
            int numberOfNodes = l;
            for(int i = 0; i < parents.get(parent); i++) {
              AbstractNode child = children.remove(RandomProvider.getRandom().nextInt(numberOfNodes--));
              child.getParents().add(parent);
              parent.getChildren().add(child);
              newNodes.add(child);
            }
          }
          // vrati samo one koji imaju roditelja
          return new ArrayList<>(newNodes);
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
  private void lEqualP(List<AbstractNode> nodes, List<Map<AbstractNode, Integer>> levelEdges, int currentLevel,
      int numberOfNodes) {

    List<AbstractNode> children = new LinkedList<>(nodes);
    Map<AbstractNode, Integer> parents = levelEdges.get(currentLevel);
    for (AbstractNode parent : parents.keySet()) {
      for (int i = 0; i < parents.get(parent); i++) {
        AbstractNode child = children.remove(RandomProvider.getRandom().nextInt(numberOfNodes--));
        child.getParents().add(parent);
        parent.getChildren().add(child);
      }
    }
  }

  private int calculateNumberOfEdges(Map<AbstractNode, Integer> map) {
    int counter = 0;
    for (AbstractNode node : map.keySet()) {
      counter += map.get(node);
    }
    return counter;
  }

  private void createEdges(List<AbstractNode> nodes, int currentLevel, int depthK,
      List<Map<AbstractNode, Integer>> levelEdges) {

    final IDiscreteDistribution targetLevelDistribution =
        new DiscreteDistributionLimiter(discreteDistributions.get("d9"), 0, depthK - currentLevel + 1);

    for (final AbstractNode node : nodes) {
      // 4
      final int edgeCount = discreteDistributions.get(node.getType().getDistributionName()).sample();
      // 5
      for (int j = 0; j < edgeCount; j++) {
        final int targetLevel = targetLevelDistribution.sample();
        putEdgeInLevelEdges(levelEdges, targetLevel, node);
      }
    }
    // obavezno stvoriti jednu vezu prema razini trenutnaRazina + 1
    if (levelEdges.get(currentLevel + 1).isEmpty()) {
      AbstractNode node = nodes.get(RandomProvider.getRandom().nextInt(nodes.size()));
      levelEdges.get(currentLevel + 1).put(node, 1);
    }
  }

  /**
   * Stvara novu vezu.
   * @param levelEdges lista koja cuva sve trenutne veze
   * @param targetLevel razina na koju pokazuje veza
   * @param node roditelj veze
   */
  private void putEdgeInLevelEdges(List<Map<AbstractNode, Integer>> levelEdges, int targetLevel, AbstractNode node) {
    if (levelEdges.get(targetLevel).containsKey(node)) {
      levelEdges.get(targetLevel).put(node, levelEdges.get(targetLevel).get(node) + 1);
    } else {
      levelEdges.get(targetLevel).put(node, 1);
    }
  }

  private List<AbstractNode> createNodes(int numberOfNodes) {
    List<AbstractNode> nodes = new ArrayList<>(numberOfNodes);

    // sluzi za odabir tipa cvora, pogledati enum NodeType zasto su granice [1, 5>
    IDiscreteDistribution nodeTypeDistribution =
        new DiscreteDistributionLimiter(discreteDistributions.get("d10"), 1, 5);

    for (int i = 0; i < numberOfNodes; i++) {
      AbstractNode node = NodeType.get(nodeTypeDistribution.sample()).newInstance();
      node.setId(nextId());
      node.setReliability(realDistributions.get("p2").sample());
      node.setWeight(realDistributions.get("p3").sample());
      nodes.add(node);
    }
    return nodes;
  }

  private int nextId() {
    return idCount++;
  }

}
