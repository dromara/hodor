package org.dromara.hodor.scheduler.api.executor;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.core.JobDesc;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;

/**
 *  common job executor
 *
 * @author tomgs
 * @version 2020/6/25 1.0 
 */
@Slf4j
public class CommonJobExecutor extends AbstractJobExecutor {

    @Override
    public void process(HodorJobExecutionContext context) {
        log.info("scheduler executor, context: {}.", context);
        long requestId = context.getRequestId();
        JobDesc jobDesc = context.getJobDesc();

    }

}
