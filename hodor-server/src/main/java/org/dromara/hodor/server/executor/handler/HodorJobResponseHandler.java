package org.dromara.hodor.server.executor.handler;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.event.AbstractEventPublisher;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.remoting.api.message.RemotingResponse;
import org.dromara.hodor.remoting.api.message.RemotingStatus;
import org.dromara.hodor.remoting.api.message.response.JobExecuteResponse;
import org.dromara.hodor.server.manager.JobExecuteManager;

/**
 * response handler
 *
 * @author tomgs
 * @since 2021/4/7
 */
@Slf4j
public class HodorJobResponseHandler extends AbstractEventPublisher<JobExecuteResponse> implements ResponseHandler<RemotingResponse<JobExecuteResponse>> {

    public static final HodorJobResponseHandler INSTANCE = new HodorJobResponseHandler();

    private HodorJobResponseHandler() {
    }

    @Override
    public void registryListener() {
        registerJobExecuteSuccessResponseListener();
        registerJobExecuteFailureResponseListener();
    }

    private void registerJobExecuteSuccessResponseListener() {
        this.addListener(event -> {
            JobExecuteResponse jobExecuteResponse = event.getValue();
            JobExecuteManager.getInstance().addFinishJob(jobExecuteResponse);
        }, RemotingStatus.SUCCEEDED);
    }

    private void registerJobExecuteFailureResponseListener() {
        this.addListener(event -> {
            JobExecuteResponse jobExecuteResponse = event.getValue();
            JobExecuteManager.getInstance().addFinishJob(jobExecuteResponse);
        }, RemotingStatus.FAILED);
    }

    @Override
    public void fireJobResponseHandler(final RemotingResponse<JobExecuteResponse> remotingResponse) {
        Event<JobExecuteResponse> event = new Event<>(remotingResponse.getData(), remotingResponse.getCode());
        publish(event);
    }

}
