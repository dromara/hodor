package org.dromara.hodor.server.execute;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;
import org.dromara.hodor.scheduler.api.JobRequestExecutorManager;
import org.dromara.hodor.scheduler.api.executor.AbstractJobExecutor;
import org.dromara.hodor.server.execute.handler.HodorJobRequestHandler;

/**
 *  common job executor
 *
 * @author tomgs
 * @version 2020/6/25 1.0 
 */
@Slf4j
public class CommonJobExecutor extends AbstractJobExecutor {

    private final JobRequestExecutorManager requestExecutor = JobRequestExecutorManager.getInstance();
    private final HodorJobRequestHandler handler = new HodorJobRequestHandler();

    @Override
    public void process(final HodorJobExecutionContext context) {
        log.info("scheduler executor, context: {}.", context);
        requestExecutor.submit(context, handler);
    }

}
