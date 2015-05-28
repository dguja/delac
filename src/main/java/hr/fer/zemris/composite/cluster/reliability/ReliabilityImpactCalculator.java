package hr.fer.zemris.composite.cluster.reliability;

import hr.fer.zemris.composite.cluster.clusterable.Vector;
import hr.fer.zemris.composite.generator.model.Dataset;
import hr.fer.zemris.composite.generator.model.DirectionType;
import hr.fer.zemris.composite.generator.model.Model;
import hr.fer.zemris.composite.generator.model.nodes.InputNode;
import hr.fer.zemris.composite.generator.model.nodes.OutputNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ReliabilityImpactCalculator {

  public static List<Vector> calculate(Dataset dataset) {
    List<Vector> relImpacts = new ArrayList<>();
    Map<Long, Integer> idToIndex = generateIdToIndexMap(dataset);

    double[] relImpact = new double[dataset.getInputNodeCount()];
    for (Model model : dataset.getModels()) {
      // izracunaj pouzdanost modela prije mijenjanja ulaznih cvorova
      OutputNode outputNode = model.getOutput();
      double oldReliability = outputNode.getReliability();

      for (int i = 0; i < model.getInputs().size(); ++i) {
        InputNode inputNode = model.getInputs().get(i);
        relImpact[idToIndex.get(inputNode.getId())] =
            calculateReliabilityImpact(inputNode, outputNode, oldReliability);
      }

      relImpacts.add(new Vector(relImpact));
    }

    return relImpacts;
  }

  private static Map<Long, Integer> generateIdToIndexMap(Dataset dataset) {
    // dodaj sve IDeve u Set
    Set<Long> inputNodeIDs = new TreeSet<>();
    for (Model model : dataset.getModels()) {
      for (InputNode inputNode : model.getInputs()) {
        inputNodeIDs.add(inputNode.getId());
      }
    }

    // mapiraj redom IDeve na indekse
    Map<Long, Integer> idToIndex = new HashMap<>();
    int index = 0;
    for (Long id : inputNodeIDs) {
      idToIndex.put(id, index);
      ++index;
    }

    return idToIndex;
  }

  private static double calculateReliabilityImpact(InputNode inputNode, OutputNode outputNode,
      double oldReliability) {
    // zapamti staru pouzdanost
    double memReliability = inputNode.getReliability();

    // pouzdanost na 1.0, izracunaj novu pouzdanost grafa
    inputNode.setReliability(1);
    inputNode.calculateReliability(DirectionType.CHILD);
    double newReliability = outputNode.getReliability();

    // vrati pouzdanost na staru vrijednost, azuriraj sve ostale pouzdanosti
    inputNode.setReliability(memReliability);
    inputNode.calculateReliability(DirectionType.CHILD);

    // utjecaj = razlika nove i stare pouzdanosti
    return newReliability - oldReliability;
  }
}
