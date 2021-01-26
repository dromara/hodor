package org.dromara.hodor.common.executor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.dromara.hodor.common.concurrent.HodorThreadFactory;
import org.dromara.hodor.common.queue.CircleQueue;

/**
 * hodor executor factory
 *
 * @author tomgs
 * @since 2021/1/25
 */
public class HodorExecutorFactory {

    private final HodorExecutor executor;

    private static final Map<String, ThreadPoolExecutor> threadPoolExecutorMap = new ConcurrentHashMap<>();

    private HodorExecutorFactory(final String executorName, final int threadSize) {
        CircleQueue<HodorRunnable> hodorQueue = new CircleQueue<>();
        ThreadPoolExecutor threadPoolExecutor = createThreadPoolExecutor(executorName, threadSize);
        this.executor = new HodorExecutor(hodorQueue, threadPoolExecutor);
    }

    public static ThreadPoolExecutor createThreadPoolExecutor(String executorName, int threadSize) {
        return threadPoolExecutorMap.computeIfAbsent(executorName, k -> {
            ThreadFactory threadFactory = HodorThreadFactory.create(executorName, false);
            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(threadSize, threadSize, 3L, TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(5000), threadFactory);
            threadPoolExecutor.allowCoreThreadTimeOut(true);
            return threadPoolExecutor;
        });
    }

    public static HodorExecutor createExecutor(final String executorName, final int threadSize) {
        return new HodorExecutorFactory(executorName, threadSize).executor;
    }

}
