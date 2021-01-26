package org.dromara.hodor.server.executor;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.dromara.hodor.common.executor.HodorExecutor;
import org.dromara.hodor.common.executor.HodorExecutorFactory;
import org.dromara.hodor.common.queue.CircleQueue;
import org.dromara.hodor.common.queue.DiscardOldestElementPolicy;
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

    private final Map<String, HodorExecutor> hodorExecutorMap = new ConcurrentHashMap<>();

    private final ThreadPoolExecutor threadPoolExecutor;

    private JobDispatcher() {
        final int threadSize = Runtime.getRuntime().availableProcessors() * 2;
        this.threadPoolExecutor = HodorExecutorFactory.createThreadPoolExecutor("job-dispatcher", threadSize);
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(2);
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(this::clearUnavailableExecutor, 1, 1, TimeUnit.HOURS);
    }

    public static JobDispatcher getInstance() {
        return INSTANCE;
    }

    public void dispatch(HodorJobExecutionContext context) {
        HodorExecutor hodorExecutor = hodorExecutorMap.computeIfAbsent(context.getJobKey(), this::createHodorExecutor);
        Objects.requireNonNull(hodorExecutor).serialExecute(new HodorJobRequestHandler(context));
    }

    private HodorExecutor createHodorExecutor(String key) {
        final HodorExecutor hodorExecutor = new HodorExecutor();
        hodorExecutor.setCircleQueue(new CircleQueue<>());
        hodorExecutor.setExecutor(threadPoolExecutor);
        hodorExecutor.setRejectEnqueuePolicy(new DiscardOldestElementPolicy<>());
        return hodorExecutor;
    }

    /**
     * 删除为使用的队列
     */
    public void clearUnavailableExecutor() {
        for (Map.Entry<String, HodorExecutor> item : hodorExecutorMap.entrySet()) {
            if (item.getValue().getQueue().isEmpty()) {
                hodorExecutorMap.remove(item.getKey());
            }
        }
    }

}
