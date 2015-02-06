package hr.fer.zemris.composite.generator;

import hr.fer.zemris.composite.generator.nodes.InputNode;
import hr.fer.zemris.composite.generator.nodes.OutputNode;

import java.util.List;

public class Model {

  private List<InputNode> inputs;

  private OutputNode output;

  public List<InputNode> getInputs() {
    return inputs;
  }

  public OutputNode getOutput() {
    return output;
  }

}
