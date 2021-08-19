package org.dromara.hodor.common.event;

/**
 * 事务发布器
 *
 * @param <V>
 */
public interface EventPublisher<V> {

    /**
     * 在事件对象上面添加事件监听器
     */
    default void registerListener() {

    }

    /**
     * 添加默认事件类型的监听器
     */
    void addListener(HodorEventListener<V> objectListener);

    /**
     * 添加某个事件类型的监听器，一个 eventType 可对应多个 object listener
     */
    void addListener(HodorEventListener<V> objectListener, Object eventType);

    /**
     * 移除指定 event type 中的一个object listener
     */
    void removeListener(HodorEventListener<V> objectListener, Object eventType);

    /**
     * 移除一组 object listeners
     */
    void removeListener(Object eventType);

    /**
     * 清除事件监听器
     */
    void clearListener();

    /**
     * 唤醒一组事件监听器。这组事件监听器按序执行
     */
    void publish(Event<V> event);

    /**
     * 给某个事件类型发布一个消息。这个消息会触发一组事件监听器执行
     */
    void publish(V v, Object eventType);

}
