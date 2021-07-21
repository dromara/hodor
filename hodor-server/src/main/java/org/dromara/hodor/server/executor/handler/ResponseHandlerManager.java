package org.dromara.hodor.server.executor.handler;

import org.dromara.hodor.common.event.AbstractEventPublisher;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.remoting.api.message.MessageType;
import org.dromara.hodor.remoting.api.message.RemotingMessage;

/**
 * response handler
 *
 * @author tomgs
 * @since 2021/4/7
 */
public class ResponseHandlerManager extends AbstractEventPublisher<RemotingMessage> {

    public static final ResponseHandlerManager INSTANCE = new ResponseHandlerManager();

    private ResponseHandlerManager() {

    }

    @Override
    public void registerListener() {
        registerJobExecuteResponseListener();
        registerHeartbeatResponseListener();
        registerKillRunningResponseListener();
        registerFetchJobStatusResponseListener();
        registerFetchJobExecLogResponseListener();
    }

    private void registerJobExecuteResponseListener() {
        this.addListener(event -> {
            RemotingMessage message = event.getValue();

        }, MessageType.JOB_EXEC_REQUEST);
    }

    private void registerHeartbeatResponseListener() {
        this.addListener(event -> {
            RemotingMessage message = event.getValue();

        }, MessageType.HEARTBEAT_REQUEST);
    }

    private void registerKillRunningResponseListener() {
        this.addListener(event -> {
            RemotingMessage message = event.getValue();

        }, MessageType.KILL_JOB_REQUEST);
    }

    private void registerFetchJobStatusResponseListener() {
        this.addListener(event -> {
            RemotingMessage message = event.getValue();

        }, MessageType.FETCH_JOB_STATUS_REQUEST);
    }

    private void registerFetchJobExecLogResponseListener() {
        this.addListener(event -> {
            RemotingMessage message = event.getValue();

        }, MessageType.FETCH_JOB_LOG_REQUEST);
    }

    public void fireJobResponseHandler(RemotingMessage message) {
        Event<RemotingMessage> event = new Event<>(message, MessageType.to(message.getHeader().getType()));
        publish(event);
    }

}
