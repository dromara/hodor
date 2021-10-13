package org.dromara.hodor.client.executor;

import java.net.SocketAddress;
import org.dromara.hodor.client.JobRegistrar;
import org.dromara.hodor.client.ServiceProvider;
import org.dromara.hodor.client.action.HeartbeatAction;
import org.dromara.hodor.client.action.JobExecuteAction;
import org.dromara.hodor.client.action.JobExecuteLogAction;
import org.dromara.hodor.client.action.JobExecuteStatusAction;
import org.dromara.hodor.client.action.KillRunningJobAction;
import org.dromara.hodor.client.config.HodorProperties;
import org.dromara.hodor.common.event.AbstractEventPublisher;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.common.utils.HostUtils;
import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.message.MessageType;
import org.dromara.hodor.remoting.api.message.RemotingMessage;
import org.dromara.hodor.remoting.api.message.RequestContext;

/**
 * 请求事件处理manager
 *
 * @author tomgs
 * @version 2021/2/26 1.0
 */
public class RequestHandleManager extends AbstractEventPublisher<RequestContext> {

    private final ExecutorManager executorManager;

    private final ClientChannelManager clientChannelManager;

    private final FailureRequestHandleManager failureRequestHandleManager;

    private final JobRegistrar jobRegistrar;

    private final JobExecutionPersistence jobExecutionPersistence;

    private final HodorProperties properties;

    public RequestHandleManager() {
        this.executorManager = ExecutorManager.getInstance();
        this.clientChannelManager = ClientChannelManager.getInstance();
        this.failureRequestHandleManager = FailureRequestHandleManager.getInstance();
        this.jobRegistrar = ServiceProvider.getInstance().getBean(JobRegistrar.class);
        this.jobExecutionPersistence = ServiceProvider.getInstance().getBean(JobExecutionPersistence.class);
        this.properties = ServiceProvider.getInstance().getBean(HodorProperties.class);
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
            executorManager.commonExecute(new JobExecuteLogAction(context, properties));
        }, MessageType.FETCH_JOB_LOG_REQUEST);
    }

    private void registerFetchJobStatusListener() {
        this.addListener(e -> {
            RequestContext context = e.getValue();
            executorManager.commonExecute(new JobExecuteStatusAction(context, jobExecutionPersistence));
        }, MessageType.FETCH_JOB_STATUS_REQUEST);
    }

    private void registerKillRunningListener() {
        this.addListener(e -> {
            RequestContext context = e.getValue();
            executorManager.commonExecute(new KillRunningJobAction(context, jobExecutionPersistence));
        }, MessageType.KILL_JOB_REQUEST);
    }

    private void registerJobExecuteListener() {
        this.addListener(e -> {
            RequestContext context = e.getValue();
            executorManager.execute(new JobExecuteAction(context, properties, jobExecutionPersistence, jobRegistrar));
        }, MessageType.JOB_EXEC_REQUEST);
    }

    private void registerHeartbeatListener() {
        this.addListener(e -> {
            RequestContext context = e.getValue();
            executorManager.commonExecute(new HeartbeatAction(context));
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

}
