package hr.fer.zemris.composite.generator;

import hr.fer.zemris.composite.generator.model.AbstractNode;
import hr.fer.zemris.composite.generator.model.Model;
import hr.fer.zemris.composite.generator.model.nodes.BranchNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OutputUtilities {

  private static final int INDENT_SIZE = 2;

  private static int indent = 0;

  public static String toDot(final Model model, final String graphName) {
    final Set<AbstractNode> nodes = new HashSet<>();

    for (final AbstractNode input : model.getInputs()) {
      getNodes(input, nodes);
    }

    final StringBuilder builder = new StringBuilder();

    putLine(builder, "digraph " + graphName + " {");
    incrementIndent();
    putLine(builder, "clusterrank=local;");
    putLine(builder, "ranksep=\"1.2 equally\";");
    putLine(builder, "");

    final List<List<AbstractNode>> levels = new ArrayList<>();

    final int levelCount = model.getOutput().getLevel();
    for (int i = 0; i <= levelCount; i++) {
      levels.add(new ArrayList<AbstractNode>());
    }

    for (final AbstractNode node : nodes) {
      levels.get(node.getLevel()).add(node);

      final int parentSize = node.getParents().size();
      for (int i = 0; i < parentSize; ++i) {
        final AbstractNode parent = node.getParents().get(i);
        String line = node.getId() + " -> " + parent.getId();

        if (node instanceof BranchNode) {
          line += " [label=\"" + String.format("%.3f", ((BranchNode) node).getNormalizedProbabilities().get(i)) + "\"]";
        }

        line += ";";
        putLine(builder, line);
      }
    }

    putLine(builder, "");

    for (int i = 0; i < levels.size(); i++) {
      final List<AbstractNode> level = levels.get(i);
      final int levelSize = level.size();

      if (levelSize == 0) {
        continue;
      }

      putLine(builder, "subgraph cluster_" + i + " {");
      incrementIndent();
      putLine(builder, "label=\"level " + i + "\"");
      putLine(builder, "rank=same;");
      putLine(builder, "color=blue;");

      for (final AbstractNode node : level) {
        putLine(builder, node.getId() + " [label=\"" + node.getLabel() + "\"];");
      }

      decrementIndent();
      putLine(builder, "}");
    }

    decrementIndent();
    putLine(builder, "}");

    return builder.toString();
  }

  public static String generateHtml(final int size, final String fileFormat) {
    final StringBuilder builder = new StringBuilder();
    putLine(builder, "<html>");
    incrementIndent();
    putLine(builder, "<head>");
    incrementIndent();
    putLine(builder, "<style>");
    incrementIndent();
    putLine(builder, "img {");
    incrementIndent();
    putLine(builder, "margin-top: 100px;");
    decrementIndent();
    putLine(builder, "}");
    decrementIndent();
    putLine(builder, "</style>");
    decrementIndent();
    putLine(builder, "</head>");
    putLine(builder, "<body>");
    incrementIndent();

    for (int i = 0; i < size; i++) {
      putLine(builder, "<img src=\"" + String.format(fileFormat, i) + "\"/><br/>");
    }
    decrementIndent();
    putLine(builder, "</body>");
    decrementIndent();
    putLine(builder, "</html>");
    return builder.toString();
  }

  private static void putLine(final StringBuilder builder, final String line) {
    putIndent(builder);
    builder.append(line + "\n");
  }

  private static void putIndent(final StringBuilder builder) {
    for (int i = 0; i < indent; ++i) {
      builder.append(' ');
    }
  }

  private static void incrementIndent() {
    indent += INDENT_SIZE;
  }

  private static void decrementIndent() {
    indent -= INDENT_SIZE;
  }

  private static void getNodes(final AbstractNode node, final Set<AbstractNode> nodes) {
    if (nodes.contains(node)) {
      return;
    }

    nodes.add(node);

    for (final AbstractNode child : node.getChildren()) {
      getNodes(child, nodes);
    }
  }

}
