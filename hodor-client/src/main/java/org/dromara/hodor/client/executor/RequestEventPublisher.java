package org.dromara.hodor.client.executor;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import org.dromara.hodor.client.action.HeartbeatAction;
import org.dromara.hodor.client.action.JobExecuteAction;
import org.dromara.hodor.client.action.JobExecuteLogAction;
import org.dromara.hodor.client.action.JobExecuteStatusAction;
import org.dromara.hodor.client.action.KillRunningJobAction;
import org.dromara.hodor.client.core.RequestContext;
import org.dromara.hodor.common.event.AbstractEventPublisher;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.remoting.api.message.MessageType;
import org.dromara.hodor.remoting.api.message.RemotingMessage;
import org.dromara.hodor.remoting.api.message.request.HeartbeatRequest;
import org.dromara.hodor.remoting.api.message.request.JobExecuteLogRequest;
import org.dromara.hodor.remoting.api.message.request.JobExecuteRequest;
import org.dromara.hodor.remoting.api.message.request.JobExecuteStatusRequest;
import org.dromara.hodor.remoting.api.message.request.KillRunningJobRequest;

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
        //TODO: 发送失败消息处理
    }

    private void registerFetchJobExecLogListener() {
        this.addListener(e -> {
            RequestContext context = e.getValue();
            context.setRequestType(JobExecuteLogRequest.class);
            executorManager.commonExecute(new JobExecuteLogAction(context));
        }, MessageType.FETCH_JOB_LOG_REQUEST);
    }

    private void registerFetchJobStatusListener() {
        this.addListener(e -> {
            RequestContext context = e.getValue();
            context.setRequestType(JobExecuteStatusRequest.class);
            executorManager.commonExecute(new JobExecuteStatusAction(context));
        }, MessageType.FETCH_JOB_STATUS_REQUEST);
    }

    private void registerKillRunningListener() {
        this.addListener(e -> {
            RequestContext context = e.getValue();
            context.setRequestType(KillRunningJobRequest.class);
            executorManager.commonExecute(new KillRunningJobAction(context));
        }, MessageType.KILL_JOB_REQUEST);
    }

    private void registerRequestExecuteListener() {
        this.addListener(e -> {
            RequestContext context = e.getValue();
            context.setRequestType(JobExecuteRequest.class);
            executorManager.execute(new JobExecuteAction(context));
        }, MessageType.JOB_EXEC_REQUEST);
    }

    private void registerHeartbeatListener() {
        this.addListener(e -> {
            RequestContext context = e.getValue();
            context.setRequestType(HeartbeatRequest.class);
            executorManager.execute(new HeartbeatAction(context));
        }, MessageType.HEARTBEAT_REQUEST);
    }

    public void notifyRequestHandler(RequestContext request) {
        Event<RequestContext> event = new Event<>(request, request.messageType());
        publish(event);
    }

    public void fireRetrySendMessage(Long requestId, SocketAddress socketAddress, RemotingMessage message) {
        // persistence to db
        InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
        String endpoint = inetSocketAddress.getAddress().getHostAddress() + ":" + inetSocketAddress.getPort();

    }

}
