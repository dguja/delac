package hr.fer.zemris.composite.generator.dot;

import java.util.HashSet;
import java.util.Set;

import hr.fer.zemris.composite.generator.model.AbstractNode;
import hr.fer.zemris.composite.generator.model.Model;

public class DotUtilities {

	public static String toDot(Model model, String graphName) {
		StringBuilder sb = new StringBuilder();
		Set<Long> visited = new HashSet<>();

		sb.append("digraph ").append(graphName).append(" {\n");
		
		buildString(sb, model.getOutput(), visited);
		sb.append("}");

		return sb.toString();
	}

	private static void buildString(StringBuilder sb, AbstractNode node, Set<Long> visited) {
		if (visited.contains(node.getId())) {
			return;
		}

		for (AbstractNode parent : node.getParents()) {
			buildString(sb, parent, visited);
			visited.add(parent.getId());
			sb.append(parent.getId()).append(" -> ").append(node.getId()).append(";\n");
		}
	}
}
