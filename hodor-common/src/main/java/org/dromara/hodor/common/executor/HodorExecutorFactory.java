package org.dromara.hodor.common.executor;

import org.dromara.hodor.common.concurrent.HodorThreadFactory;
import org.dromara.hodor.common.queue.AbortEnqueuePolicy;
import org.dromara.hodor.common.queue.AdjustThreadSizePolicy;
import org.dromara.hodor.common.queue.DiscardOldestElementPolicy;
import org.dromara.hodor.common.queue.ResizeQueuePolicy;

import java.util.Map;
import java.util.concurrent.*;

/**
 * hodor executor factory
 *
 * @author tomgs
 * @since 1.0
 */
public class HodorExecutorFactory {

    private static final Map<String, ThreadPoolExecutor> threadPoolExecutorMap = new ConcurrentHashMap<>(8);

    public static ThreadPoolExecutor createThreadPoolExecutor(final String executorName, final int threadSize, final int poolSize, final boolean coreThreadTimeOut) {
        return threadPoolExecutorMap.computeIfAbsent(executorName, k -> {
            ThreadFactory threadFactory = HodorThreadFactory.create(executorName, false);
            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(threadSize, threadSize, 3L, TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(poolSize), threadFactory);
            threadPoolExecutor.allowCoreThreadTimeOut(coreThreadTimeOut);
            return threadPoolExecutor;
        });
    }

    public static HodorExecutor createDefaultExecutor(final String executorName, final int threadSize, final boolean coreThreadTimeOut) {
        return createExecutor(executorName, threadSize, 500, coreThreadTimeOut, 0);
    }

    public static HodorExecutor createExecutor(final String executorName, final int threadSize, final int poolSize,
                                               final boolean coreThreadTimeOut, final int taskStackingStrategy) {
        ThreadPoolExecutor threadPoolExecutor = createThreadPoolExecutor(executorName, threadSize, poolSize, coreThreadTimeOut);
        HodorExecutor executor = new HodorExecutor(threadPoolExecutor);
        setHodorExecutorStackingStrategy(taskStackingStrategy, executor);
        return executor;
    }

    private static void setHodorExecutorStackingStrategy(int taskStackingStrategy, HodorExecutor executor) {
        if (taskStackingStrategy == 0) {
            executor.setRejectEnqueuePolicy(new DiscardOldestElementPolicy<>());
            return;
        }

        if (taskStackingStrategy == 1) {
            executor.setRejectEnqueuePolicy(new AbortEnqueuePolicy<>());
            return;
        }

        if (taskStackingStrategy == 2) {
            executor.setRejectEnqueuePolicy(new ResizeQueuePolicy<>());
            return;
        }

        if (taskStackingStrategy == 3) {
            executor.setRejectEnqueuePolicy(new AdjustThreadSizePolicy<>(executor.getExecutor()));
            return;
        }

        throw new UnsupportedOperationException("Unsupported taskStackingStrategy " + taskStackingStrategy);
    }

}
