package org.dromara.hodor.common.event;

import java.util.Set;
import org.dromara.hodor.common.executor.HodorExecutor;
import org.dromara.hodor.common.executor.HodorExecutorFactory;
import org.dromara.hodor.common.executor.HodorRunnable;

/**
 * abstract async event publisher
 *
 * 异步串行执行：事件发布不会阻塞主任务执行，但是同一事件发布器发布的事件为有序执行
 *
 * @author tomgs
 * @since 2021/3/18
 */
public abstract class AbstractAsyncEventPublisher<V> extends AbstractEventPublisher<V> {

    private final HodorExecutor eventExecutor;

    public AbstractAsyncEventPublisher() {
        final int threadSize = Runtime.getRuntime().availableProcessors() * 2;
        this.eventExecutor = HodorExecutorFactory.createDefaultExecutor("event-exec", threadSize, true);
    }

    @Override
    public void publish(V v, Object eventType) {
        publish(new Event<>(v, eventType));
    }

    @Override
    public void publish(Event<V> event) {
        Set<HodorEventListener<V>> listenerSet = getListeners(event.getEventType());
        eventExecutor.serialExecute(new HodorRunnable() {
            @Override
            public void execute() {
                for (HodorEventListener<V> listener : listenerSet) {
                    listener.onEvent(event);
                }
            }
        });
    }

    /**
     * 并行事件发布
     *
     * @param event 事件
     */
    public void parallelPublish(Event<V> event) {
        Set<HodorEventListener<V>> listenerSet = getListeners(event.getEventType());
        eventExecutor.parallelExecute(new HodorRunnable() {
            @Override
            public void execute() {
                for (HodorEventListener<V> listener : listenerSet) {
                    listener.onEvent(event);
                }
            }
        });
    }

}
