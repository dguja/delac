package hr.fer.zemris.composite.generator;

import hr.fer.zemris.composite.generator.model.Model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

public class Dataset implements Serializable {

  /**
   * Generated serial version ID.
   */
  private static final long serialVersionUID = 9010594095676536207L;

  private List<Model> models;

  public Dataset(List<Model> models) {
    super();
    this.models = models;
  }

  public List<Model> getModels() {
    return models;
  }

  public void setModels(List<Model> models) {
    this.models = models;
  }

  public void saveDataset(String path) throws FileNotFoundException, IOException {
    FileOutputStream fos = new FileOutputStream(path);
    ObjectOutputStream oos = new ObjectOutputStream(fos);
    oos.writeObject(this);
    oos.close();
    fos.close();
  }

  public static Dataset loadDataset(String path) throws ClassNotFoundException, IOException {
    FileInputStream fis = new FileInputStream(path);
    ObjectInputStream ois = new ObjectInputStream(fis);
    Dataset dataset = (Dataset) ois.readObject();
    ois.close();
    return dataset;
  }

}
