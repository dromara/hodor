package org.dromara.hodor.common.event;

/**
 * Event Publisher
 *
 * @author tomgs
 */
public interface EventPublisher<V> {

    /**
     * registry listener
     */
    default void registryListener() {

    }

    /**
     * add default event type listener
     *
     * @param listener registry listener
     */
    void addListener(HodorEventListener<V> listener);

    /**
     * 添加某个事件类型的监听器，一个 eventType 可对应多个 listener
     *
     * @param listener listener
     * @param eventType event type
     */
    void addListener(HodorEventListener<V> listener, Object eventType);

    /**
     * Removes a listener of the specified event type
     *
     * @param eventType the specified event type
     * @param listener a listener
     */
    void removeListener(Object eventType, HodorEventListener<V> listener);

    /**
     * Removes listeners from the specified event type
     *
     * @param eventType the specified event type
     */
    void removeListener(Object eventType);

    /**
     * Clear the event listener
     */
    void clearListener();

    /**
     * Publish the specified event
     *
     * @param event the specified event
     *
     */
    void publish(Event<V> event);

}
