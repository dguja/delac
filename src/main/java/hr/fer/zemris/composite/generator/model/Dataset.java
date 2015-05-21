package hr.fer.zemris.composite.generator.model;

import java.io.Serializable;
import java.util.List;

public class Dataset implements Serializable {

  /**
   * Generated serial version ID.
   */
  private static final long serialVersionUID = 9010594095676536207L;

  private List<Model> models;

  private int inputNodeCount;
  
  public Dataset(final List<Model> models, int inputNodeCount) {
    super();

    this.models = models;
    this.inputNodeCount = inputNodeCount;
  }

  public List<Model> getModels() {
    return models;
  }

  public void setModels(final List<Model> models) {
    this.models = models;
  }
  
  public int getInputNodeCount() {
    return inputNodeCount;
  }

}
