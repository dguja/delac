package hr.fer.zemris.composite.generator.model;

import java.io.Serializable;
import java.util.List;

public class Dataset implements Serializable {

  /**
   * Generated serial version ID.
   */
  private static final long serialVersionUID = 9010594095676536207L;

  private List<Model> models;

  public Dataset(final List<Model> models) {
    super();

    this.models = models;
  }

  public List<Model> getModels() {
    return models;
  }

  public void setModels(final List<Model> models) {
    this.models = models;
  }

}
