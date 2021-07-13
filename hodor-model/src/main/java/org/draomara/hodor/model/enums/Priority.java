package org.draomara.hodor.model.enums;

/**
 * job priority
 *
 * @author tomgs
 * @since 2020/8/26
 */
public enum Priority {

  DEFAULT(0),

  MEDIUM(1),

  HIGHER(2);

  private int priority;

  Priority(int priority) {
    this.priority = priority;
  }

  public int getValue() {
    return priority;
  }

}
