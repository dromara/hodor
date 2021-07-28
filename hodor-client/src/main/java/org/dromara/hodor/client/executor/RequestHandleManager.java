package org.dromara.hodor.client.executor;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.dromara.hodor.client.JobRegistrar;
import org.dromara.hodor.client.ServiceProvider;
import org.dromara.hodor.client.action.HeartbeatAction;
import org.dromara.hodor.client.action.JobExecuteAction;
import org.dromara.hodor.client.action.JobExecuteLogAction;
import org.dromara.hodor.client.action.JobExecuteStatusAction;
import org.dromara.hodor.client.action.KillRunningJobAction;
import org.dromara.hodor.client.config.HodorProperties;
import org.dromara.hodor.client.core.RequestContext;
import org.dromara.hodor.common.event.AbstractEventPublisher;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.common.utils.HostUtils;
import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.message.MessageType;
import org.dromara.hodor.remoting.api.message.RemotingMessage;
import org.dromara.hodor.remoting.api.message.request.HeartbeatRequest;
import org.dromara.hodor.remoting.api.message.request.JobExecuteLogRequest;
import org.dromara.hodor.remoting.api.message.request.JobExecuteRequest;
import org.dromara.hodor.remoting.api.message.request.JobExecuteStatusRequest;
import org.dromara.hodor.remoting.api.message.request.KillRunningJobRequest;

/**
 *  请求事件处理manager
 *
 * @author tomgs
 * @version 2021/2/26 1.0 
 */
public class RequestHandleManager extends AbstractEventPublisher<RequestContext> {

    private final ExecutorManager executorManager;

    private final FailureRequestHandleManager failureRequestHandleManager;

    private final Map<String, HodorChannel> activeChannels;

    private final JobRegistrar jobRegistrar;

    private final JobExecutionPersistence jobExecutionPersistence;

    private final HodorProperties properties;

    public RequestHandleManager() {
        this.executorManager = ExecutorManager.getInstance();
        this.failureRequestHandleManager = FailureRequestHandleManager.getInstance();
        this.activeChannels = new ConcurrentHashMap<>();
        this.jobRegistrar = ServiceProvider.getInstance().getBean(JobRegistrar.class);
        this.jobExecutionPersistence = ServiceProvider.getInstance().getBean(JobExecutionPersistence.class);
        this.properties = ServiceProvider.getInstance().getBean(HodorProperties.class);
    }

    @Override
    public void registerListener() {
        registerHeartbeatListener();
        registerJobExecuteListener();
        registerKillRunningListener();
        registerFetchJobStatusListener();
        registerFetchJobExecLogListener();
    }

    private void registerFetchJobExecLogListener() {
        this.addListener(e -> {
            RequestContext context = e.getValue();
            context.setRequestType(JobExecuteLogRequest.class);
            executorManager.commonExecute(new JobExecuteLogAction(context, properties));
        }, MessageType.FETCH_JOB_LOG_REQUEST);
    }

    private void registerFetchJobStatusListener() {
        this.addListener(e -> {
            RequestContext context = e.getValue();
            context.setRequestType(JobExecuteStatusRequest.class);
            executorManager.commonExecute(new JobExecuteStatusAction(context, jobExecutionPersistence));
        }, MessageType.FETCH_JOB_STATUS_REQUEST);
    }

    private void registerKillRunningListener() {
        this.addListener(e -> {
            RequestContext context = e.getValue();
            context.setRequestType(KillRunningJobRequest.class);
            executorManager.commonExecute(new KillRunningJobAction(context, jobExecutionPersistence));
        }, MessageType.KILL_JOB_REQUEST);
    }

    private void registerJobExecuteListener() {
        this.addListener(e -> {
            RequestContext context = e.getValue();
            context.setRequestType(JobExecuteRequest.class);
            executorManager.execute(new JobExecuteAction(context, properties, jobExecutionPersistence, jobRegistrar));
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

    public void addRetrySendMessage(SocketAddress socketAddress, RemotingMessage message) {
        // persistence to db
        String remoteIp = HostUtils.getIp(socketAddress);
        /*
         * 优先发送到对应remoteIp的Channel，如果没有则可以随便发送到一个activeChannel，由调度端去转发响应的请求即可。
         */
        HodorChannel activeChannel = activeChannels.get(remoteIp);
        if (activeChannel == null || !activeChannel.isOpen()) {
            activeChannels.remove(remoteIp);
        }
        for (HodorChannel backupChannel : activeChannels.values()) {
            if (backupChannel.isOpen()) {
                activeChannel = backupChannel;
                break;
            }
        }
        failureRequestHandleManager.addFailureRequest(remoteIp, activeChannel, message);
    }

    public void recordActiveChannel(HodorChannel activeChannel) {
        SocketAddress remoteAddress = activeChannel.remoteAddress();
        InetSocketAddress inetSocketAddress = (InetSocketAddress) remoteAddress;
        String remoteIp = inetSocketAddress.getAddress().getHostAddress();
        activeChannels.put(remoteIp, activeChannel);
        failureRequestHandleManager.fireFailureRequestHandler(remoteIp, activeChannel);
    }

}
