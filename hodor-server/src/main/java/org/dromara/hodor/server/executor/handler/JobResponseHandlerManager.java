package org.dromara.hodor.server.executor.handler;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.event.AbstractEventPublisher;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.remoting.api.message.RemotingResponse;
import org.dromara.hodor.remoting.api.message.RemotingStatus;
import org.dromara.hodor.remoting.api.message.response.JobExecuteResponse;

/**
 * response handler
 *
 * @author tomgs
 * @since 2021/4/7
 */
@Slf4j
public class JobResponseHandlerManager extends AbstractEventPublisher<RemotingResponse<JobExecuteResponse>> {

    public static final JobResponseHandlerManager INSTANCE = new JobResponseHandlerManager();

    private JobResponseHandlerManager() {
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
            // TODO: 记录成功的任务请求
            log.info("job request execute response {}.", jobExecuteResponse);
        }, RemotingStatus.SUCCEEDED);
    }

    private void registerJobExecuteFailureResponseListener() {
        this.addListener(event -> {
            RemotingResponse<JobExecuteResponse> remotingResponse = event.getValue();
            Long requestId = remotingResponse.getRequestId();
            String errorMsg = remotingResponse.getMsg();
            // TODO: 记录失败的任务请求
            log.error("job request {} execute failure, msg: {}.", requestId, errorMsg);
        }, RemotingStatus.FAILED);
    }

    public void fireJobResponseHandler(final RemotingResponse<JobExecuteResponse> remotingResponse) {
        Event<RemotingResponse<JobExecuteResponse>> event = new Event<>(remotingResponse, remotingResponse.getCode());
        publish(event);
    }

    public void fireJobExecuteInnerErrorHandler(final JobExecuteResponse jobExecuteResponse) {
        RemotingResponse<JobExecuteResponse> remotingResponse = RemotingResponse.succeeded(jobExecuteResponse.getRequestId(), jobExecuteResponse);
        fireJobResponseHandler(remotingResponse);
    }

}
