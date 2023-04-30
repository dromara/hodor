package org.dromara.hodor.actuator.api.executor;

import org.dromara.hodor.actuator.api.config.HodorProperties;
import org.dromara.hodor.common.executor.HodorExecutor;
import org.dromara.hodor.common.executor.HodorExecutorFactory;
import org.dromara.hodor.common.executor.HodorRunnable;

/**
 * executor manager
 *
 * @author tomgs
 * @since 1.0
 */
public class ExecutorManager {

    private final HodorExecutor hodorExecutor;

    private final HodorExecutor commonExecutor;

    public ExecutorManager(final HodorProperties properties) {
        final int threadSize = properties.getThreadSize() > 0 ? properties.getThreadSize() : Runtime.getRuntime().availableProcessors() * 2;
        // request job
        hodorExecutor = HodorExecutorFactory.createExecutor("job-exec", threadSize, properties.getQueueSize(), false,
            properties.getTaskStackingStrategy());
        // heartbeat, fetch log, job status, kill job
        commonExecutor = HodorExecutorFactory.createDefaultExecutor("common-exec", threadSize / 2, true);
    }

    public void execute(final HodorRunnable runnable) {
        //hodorExecutor.serialExecute(runnable);
        hodorExecutor.parallelExecute(runnable);
    }

    public void commonExecute(final HodorRunnable runnable) {
        commonExecutor.parallelExecute(runnable);
    }

    public HodorExecutor getHodorExecutor() {
        return hodorExecutor;
    }

    public HodorExecutor getCommonExecutor() {
        return commonExecutor;
    }

}
