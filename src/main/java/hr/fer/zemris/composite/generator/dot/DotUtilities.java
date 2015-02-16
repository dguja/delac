package hr.fer.zemris.composite.generator.dot;

import hr.fer.zemris.composite.generator.model.AbstractNode;
import hr.fer.zemris.composite.generator.model.Model;
import hr.fer.zemris.composite.generator.model.nodes.OutputNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DotUtilities {

  public static String toDot(final Model model, final String graphName) {
    final OutputNode outputNode = model.getOutput();

    final Set<AbstractNode> visited = new HashSet<>();
    getNodes(outputNode, visited);

    final StringBuilder builder = new StringBuilder();

    builder.append("digraph ").append(graphName).append(" {\n");

    final List<List<AbstractNode>> levels = new ArrayList<>();

    for (int i = 0; i <= outputNode.getLevel(); i++) {
      levels.add(new ArrayList<AbstractNode>());
    }

    for (final AbstractNode node : visited) {
      levels.get(node.getLevel()).add(node);

      for (final AbstractNode parent : node.getParents()) {
        builder.append(node.getId()).append(" -> ").append(parent.getId()).append(";\n");
      }
    }

    builder.append("\n");

    for (int i = 0; i < levels.size(); i++) {
      builder.append("subgraph level_" + i + " {\nrank=same;\n");

      for (final AbstractNode node : levels.get(i)) {
        builder.append(node.getId() + " [label=\"" + node.getType().toString().toLowerCase() + "\\n"
            + String.format("%.3f", node.getReliability()) + "\"]" + "\n");
      }

      builder.append(";\n}\n");
    }

    builder.append("}");

    return builder.toString();
  }

  private static void getNodes(final AbstractNode node, final Set<AbstractNode> visited) {
    if (visited.contains(node)) {
      return;
    }

    visited.add(node);

    for (final AbstractNode parent : node.getParents()) {
      getNodes(parent, visited);
    }
  }

}
