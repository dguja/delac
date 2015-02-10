package hr.fer.zemris.composite.generator.model;

import hr.fer.zemris.composite.generator.model.nodes.InputNode;
import hr.fer.zemris.composite.generator.model.nodes.OutputNode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Model implements Serializable {

  /**
   * Generated serial version ID.
   */
  private static final long serialVersionUID = -9102857521500197424L;

  private final List<InputNode> inputs;

  private final OutputNode output;

  public Model(final List<InputNode> inputs, final OutputNode output) {
    super();

    this.inputs = new ArrayList<>(inputs);
    this.output = output;
  }

  public List<InputNode> getInputs() {
    return Collections.unmodifiableList(inputs);
  }

  public OutputNode getOutput() {
    return output;
  }

}
