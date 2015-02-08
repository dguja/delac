package hr.fer.zemris.composite.generator.model;

public enum NodeType {

  INPUT(0), BRANCH(1), SEQUENCE(2), PARALLEL(3), LOOP(4), OUTPUT(5);

  private int index;

  private NodeType(final int index) {

    this.index = index;
  }

  public String getDistributionName() {
    return "d" + (index + 4);
  }

  /**
   * TODO
   * NAPOMENA stvara se cvor kojemu nije postavljen ID
   * 
   * @return
   */
  public AbstractNode newInstance() {
    switch (this) {
      case BRANCH:
        return null; // TODO

      case SEQUENCE:
        return null; // TODO

      case PARALLEL:
        return null; // TODO

      case LOOP:
        return null; // TODO

      default:
        return null;
    }
  }

  public static NodeType get(final int index) {
    return values()[index];
  }

}
