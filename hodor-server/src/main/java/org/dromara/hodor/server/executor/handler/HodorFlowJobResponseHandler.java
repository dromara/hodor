package org.dromara.hodor.server.executor.handler;

import org.dromara.hodor.common.dag.Dag;
import org.dromara.hodor.common.dag.Node;
import org.dromara.hodor.common.dag.Status;
import org.dromara.hodor.common.event.AbstractEventPublisher;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.common.utils.Pair;
import org.dromara.hodor.common.utils.Utils.Assert;
import org.dromara.hodor.model.enums.JobExecuteStatus;
import org.dromara.hodor.model.job.JobKey;
import org.dromara.hodor.remoting.api.message.RemotingResponse;
import org.dromara.hodor.remoting.api.message.RemotingStatus;
import org.dromara.hodor.remoting.api.message.response.JobExecuteResponse;
import org.dromara.hodor.server.executor.FlowJobExecutorManager;
import org.dromara.hodor.server.manager.JobExecuteManager;

/**
 * HodorFlowJobResponseHandler
 *
 * @author tomgs
 * @since 1.0
 */
public class HodorFlowJobResponseHandler extends AbstractEventPublisher<Pair<JobKey, JobExecuteResponse>>
        implements ResponseHandler<Pair<JobKey, RemotingResponse<JobExecuteResponse>>> {

    public static final HodorFlowJobResponseHandler INSTANCE = new HodorFlowJobResponseHandler();

    private final FlowJobExecutorManager flowJobExecutorManager;

    private HodorFlowJobResponseHandler() {
        this.flowJobExecutorManager = FlowJobExecutorManager.getInstance();
    }

    @Override
    public void registryListener() {
        registerSubJobExecuteSuccessResponseListener();
        registerSubJobExecuteFailureResponseListener();
    }

    private void registerSubJobExecuteSuccessResponseListener() {
        this.addListener(event -> changeNodeStatus(event, Status.SUCCESS), RemotingStatus.SUCCEEDED);
    }

    private void registerSubJobExecuteFailureResponseListener() {
        this.addListener(event -> changeNodeStatus(event, Status.FAILURE), RemotingStatus.FAILED);
    }

    private void changeNodeStatus(Event<Pair<JobKey, JobExecuteResponse>> event, Status status) {
        Pair<JobKey, JobExecuteResponse> pair = event.getValue();
        JobKey rootJobKey = pair.getFirst();
        JobExecuteResponse jobExecuteResponse = pair.getSecond();
        if (!JobExecuteStatus.isFinished(jobExecuteResponse.getStatus())) {
            return;
        }
        try {
            // notify execute next
            Dag dagInstance = flowJobExecutorManager.getDagInstance(rootJobKey);
            Assert.notNull(dagInstance, "not found dag instance by root key {}.", rootJobKey);
            Node node = dagInstance.getNode(jobExecuteResponse.getJobKey().getGroupName(), jobExecuteResponse.getJobKey().getJobName());
            flowJobExecutorManager.changeNodeStatus(node, status);
        } finally {
            JobExecuteManager.getInstance().addFinishJob(jobExecuteResponse);
        }
    }

    public void fireJobResponseHandler(Pair<JobKey, RemotingResponse<JobExecuteResponse>> pair) {
        publish(Event.create(new Pair<>(pair.getFirst(), pair.getSecond().getData()), pair.getSecond().getCode()));
    }

}
