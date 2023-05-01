package org.dromara.hodor.common.executor;

import java.util.Optional;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.concurrent.HodorThreadFactory;
import org.dromara.hodor.common.queue.CircleQueue;
import org.dromara.hodor.common.queue.RejectedEnqueueHandler;

/**
 * HodorExecutor</br>
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class HodorExecutor {

    private final ReentrantLock lock = new ReentrantLock();

    private final AtomicBoolean executable = new AtomicBoolean(false);

    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    private final AtomicInteger rejectCount = new AtomicInteger(0);

    private CircleQueue<HodorRunnable> circleQueue;

    private ThreadPoolExecutor executor;

    public HodorExecutor() {

    }

    public HodorExecutor(final CircleQueue<HodorRunnable> circleQueue, final ThreadPoolExecutor executor) {
        this.circleQueue = circleQueue;
        this.executor = executor;

        setRejectExecutionHandler(executor);
    }

    public void setCircleQueue(final CircleQueue<HodorRunnable> circleQueue) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            this.circleQueue = circleQueue;
        } finally {
            lock.unlock();
        }
    }

    public void setRejectEnqueuePolicy(final RejectedEnqueueHandler<HodorRunnable> handler) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            this.circleQueue.setRejectedEnqueueHandler(handler);
        } finally {
            lock.unlock();
        }
    }

    public void setExecutor(final ThreadPoolExecutor executor) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            this.executor = executor;
            setRejectExecutionHandler(executor);
        } finally {
            lock.unlock();
        }
    }

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

    public CircleQueue<HodorRunnable> getQueue() {
        return circleQueue;
    }

    /**
     * 任务串行执行<br/>
     *
     * 按照任务的提交顺序执行
     *
     * @param runnable 待执行任务
     */
    public void serialExecute(final HodorRunnable runnable) {
        offer(runnable);
        // 从第一个任务触发，后续自动执行
        if (executable.compareAndSet(false, true) && circleQueue.size() >= 1) {
            notifyNextTaskExecute();
        }

    }

    /**
     * 并行执行</br>
     *
     * @param runnable 待执行任务
     */
    public void parallelExecute(final HodorRunnable runnable) {
        offer(runnable);
        if (executable.compareAndSet(false, true) && circleQueue.size() >= 1) {
            notifyTaskExecute();
        }
    }

    private void offer(HodorRunnable runnable) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (isShutdown()) {
                throw new IllegalStateException("Hodor executor has shutdown.");
            }

            boolean offered = circleQueue.offer(runnable);
            if (!offered) {
                log.warn("Queue offer false. Please check the job entry policy...");
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 设置任务执行拒绝策略，新增拒绝次数的记录
     * @param executor 线程池
     */
    private void setRejectExecutionHandler(final ThreadPoolExecutor executor) {
        RejectedExecutionHandler rejectedExecutionHandler = executor.getRejectedExecutionHandler();
        executor.setRejectedExecutionHandler((r, pool) -> {
            rejectCount.incrementAndGet();
            rejectedExecutionHandler.rejectedExecution(r, pool);
        });
    }

    /**
     * 重置可执行标识位
     */
    private void reset() {
        executable.compareAndSet(true, false);
    }

    /**
     * 通知下一个任务开始执行
     */
    private void notifyNextTaskExecute() {
        if (circleQueue.isEmpty()) {
            // 对于空队列再添加元素时需要重新开始消费，否则将会导致队列任务不消费
            reset();
            return;
        }

        Optional.ofNullable(circleQueue.poll()).ifPresent(runnable -> executor.execute(() -> {
            try {
                runnable.run();
            } catch (Throwable unexpected) {
                log.error("Catch unexpected exception {}.", unexpected.getMessage(), unexpected);
            } finally {
                notifyNextTaskExecute();
            }
        }));

    }

    private void notifyTaskExecute() {
        for (;;) {
            if (circleQueue.isEmpty()) {
                reset();
                return;
            }
            Optional.ofNullable(circleQueue.poll()).ifPresent(runnable -> executor.execute(runnable));
        }
        // 这里为了严谨起见递归调用改用循环方式避免栈溢出
        // notifyTaskExecute();
    }

    public boolean isShutdown() {
        return shutdown.get() && executor.isShutdown();
    }

    public void shutdown() {
        if (shutdown.compareAndSet(false, true)) {
            executor.shutdown();
        }
    }

    /**
     * 获取执行器的运行状况
     *
     * @return ExecutorInfo
     */
    public ExecutorInfo getExecutorInfo() {
        return ExecutorInfo.builder()
            .executorName(((HodorThreadFactory)executor.getThreadFactory()).getName())
            .circleQueueSize(circleQueue.size())
            .circleQueueCapacity(circleQueue.getCapacity())
            .queueSize(executor.getQueue().size())
            .queueCapacity(executor.getQueue().size() + executor.getQueue().remainingCapacity())
            .activeTaskCount(executor.getActiveCount())
            .waitTaskCount(executor.getQueue().size())
            .taskCount(executor.getTaskCount())
            .completeTaskCount(executor.getCompletedTaskCount())
            .largestPoolSize(executor.getLargestPoolSize())
            .currentThreadSize(executor.getPoolSize())
            .coreThreadSize(executor.getCorePoolSize())
            .maximumPoolSize(executor.getMaximumPoolSize())
            .rejectCount(rejectCount.get())
            .build();
    }

}
