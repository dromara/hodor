package org.dromara.hodor.common.concurrent;

import java.util.concurrent.ConcurrentHashMap;
import org.dromara.hodor.common.disruptor.QueueConsumerFactory;

/**
 * hodor executor manager
 *
 * @author tomgs
 * @since 2021/1/7
 */
public class HodorExecutorManager {

    private static final HodorExecutorManager INSTANCE = new HodorExecutorManager();

    private final ConcurrentHashMap<String, HodorExecutor<?>> queueProvider;

    private HodorExecutorManager() {
        this.queueProvider = new ConcurrentHashMap<>();
    }

    public static HodorExecutorManager getInstance() {
        return INSTANCE;
    }

    public <T> HodorExecutor<T> createSingleThreadExecutor() {
        HodorQueueConsumerFactory<T> factory = new HodorQueueConsumerFactory<>("hodor_thread");
        return createFixedThreadExecutor(factory, 1);
    }

    public <T> HodorExecutor<T> createFixedThreadExecutor(QueueConsumerFactory<T> factory, int threadSize) {
        return new HodorExecutor<>(factory, threadSize);
    }

    public void deleteQueueProvider(String queueName) {
        this.queueProvider.remove(queueName);
    }

}
