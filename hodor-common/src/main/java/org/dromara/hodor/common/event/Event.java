package org.dromara.hodor.common.event;


/**
 * event source
 *
 * @author tomgs
 * @param <V> event value
 */
public class Event<V> {

    private V value;

    private final Object eventType;

    /**
     * @param value     自定义参数
     * @param eventType 事件类型
     */
    public Event(V value, Object eventType) {
        this.value = value;
        this.eventType = eventType;
    }

    public static <V> Event<V> create(V value, Object eventType) {
        return new Event<>(value, eventType);
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
