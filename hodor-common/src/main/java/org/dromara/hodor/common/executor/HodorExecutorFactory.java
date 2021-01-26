package org.dromara.hodor.common.executor;

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

    private HodorExecutorFactory(final String executorName, final int threadSize) {
        ThreadFactory threadFactory = HodorThreadFactory.create(executorName, false);
        CircleQueue<HodorRunnable> hodorQueue = new CircleQueue<>();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(threadSize, threadSize, 3L, TimeUnit.MINUTES, new LinkedBlockingQueue<>(), threadFactory);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        this.executor = new HodorExecutor(hodorQueue, threadPoolExecutor);
    }

    public static HodorExecutor createExecutor(final String executorName, final int threadSize) {
        return new HodorExecutorFactory(executorName, threadSize).executor;
    }

}
