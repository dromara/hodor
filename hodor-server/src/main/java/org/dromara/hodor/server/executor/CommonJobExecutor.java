package org.dromara.hodor.server.executor;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.scheduler.api.AbstractJobExecutor;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;
import org.dromara.hodor.server.ServiceProvider;
import org.dromara.hodor.server.executor.handler.HodorJobRequestHandler;

/**
 *  common job executor
 *
 * @author tomgs
 * @version 2020/6/25 1.0 
 */
@Slf4j
public class CommonJobExecutor extends AbstractJobExecutor {

    private final JobDispatcher dispatcher;

    public CommonJobExecutor() {
        this.dispatcher = new JobDispatcher(new HodorJobRequestHandler());
    }

    @Override
    public void process(final HodorJobExecutionContext context) {
        log.debug("scheduler executor, context: {}.", context);
        dispatcher.dispatch(context);
    }

}
