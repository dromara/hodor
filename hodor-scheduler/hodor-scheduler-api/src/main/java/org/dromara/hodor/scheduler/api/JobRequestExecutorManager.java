package org.dromara.hodor.scheduler.api;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.dromara.hodor.common.disruptor.QueueConsumerExecutor;
import org.dromara.hodor.common.disruptor.QueueProviderManager;
import org.dromara.hodor.scheduler.api.executor.HodorJobRequestConsumerFactory;

/**
 * job request executor manager
 *
 * @author tomgs
 * @since 2020/9/23
 */
public final class JobRequestExecutorManager {

    private static final JobRequestExecutorManager INSTANCE = new JobRequestExecutorManager();

    private final ConcurrentHashMap<String, QueueProviderManager<?>> queueProvider;

    private JobRequestExecutorManager() {
        this.queueProvider = new ConcurrentHashMap<>();
    }

    public static JobRequestExecutorManager getInstance() {
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    public <T> QueueProviderManager<T> getQueueProvider(String jobKey, Function<? super String, ? extends QueueProviderManager<T>> queueProvider) {
        return (QueueProviderManager<T>) this.queueProvider.computeIfAbsent(jobKey, queueProvider);
    }

    public void submit(final HodorJobExecutionContext context, final QueueConsumerExecutor<HodorJobExecutionContext> consumerExecutor) {
        getQueueProvider(context.getJobKey(), key -> buildQueueProviderManager(key, consumerExecutor)).getProvider().onData(e -> e.setData(context));
    }

    private QueueProviderManager<HodorJobExecutionContext> buildQueueProviderManager(final String jobKey, final QueueConsumerExecutor<HodorJobExecutionContext> executor) {
        QueueProviderManager<HodorJobExecutionContext> manager = buildQueueProviderManager(new HodorJobRequestConsumerFactory(jobKey, executor));
        manager.start();
        return manager;
    }

    private QueueProviderManager<HodorJobExecutionContext> buildQueueProviderManager(final HodorJobRequestConsumerFactory factory) {
        return new QueueProviderManager<>(factory, 1, 1, 4096 * 2 * 2);
    }

    public void deleteQueueProvider(String jobKey) {
        this.queueProvider.remove(jobKey);
    }

}
