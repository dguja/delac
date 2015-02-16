package hr.fer.zemris.composite.generator.model.nodes;

import hr.fer.zemris.composite.generator.model.AbstractNode;
import hr.fer.zemris.composite.generator.model.NodeType;

/**
 * Loop node.
 * 
 * @author mmilisic
 * 
 */
public class LoopNode extends AbstractNode {

  /**
   * Generated serial version ID.
   */
  private static final long serialVersionUID = 2416008536138769590L;

  private final int numRepetitions;

  public LoopNode(final long id, final int level, final int numRepetitions) {
    super(id, level);

    this.numRepetitions = numRepetitions;
  }

  public int getNumRepetitions() {
    return numRepetitions;
  }

  @Override
  public boolean isFull() {
    return !parents.isEmpty();
  }

  @Override
  protected void calculateDirectReliability() {
    reliability = Math.pow(parents.get(0).getReliability(), numRepetitions);
  }

  @Override
  public NodeType getType() {
    return NodeType.LOOP;
  }

  @Override
  protected String getParameterText() {
    return Integer.toString(numRepetitions);
  }

}
