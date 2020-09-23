package org.dromara.hodor.scheduler.api;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.dromara.hodor.common.disruptor.QueueProviderManager;
import org.dromara.hodor.scheduler.api.executor.HodorJobRequestConsumerFactory;
import org.dromara.hodor.scheduler.api.handler.HodorJobRequestHandler;

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

    public void submitHodorJobExecutor(HodorJobExecutionContext context) {
        getQueueProvider(context.getJobKey(), this::getQueueProviderManager).getProvider().onData(e -> e.setData(context));
    }

    private QueueProviderManager<HodorJobExecutionContext> getQueueProviderManager(String jobKey) {
        QueueProviderManager<HodorJobExecutionContext> manager = new QueueProviderManager<>(new HodorJobRequestConsumerFactory(jobKey, new HodorJobRequestHandler()));
        manager.start();
        return manager;
    }

}
