package org.dromara.hodor.server.executor.handler;

import org.dromara.hodor.common.dag.Status;
import org.dromara.hodor.common.event.AbstractEventPublisher;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.remoting.api.message.RemotingResponse;
import org.dromara.hodor.remoting.api.message.RemotingStatus;
import org.dromara.hodor.remoting.api.message.response.JobExecuteResponse;
import org.dromara.hodor.server.executor.FlowJobExecutorManager;
import org.dromara.hodor.server.manager.JobExecuteManager;

/**
 * HodorFlowJobResponseHandler
 *
 * @author tomgs
 * @since 2021/9/8
 */
public class HodorFlowJobResponseHandler extends AbstractEventPublisher<RemotingResponse<JobExecuteResponse>> implements ResponseHandler {

    public static final HodorFlowJobResponseHandler INSTANCE = new HodorFlowJobResponseHandler();

    private final FlowJobExecutorManager flowJobExecutorManager;

    public HodorFlowJobResponseHandler() {
        this.flowJobExecutorManager = FlowJobExecutorManager.getInstance();
    }

    @Override
    public void registerListener() {
        registerSubJobExecuteSuccessResponseListener();
        registerSubJobExecuteFailureResponseListener();
    }

    private void registerSubJobExecuteSuccessResponseListener() {
        this.addListener(event -> {
            RemotingResponse<JobExecuteResponse> remotingResponse = event.getValue();
            JobExecuteResponse jobExecuteResponse = remotingResponse.getData();
            JobExecuteManager.getInstance().addFinishJob(jobExecuteResponse);

            // notify execute next
            flowJobExecutorManager.changeNodeStatus(jobExecuteResponse.getJobKey(), Status.SUCCESS);
        }, RemotingStatus.SUCCEEDED);
    }

    private void registerSubJobExecuteFailureResponseListener() {
        this.addListener(event -> {
            RemotingResponse<JobExecuteResponse> remotingResponse = event.getValue();
            JobExecuteResponse jobExecuteResponse = remotingResponse.getData();
            JobExecuteManager.getInstance().addFinishJob(jobExecuteResponse);

            flowJobExecutorManager.changeNodeStatus(jobExecuteResponse.getJobKey(), Status.FAILURE);
        }, RemotingStatus.FAILED);
    }

    @Override
    public void fireJobResponseHandler(RemotingResponse<JobExecuteResponse> remotingResponse) {
        Event<RemotingResponse<JobExecuteResponse>> event = new Event<>(remotingResponse, remotingResponse.getCode());
        publish(event);
    }

}
