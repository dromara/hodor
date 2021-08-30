package org.dromara.hodor.server.executor.handler;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.event.AbstractEventPublisher;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.remoting.api.message.RemotingResponse;
import org.dromara.hodor.remoting.api.message.RemotingStatus;
import org.dromara.hodor.remoting.api.message.response.JobExecuteResponse;
import org.dromara.hodor.server.manager.JobExecuteStatusManager;

/**
 * response handler
 *
 * @author tomgs
 * @since 2021/4/7
 */
@Slf4j
public class HodorJobResponseHandler extends AbstractEventPublisher<RemotingResponse<JobExecuteResponse>> {

    public static final HodorJobResponseHandler INSTANCE = new HodorJobResponseHandler();

    private HodorJobResponseHandler() {
    }

    @Override
    public void registerListener() {
        registerJobExecuteSuccessResponseListener();
        registerJobExecuteFailureResponseListener();
    }

    private void registerJobExecuteSuccessResponseListener() {
        this.addListener(event -> {
            RemotingResponse<JobExecuteResponse> remotingResponse = event.getValue();
            JobExecuteResponse jobExecuteResponse = remotingResponse.getData();
            JobExecuteStatusManager.getInstance().addFinishJob(jobExecuteResponse);
        }, RemotingStatus.SUCCEEDED);
    }

    private void registerJobExecuteFailureResponseListener() {
        this.addListener(event -> {
            RemotingResponse<JobExecuteResponse> remotingResponse = event.getValue();
            JobExecuteStatusManager.getInstance().addFinishJob(remotingResponse.getData());
        }, RemotingStatus.FAILED);
    }

    public void fireJobResponseHandler(final RemotingResponse<JobExecuteResponse> remotingResponse) {
        Event<RemotingResponse<JobExecuteResponse>> event = new Event<>(remotingResponse, remotingResponse.getCode());
        publish(event);
    }

    public void fireJobExecuteInnerErrorHandler(final JobExecuteResponse jobExecuteResponse) {
        RemotingResponse<JobExecuteResponse> remotingResponse = RemotingResponse.failed("inner error", jobExecuteResponse);
        fireJobResponseHandler(remotingResponse);
    }

}
