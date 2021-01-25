package org.dromara.hodor.common.concurrent;

import org.dromara.hodor.common.disruptor.QueueConsumerExecutor;
import org.dromara.hodor.common.disruptor.QueueConsumerFactory;
import org.dromara.hodor.common.disruptor.QueueProviderManager;

/**
 * hodor executor
 *
 * @author tomgs
 * @since 2021/1/7
 */
public class HodorExecutor<T> {
    /**
     * 分区大小.
     */
    private volatile Integer ringBufferSize;

    /**
     * 线程的大小.
     */
    private volatile Integer threadSize;

    /**
     * 消费端的数量.
     */
    private volatile Integer consumerSize;

    /**
     * 队列管理器
     */
    private QueueProviderManager<T> manager;

    public HodorExecutor(int threadSize) {
        this(new HodorQueueConsumerFactory<>("hodor_thread"), threadSize);
    }

    public HodorExecutor(QueueConsumerFactory<T> factory, int threadSize) {
        this(factory, threadSize, threadSize);
    }

    public HodorExecutor(QueueConsumerFactory<T> factory, int threadSize, int consumerSize) {
        this(factory, threadSize, consumerSize, 8 * 1024 * 1024);
    }

    public HodorExecutor(QueueConsumerFactory<T> factory, int threadSize, int consumerSize, int ringBufferSize) {
        this.threadSize = threadSize;
        this.consumerSize = consumerSize;
        this.ringBufferSize = ringBufferSize;
        this.manager = new QueueProviderManager<>(factory, threadSize, consumerSize, ringBufferSize);
        this.manager.start();
    }

    public void execute(final QueueConsumerExecutor<T> consumerExecutor) {
        this.manager.getProvider().onData(e -> e.setData(consumerExecutor.getData()));
    }

    public Integer getRingBufferSize() {
        return ringBufferSize;
    }

    public Integer getThreadSize() {
        return threadSize;
    }

    public Integer getConsumerSize() {
        return consumerSize;
    }

}
