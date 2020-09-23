package org.dromara.hodor.scheduler.api.executor;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.disruptor.QueueProviderManager;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;
import org.dromara.hodor.scheduler.api.JobRequestExecutorManager;

/**
 *  common job executor
 *
 * @author tomgs
 * @version 2020/6/25 1.0 
 */
@Slf4j
public class CommonJobExecutor extends AbstractJobExecutor {

    private final JobRequestExecutorManager requestExecutor = JobRequestExecutorManager.getInstance();

    @Override
    public void process(final HodorJobExecutionContext context) {
        log.info("scheduler executor, context: {}.", context);

        requestExecutor.submitHodorJobExecutor(context);
    }

}
