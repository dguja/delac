package hr.fer.zemris.composite.generator.model;

import hr.fer.zemris.composite.generator.model.nodes.InputNode;
import hr.fer.zemris.composite.generator.model.nodes.OutputNode;

import java.io.Serializable;
import java.util.List;

public class Model implements Serializable {

  /**
   * Generated serial version ID.
   */
  private static final long serialVersionUID = -9102857521500197424L;

  private List<InputNode> inputs;

  private OutputNode output;

  public List<InputNode> getInputs() {
    return inputs;
  }
  
  public void setInputs(List<InputNode> inputs) {
    this.inputs = inputs;
  }

  public OutputNode getOutput() {
    return output;
  }

}
