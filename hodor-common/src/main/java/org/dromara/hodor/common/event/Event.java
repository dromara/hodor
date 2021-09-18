package org.dromara.hodor.common.event;


/**
 * event source
 *
 * @author tomgs
 * @param <V> event value
 */
public class Event<V> {

    public static final String DEFAULT_TYPE = "DEFAULT";

    private V value;

    private final Object eventType;

    /**
     * new event
     *
     * @param eventValue event value
     * @param eventType  the specified event type
     */
    public Event(V eventValue, Object eventType) {
        this.value = eventValue;
        this.eventType = eventType;
    }

    /**
     * creat event by the specified event type
     *
     * @param eventValue event value
     * @param eventType  the specified event type
     */
    public static <V> Event<V> create(V eventValue, Object eventType) {
        return new Event<>(eventValue, eventType);
    }

    /**
     * creat event by the default event type
     *
     * @param eventValue event value
     */
    public static <V> Event<V> create(V eventValue) {
        return new Event<>(eventValue, DEFAULT_TYPE);
    }

    public Object getEventType() {
        return eventType;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

}
