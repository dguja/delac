package hr.fer.zemris.composite.generator.dot;

import hr.fer.zemris.composite.generator.model.AbstractNode;
import hr.fer.zemris.composite.generator.model.Model;
import hr.fer.zemris.composite.generator.model.nodes.BranchNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DotUtilities {

  public static String toDot(final Model model, final String graphName) {
    final Set<AbstractNode> visited = new HashSet<>();

    for (final AbstractNode input : model.getInputs()) {
      getNodes(input, visited);
    }

    final StringBuilder builder = new StringBuilder();

    builder.append("digraph ").append(graphName).append(" {\nclusterrank=local;\n");

    final List<List<AbstractNode>> levels = new ArrayList<>();

    final int levelCount = model.getOutput().getLevel();
    for (int i = 0; i <= levelCount; i++) {
      levels.add(new ArrayList<AbstractNode>());
    }

    for (final AbstractNode node : visited) {
      levels.get(node.getLevel()).add(node);

      int parentSize = node.getParents().size();
      for (int i = 0; i < parentSize; ++i) {
        final AbstractNode parent = node.getParents().get(i);
        builder.append(node.getId()).append(" -> ").append(parent.getId());
        
        if (node instanceof BranchNode) {
          builder.append(" [label=\"" + ((BranchNode)node).getProbabilities().get(i) + "\"]");
        }
        
        builder.append(";\n");
      }
    }

    builder.append("\n");

    for (int i = 0; i < levels.size(); i++) {
      final List<AbstractNode> level = levels.get(i);
      final int levelSize = level.size();

      if (levelSize == 0) {
        continue;
      }

      builder.append("subgraph cluster_" + i + " {\nrank=same;\n");

      for (final AbstractNode node : level) {
        builder.append(node.getId() + " [label=\"" + node.getLabel() + "\"];" + "\n");
      }

      builder.append("color=blue;\nlabel=\"level " + i + "\"\n}\n");
    }

    builder.append("}");

    return builder.toString();
  }

  private static void getNodes(final AbstractNode node, final Set<AbstractNode> visited) {
    if (visited.contains(node)) {
      return;
    }

    visited.add(node);

    for (final AbstractNode child : node.getChildren()) {
      getNodes(child, visited);
    }
  }

}
