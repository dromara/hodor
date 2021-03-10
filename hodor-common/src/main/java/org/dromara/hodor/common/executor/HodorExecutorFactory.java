package org.dromara.hodor.common.executor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.dromara.hodor.common.concurrent.HodorThreadFactory;
import org.dromara.hodor.common.queue.CircleQueue;
import org.dromara.hodor.common.queue.DiscardOldestElementPolicy;

/**
 * hodor executor factory
 *
 * @author tomgs
 * @since 2021/1/25
 */
public class HodorExecutorFactory {

    private final HodorExecutor executor;

    private static final Map<String, ThreadPoolExecutor> threadPoolExecutorMap = new ConcurrentHashMap<>();

    private HodorExecutorFactory(final String executorName, final int threadSize, final boolean coreThreadTimeOut) {
        CircleQueue<HodorRunnable> hodorQueue = new CircleQueue<>(128);
        ThreadPoolExecutor threadPoolExecutor = createThreadPoolExecutor(executorName, threadSize, coreThreadTimeOut);
        this.executor = new HodorExecutor(hodorQueue, threadPoolExecutor);
        this.executor.setRejectEnqueuePolicy(new DiscardOldestElementPolicy<>());
    }

    public static ThreadPoolExecutor createThreadPoolExecutor(final String executorName, final int threadSize, final boolean coreThreadTimeOut) {
        return threadPoolExecutorMap.computeIfAbsent(executorName, k -> {
            ThreadFactory threadFactory = HodorThreadFactory.create(executorName, false);
            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(threadSize, threadSize, 3L, TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(5000), threadFactory);
            threadPoolExecutor.allowCoreThreadTimeOut(coreThreadTimeOut);
            return threadPoolExecutor;
        });
    }

    public static HodorExecutor createDefaultExecutor(final String executorName, final int threadSize, final boolean coreThreadTimeOut) {
        return new HodorExecutorFactory(executorName, threadSize, coreThreadTimeOut).executor;
    }

}
