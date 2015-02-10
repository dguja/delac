package hr.fer.zemris.composite.generator.model;

import hr.fer.zemris.composite.generator.model.nodes.DirectionType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Apstraktni čvor
 * 
 * @author Daniel
 * 
 */

public abstract class AbstractNode implements Serializable {

  /**
   * Generated serial version ID.
   */
  private static final long serialVersionUID = 4195003577285391138L;

  protected long id;

  protected int level;

  protected double reliability;

  protected double weight;

  protected List<AbstractNode> parents = new ArrayList<>();

  protected List<AbstractNode> children = new ArrayList<>();

  public AbstractNode(final long id, final int level) {
    super();
    this.id = id;
    this.level = level;
  }

  public long getId() {
    return id;
  }

  public int getLevel() {
    return level;
  }

  public List<AbstractNode> getParents() {
    return Collections.unmodifiableList(parents);
  }

  public List<AbstractNode> getChildren() {
    return Collections.unmodifiableList(children);
  }

  public double getReliability() {
    return reliability;
  }

  public double getWeight() {
    return weight;
  }

  public void setWeight(final double weight) {
    this.weight = weight;
  }

  public List<AbstractNode> getUpdateList(final DirectionType direction) {
    final Set<AbstractNode> updateSet = new TreeSet<>(new Comparator<AbstractNode>() {

      @Override
      public int compare(final AbstractNode node1, final AbstractNode node2) {
        return Integer.compare(node1.getLevel(), node2.getLevel());
      }

    });

    List<AbstractNode> nodes;
    if (direction == DirectionType.CHILD) {
      nodes = children;
    } else {
      nodes = parents;
    }

    for (final AbstractNode node : nodes) {
      updateSet.add(node);
      updateSet.addAll(node.getUpdateList(direction));
    }

    final List<AbstractNode> updateList = new ArrayList<AbstractNode>(updateSet);
    return updateList;
  }

  public void calculateReliability(final DirectionType direction) {
    final List<AbstractNode> updateList = getUpdateList(direction);
    for (final AbstractNode node : updateList) {
      node.calculateDirectReliability();
    }
  }

  @Override
  public int hashCode() {
    return 31 + (int) (id ^ (id >>> 32));
  }

  @Override
  public boolean equals(final Object object) {
    if (this == object) {
      return true;
    }

    if (object == null) {
      return false;
    }

    if (!(object instanceof AbstractNode)) {
      return false;
    }

    final AbstractNode other = (AbstractNode) object;
    if (id != other.id) {
      return false;
    }

    return true;
  }

  protected boolean isFull() {
    return false;
  }

  public boolean addParent(final AbstractNode parent) {
    if (isFull()) {
      return false;
    }
    parents.add(parent);
    parent.addChild(this);
    return true;
  }

  protected void addChild(final AbstractNode child) {
    children.add(child);
  }

  protected abstract void calculateDirectReliability();

  public abstract NodeType getType();

}
