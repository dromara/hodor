package org.dromara.hodor.core.enums;

public enum Priority {

  DEFAULT(5),
  MEDIUM(10),
  HIGHER(15);

  private int priority;

  Priority(int priority) {
    this.priority = priority;
  }

  public int getValue() {
    return priority;
  }

}
