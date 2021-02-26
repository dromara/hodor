package org.dromara.hodor.client.executor;

import org.dromara.hodor.client.action.HeartbeatAction;
import org.dromara.hodor.client.action.JobExecuteAction;
import org.dromara.hodor.client.core.RequestContext;
import org.dromara.hodor.common.event.AbstractEventPublisher;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.remoting.api.message.MessageType;

/**
 *  请求事件分发器
 *
 * @author tomgs
 * @version 2021/2/26 1.0 
 */

public class RequestEventPublisher extends AbstractEventPublisher<RequestContext> {

    private final ExecutorManager executorManager;

    public RequestEventPublisher() {
        executorManager = ExecutorManager.getInstance();
    }

    @Override
    public void registerListener() {
        registerHeartbeatListener();
        registerRequestExecuteListener();
        registerKillRunningListener();
        registerFetchJobStatusListener();
        registerFetchJobExecLogListener();
        registerFailureHandlerListener();
    }

    private void registerFailureHandlerListener() {

    }

    private void registerFetchJobExecLogListener() {

    }

    private void registerFetchJobStatusListener() {

    }

    private void registerKillRunningListener() {

    }

    private void registerRequestExecuteListener() {
        this.addListener(e -> {
            RequestContext context = e.getValue();
            executorManager.submit(new JobExecuteAction(context));
        }, MessageType.HEARTBEAT_REQUEST);
    }

    private void registerHeartbeatListener() {
        this.addListener(e -> {
            RequestContext context = e.getValue();
            executorManager.submit(new HeartbeatAction(context));
        }, MessageType.HEARTBEAT_REQUEST);
    }

    public void notifyRequestHandler(RequestContext request) {
        Event<RequestContext> event = new Event<>(request, request.messageType());
        publish(event);
    }

}
