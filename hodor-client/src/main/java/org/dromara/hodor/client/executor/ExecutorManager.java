package org.dromara.hodor.client.executor;

import java.util.concurrent.ThreadPoolExecutor;
import org.dromara.hodor.common.executor.HodorExecutor;
import org.dromara.hodor.common.executor.HodorExecutorFactory;
import org.dromara.hodor.common.executor.HodorRunnable;
import org.dromara.hodor.common.queue.CircleQueue;
import org.dromara.hodor.common.queue.DiscardOldestElementPolicy;

/**
 * executor manager
 *
 * @author tomgs
 * @since 2021/2/26
 */
public class ExecutorManager {

    private static final ExecutorManager INSTANCE = new ExecutorManager();

    private final HodorExecutor hodorExecutor;

    private ExecutorManager() {
        final int threadSize = Runtime.getRuntime().availableProcessors() * 2;
        final ThreadPoolExecutor threadPoolExecutor = HodorExecutorFactory.createThreadPoolExecutor("job-exec", threadSize);
        this.hodorExecutor = new HodorExecutor();

        hodorExecutor.setCircleQueue(new CircleQueue<>(100));
        hodorExecutor.setExecutor(threadPoolExecutor);
        hodorExecutor.setRejectEnqueuePolicy(new DiscardOldestElementPolicy<>());
    }

    public static ExecutorManager getInstance() {
        return INSTANCE;
    }

    public void submit(final HodorRunnable runnable) {
        hodorExecutor.parallelExecute(runnable);
    }

}
