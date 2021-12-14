package org.dromara.hodor.actuator.common.executor;

import java.net.SocketAddress;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.actuator.common.JobRegistrar;
import org.dromara.hodor.actuator.common.action.HeartbeatAction;
import org.dromara.hodor.actuator.common.action.JobExecuteAction;
import org.dromara.hodor.actuator.common.action.JobExecuteLogAction;
import org.dromara.hodor.actuator.common.action.JobExecuteStatusAction;
import org.dromara.hodor.actuator.common.action.KillRunningJobAction;
import org.dromara.hodor.actuator.common.config.HodorProperties;
import org.dromara.hodor.common.event.AbstractEventPublisher;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.common.storage.db.DBOperator;
import org.dromara.hodor.common.utils.HostUtils;
import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.HodorChannelFuture;
import org.dromara.hodor.remoting.api.message.Header;
import org.dromara.hodor.remoting.api.message.MessageType;
import org.dromara.hodor.remoting.api.message.RemotingMessage;
import org.dromara.hodor.remoting.api.message.RemotingResponse;
import org.dromara.hodor.remoting.api.message.RequestContext;

/**
 * 请求事件处理manager
 *
 * @author tomgs
 * @version 2021/2/26 1.0
 */
@Slf4j
public class RequestHandleManager extends AbstractEventPublisher<RequestContext> {

    private final ExecutorManager executorManager;

    private final ClientChannelManager clientChannelManager;

    private final FailureRequestHandleManager failureRequestHandleManager;

    private final HodorProperties properties;

    private final JobExecutionPersistence jobExecutionPersistence;

    private final JobRegistrar jobRegistrar;

    public RequestHandleManager(final HodorProperties properties,
                                final ExecutorManager executorManager,
                                final ClientChannelManager clientChannelManager,
                                final DBOperator dbOperator,
                                final JobRegistrar jobRegistrar) {
        this.properties = properties;
        this.executorManager = executorManager;
        this.clientChannelManager = clientChannelManager;
        this.jobExecutionPersistence = new JobExecutionPersistence(dbOperator);
        this.jobRegistrar = jobRegistrar;
        this.failureRequestHandleManager = new FailureRequestHandleManager(clientChannelManager, executorManager, dbOperator);
    }

    @Override
    public void registryListener() {
        registerHeartbeatListener();
        registerJobExecuteListener();
        registerKillRunningListener();
        registerFetchJobStatusListener();
        registerFetchJobExecLogListener();
    }

    private void registerFetchJobExecLogListener() {
        this.addListener(e -> {
            RequestContext context = e.getValue();
            executorManager.commonExecute(new JobExecuteLogAction(context, properties, this));
        }, MessageType.FETCH_JOB_LOG_REQUEST);
    }

    private void registerFetchJobStatusListener() {
        this.addListener(e -> {
            RequestContext context = e.getValue();
            executorManager.commonExecute(new JobExecuteStatusAction(context, jobExecutionPersistence, this));
        }, MessageType.FETCH_JOB_STATUS_REQUEST);
    }

    private void registerKillRunningListener() {
        this.addListener(e -> {
            RequestContext context = e.getValue();
            executorManager.commonExecute(new KillRunningJobAction(context, jobExecutionPersistence, executorManager, this));
        }, MessageType.KILL_JOB_REQUEST);
    }

    private void registerJobExecuteListener() {
        this.addListener(e -> {
            RequestContext context = e.getValue();
            long requestId = context.requestHeader().getId();
            if (!executorManager.executeReady(requestId)) {
                retryableSendMessage(context, RemotingResponse.failed(String.format("RequestId %s has running.", requestId)));
            }
            executorManager.execute(new JobExecuteAction(context, properties, jobExecutionPersistence, jobRegistrar, this));
        }, MessageType.JOB_EXEC_REQUEST);
    }

    private void registerHeartbeatListener() {
        this.addListener(e -> {
            RequestContext context = e.getValue();
            executorManager.commonExecute(new HeartbeatAction(context, this));
        }, MessageType.HEARTBEAT_REQUEST);
    }

    public void notifyRequestHandler(RequestContext request) {
        Event<RequestContext> event = new Event<>(request, request.messageType());
        publish(event);
    }

    public void addRetrySendMessage(SocketAddress socketAddress, RemotingMessage message) {
        // persistence to db
        String remoteIp = HostUtils.getIp(socketAddress);
        failureRequestHandleManager.addFailureRequest(remoteIp, message);
    }

    public void recordActiveChannel(HodorChannel activeChannel) {
        clientChannelManager.addActiveChannel(activeChannel);
    }

    /**
     * 可重试消息发送
     *
     * @param response 消息体
     */
    public <T> void retryableSendMessage(RequestContext context, RemotingResponse<T> response) {
        RemotingMessage message = buildResponseMessage(context, response);
        sendMessage(context, message).operationComplete(future -> {
            if (!future.isSuccess() || future.cause() != null) {
                log.warn("response failed.", future.cause());
                addRetrySendMessage(future.channel().remoteAddress(), message);
            } else {
                recordActiveChannel(future.channel());
            }
        });
    }

    public HodorChannelFuture sendMessage(RequestContext context, RemotingMessage message) {
        return context.channel().send(message);
    }

    public void sendMessageThenClose(RequestContext context, RemotingMessage message) {
        context.channel().send(message).operationComplete(e -> e.channel().close());
    }

    public <T> RemotingMessage buildResponseMessage(RequestContext context, RemotingResponse<T> response) {
        byte[] body = context.serializer().serialize(response);
        Header header = Header.builder()
            .id(context.requestHeader().getId())
            .type(context.requestHeader().getType())
            .version(context.requestHeader().getVersion())
            .attachment(context.requestHeader().getAttachment())
            .length(body.length)
            .build();
        return RemotingMessage
            .builder()
            .header(header)
            .body(body)
            .build();
    }

}
