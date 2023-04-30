package org.dromara.hodor.model.enums;

/**
 * job priority
 *
 * @author tomgs
 * @since 1.0
 */
public enum Priority {

    DEFAULT(0),

    MEDIUM(1),

    HIGHER(2);

    private final int priority;

    Priority(int priority) {
        this.priority = priority;
    }

    public int getValue() {
        return priority;
    }

    public static Priority valueOf(int priorityValue) {
        for (Priority priority : Priority.values()) {
            if (priority.priority == priorityValue) {
                return priority;
            }
        }
        throw new IllegalArgumentException("not found priority value " + priorityValue);
    }

}
