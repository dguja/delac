package hr.fer.zemris.composite.generator.dot;

import java.util.HashSet;
import java.util.Set;

import hr.fer.zemris.composite.generator.model.AbstractNode;
import hr.fer.zemris.composite.generator.model.Model;

public class DotUtilities {

	public static String toDot(Model model) {
		StringBuilder sb = new StringBuilder();
		Set<Long> visited = new HashSet<>();
		
		sb.append("digraph test {\n");
		buildString(sb, model.getOutput(), visited);
		sb.append("}");
		
		return sb.toString();
	}

	private static void buildString(StringBuilder sb, AbstractNode node, Set<Long> visited) {
		if(visited.contains(node.getId())) {
			return;
		}
		
		for(AbstractNode parent : node.getParents()) {
			visited.add(parent.getId());
			buildString(sb, parent, visited);
			sb.append(parent.getId()).append(" -> ").append(node.getId()).append(";\n");
		}
	}
}
