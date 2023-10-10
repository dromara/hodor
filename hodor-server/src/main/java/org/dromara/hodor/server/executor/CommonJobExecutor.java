package org.dromara.hodor.server.executor;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.Host;
import org.dromara.hodor.model.job.JobKey;
import org.dromara.hodor.scheduler.api.AbstractJobExecutor;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;
import org.dromara.hodor.server.executor.dispatch.JobDispatcher;
import org.dromara.hodor.server.executor.exception.IllegalJobExecuteStateException;
import org.dromara.hodor.server.executor.exception.JobScheduleException;
import org.dromara.hodor.server.executor.handler.HodorJobRequestHandler;
import org.dromara.hodor.server.executor.handler.RequestHandler;
import org.dromara.hodor.server.manager.ActuatorNodeManager;
import org.dromara.hodor.server.manager.JobExecuteManager;

/**
 *  common job executor
 *
 * @author tomgs
 * @version 2020/6/25 1.0
 */
@Slf4j
public class CommonJobExecutor extends AbstractJobExecutor {

    private final JobDispatcher dispatcher;

    private final ActuatorNodeManager actuatorNodeManager;

    private final RequestHandler requestHandler;

    public CommonJobExecutor() {
        this.requestHandler = new HodorJobRequestHandler();
        this.dispatcher = new JobDispatcher(requestHandler);
        this.actuatorNodeManager = ActuatorNodeManager.getInstance();
    }

    @Override
    public void preProcess(HodorJobExecutionContext context) {
        // check job is running
        final JobKey jobKey = context.getJobKey();
        if (JobExecuteManager.getInstance().isRunning(jobKey)) {
            throw new IllegalJobExecuteStateException("The job {} is running", jobKey);
        }
        // set actuator hosts
        final List<Host> hosts = actuatorNodeManager.getAvailableHosts(jobKey);
        if (hosts.isEmpty()) {
            throw new JobScheduleException("The job [{}] has no available actuator nodes", jobKey);
        }
        context.resetHosts(hosts);
    }

    @Override
    public void process(final HodorJobExecutionContext context) {
        log.debug("scheduler executor, context: {}.", context);
        JobExecuteManager.getInstance().addSchedulerStartJob(context);
        dispatcher.dispatch(context);
    }

    @Override
    public void exceptionProcess(HodorJobExecutionContext context, Exception e) {
        requestHandler.exceptionCaught(context, e);
    }

}
