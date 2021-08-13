package org.dromara.hodor.server.executor;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.dromara.hodor.common.executor.HodorExecutor;
import org.dromara.hodor.common.executor.HodorExecutorFactory;
import org.dromara.hodor.common.executor.HodorRunnable;
import org.dromara.hodor.common.queue.CircleQueue;
import org.dromara.hodor.common.queue.DiscardOldestElementPolicy;
import org.dromara.hodor.model.job.JobKey;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;
import org.dromara.hodor.server.executor.handler.HodorJobRequestHandler;

/**
 * 任务分发器
 *
 * @author tomgs
 * @since 2021/1/26
 */
public class JobDispatcher {

    private static final JobDispatcher INSTANCE = new JobDispatcher();

    private final Map<JobKey, HodorExecutor> hodorExecutorMap = new ConcurrentHashMap<>();

    private final ThreadPoolExecutor threadPoolExecutor;

    private final HodorJobRequestHandler hodorJobRequestHandler;

    private JobDispatcher() {
        final int threadSize = Runtime.getRuntime().availableProcessors() * 2;
        this.threadPoolExecutor = HodorExecutorFactory.createThreadPoolExecutor("job-dispatcher", threadSize, false);
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(2);
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(this::clearUnavailableExecutor, 1, 1, TimeUnit.HOURS);
        this.hodorJobRequestHandler = new HodorJobRequestHandler();
    }

    public static JobDispatcher getInstance() {
        return INSTANCE;
    }

    /**
     * 分发任务
     *
     * @param context 任务上下文参数
     */
    public void dispatch(final HodorJobExecutionContext context) {
        HodorExecutor hodorExecutor = hodorExecutorMap.computeIfAbsent(context.getJobKey(), this::createHodorExecutor);
        Objects.requireNonNull(hodorExecutor).serialExecute(new HodorRunnable() {
            @Override
            public void execute() {
                try {
                    hodorJobRequestHandler.preHandle(context);
                    hodorJobRequestHandler.handle(context);
                } catch (Throwable t) {
                    hodorJobRequestHandler.exceptionCaught(context, t);
                }
            }
        });
    }

    private HodorExecutor createHodorExecutor(final JobKey key) {
        final HodorExecutor hodorExecutor = new HodorExecutor();
        hodorExecutor.setCircleQueue(new CircleQueue<>());
        hodorExecutor.setExecutor(threadPoolExecutor);
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

}
