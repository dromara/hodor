package org.dromara.hodor.server.executor.dispatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.Host;
import org.dromara.hodor.common.concurrent.FutureCallback;
import org.dromara.hodor.common.executor.HodorExecutor;
import org.dromara.hodor.common.executor.HodorExecutorFactory;
import org.dromara.hodor.common.executor.HodorRunnable;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.common.loadbalance.LoadBalance;
import org.dromara.hodor.common.loadbalance.LoadBalanceFactory;
import org.dromara.hodor.common.queue.DiscardOldestElementPolicy;
import org.dromara.hodor.core.Constants;
import org.dromara.hodor.model.enums.ScheduleStrategy;
import org.dromara.hodor.model.job.JobDesc;
import org.dromara.hodor.model.job.JobKey;
import org.dromara.hodor.remoting.api.RemotingClient;
import org.dromara.hodor.remoting.api.RemotingConst;
import org.dromara.hodor.remoting.api.RemotingMessageSerializer;
import org.dromara.hodor.remoting.api.message.Header;
import org.dromara.hodor.remoting.api.message.MessageType;
import org.dromara.hodor.remoting.api.message.RemotingMessage;
import org.dromara.hodor.remoting.api.message.RemotingResponse;
import org.dromara.hodor.remoting.api.message.request.JobExecuteRequest;
import org.dromara.hodor.remoting.api.message.response.JobExecuteResponse;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;
import org.dromara.hodor.server.executor.exception.JobScheduleException;
import org.dromara.hodor.server.executor.handler.RequestHandler;
import org.dromara.hodor.server.manager.JobExecuteManager;
import org.jetbrains.annotations.NotNull;

/**
 * 任务分发器
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class JobDispatcher {

    private static final Map<JobKey, HodorExecutor> hodorExecutorMap = new ConcurrentHashMap<>();

    private final ThreadPoolExecutor threadPoolExecutor;

    private final RequestHandler requestHandler;

    private final RemotingClient clientService;

    private final RemotingMessageSerializer serializer;

    private final FutureCallback<RemotingMessage> futureCallback;

    public JobDispatcher(final RequestHandler requestHandler) {
        final int threadSize = Runtime.getRuntime().availableProcessors() * 2;
        this.threadPoolExecutor = HodorExecutorFactory.createThreadPoolExecutor("job-dispatcher", threadSize, 500, false);
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(2);
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(this::clearUnavailableExecutor, 1, 1, TimeUnit.HOURS);
        this.requestHandler = requestHandler;
        this.clientService = new RemotingClient();
        this.futureCallback = getResponseCallback();
        this.serializer = ExtensionLoader.getExtensionLoader(RemotingMessageSerializer.class).getDefaultJoin();
    }

    /**
     * 分发任务
     *
     * @param context 任务上下文参数
     */
    public void dispatch(final HodorJobExecutionContext context) {
        HodorExecutor hodorExecutor = hodorExecutorMap.computeIfAbsent(context.getJobKey(), this::createHodorExecutor);
        Objects.requireNonNull(hodorExecutor, context.getJobKey() + " hodor executor must be not null")
            .serialExecute(new HodorRunnable() {
            @Override
            public void execute() {
                try {
                    doDispatch(context);
                } catch (Throwable t) {
                    requestHandler.exceptionHandle(context, t);
                }
            }
        });
    }

    private void doDispatch(HodorJobExecutionContext context) {
        log.info("Job [key:{}, id:{}] dispatch begins, details: {}", context.getJobKey(), context.getRequestId(), context);
        selectHost(context);

        JobExecuteManager.getInstance().addSchedulerStartJob(context);
        Exception jobException = null;
        final RemotingMessage request = getRequestBody(context);
        final List<Host> hosts = context.getHosts();
        for (int i = hosts.size() - 1; i >= 0; i--) {
            Host host = hosts.get(i);
            try {
                clientService.sendBidiRequest(host, request, futureCallback);
                JobExecuteManager.getInstance().addSchedulerEndJob(context, host);
                jobException = null;
                break;
            } catch (Exception e) {
                jobException = e;
            }
            // 广播任务可能需要将此设置为false
            if (!context.getJobDesc().getFailover()) {
                break;
            }
        }
        if (jobException != null) {
            requestHandler.exceptionHandle(context, jobException);
        }
        log.info("Job [key:{}, id:{}] dispatch success", context.getJobKey(), context.getRequestId());
    }

    @NotNull
    @SuppressWarnings("unchecked")
    private FutureCallback<RemotingMessage> getResponseCallback() {
        return new FutureCallback<RemotingMessage>() {
            @Override
            public void onSuccess(RemotingMessage response) {
                Header header = response.getHeader();
                RemotingResponse<JobExecuteResponse> remotingResponse = serializer.deserialize(response.getBody(), RemotingResponse.class);
                requestHandler.resultHandle(header.getAttachment(), remotingResponse);
            }

            @Override
            public void onFailure(Throwable cause) {
                log.error("HodorJobRequestHandler exceptionCaught : {}", cause.getMessage(), cause);
            }
        };
    }

    private static void selectHost(HodorJobExecutionContext context) {
        final JobDesc jobDesc = context.getJobDesc();
        final List<Host> hosts = context.getHosts();
        Host selected;
        if (jobDesc.getScheduleStrategy() == ScheduleStrategy.SPECIFY) {
            final String scheduleExp = jobDesc.getScheduleExp();
            // 检查是否合法ip:port
            final Host host = Host.of(scheduleExp);
            if (!hosts.contains(host)) {
                throw new JobScheduleException("The specify actuator endpoint [{}] is not contains available actuator nodes", scheduleExp);
            }
            selected = host;
        } else {
            LoadBalance loadBalance = LoadBalanceFactory.getLoadBalance(jobDesc.getScheduleStrategy().getName());
            selected = loadBalance.select(hosts);
        }
        context.refreshHosts(selected);
    }

    private HodorExecutor createHodorExecutor(final JobKey key) {
        final HodorExecutor hodorExecutor = new HodorExecutor(64, threadPoolExecutor);
        hodorExecutor.setRejectEnqueuePolicy(new DiscardOldestElementPolicy<>());
        return hodorExecutor;
    }

    /**
     * 获取正在运行的任务执行器
     *
     * @param key 任务key
     * @return HodorExecutor
     */
    public HodorExecutor getHodorExecutor(final JobKey key) {
        return hodorExecutorMap.get(key);
    }

    /**
     * 删除未使用的队列
     */
    public void clearUnavailableExecutor() {
        for (Map.Entry<JobKey, HodorExecutor> item : hodorExecutorMap.entrySet()) {
            if (item.getValue().getQueue().isEmpty()) {
                hodorExecutorMap.remove(item.getKey());
            }
        }
    }

    private RemotingMessage getRequestBody(final HodorJobExecutionContext context) {
        byte[] requestBody = serializer.serialize(buildRequestFromContext(context));
        return RemotingMessage.builder()
            .header(buildHeader(requestBody.length, context))
            .body(requestBody)
            .build();
    }

    private JobExecuteRequest buildRequestFromContext(final HodorJobExecutionContext context) {
        Long requestId = context.getRequestId();
        JobDesc jobDesc = context.getJobDesc();
        return JobExecuteRequest.builder()
            .requestId(requestId)
            .instanceId(context.getInstanceId())
            .jobName(jobDesc.getJobName())
            .groupName(jobDesc.getGroupName())
            .jobCommandType(jobDesc.getJobCommandType())
            .jobCommand(context.getExecCommand())
            .jobParameters(jobDesc.getJobParameters())
            .extensibleParameters(jobDesc.getExtensibleParameters())
            .shardingCount(context.getShardingCount())
            .shardingId(context.getShardingId())
            .shardingParams(context.getShardingParams())
            .parentJobData(context.getParentJobData())
            .parentJobExecuteStatuses(context.getParentJobExecuteStatuses())
            .parentJobExecuteResults(context.getParentJobExecuteResults())
            .timeout(jobDesc.getTimeout())
            .retryCount(jobDesc.getRetryCount())
            .version(jobDesc.getVersion())
            .build();
    }

    private Header buildHeader(int bodyLength, HodorJobExecutionContext context) {
        Map<String, Object> attachment = new HashMap<>();
        attachment.put("schedulerName", context.getSchedulerName());
        if (context.getRootJobKey() != null) {
            attachment.put(Constants.FlowNodeConstants.ROOT_JOB_KEY, context.getRootJobKey().getKeyName());
        }
        if (context.getStage() != null) {
            attachment.put(Constants.JobConstants.JOB_STAGE_KEY, context.getStage());
        }
        return Header.builder()
            .id(context.getRequestId())
            .version(RemotingConst.DEFAULT_VERSION)
            .type(MessageType.JOB_EXEC_REQUEST.getType())
            .attachment(attachment)
            .length(bodyLength)
            .build();
    }

}
