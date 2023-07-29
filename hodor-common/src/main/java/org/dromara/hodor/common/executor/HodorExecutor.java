package org.dromara.hodor.common.executor;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.concurrent.HodorThreadFactory;
import org.dromara.hodor.common.queue.CircleQueue;
import org.dromara.hodor.common.queue.RejectedEnqueueHandler;

import java.util.Optional;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * HodorExecutor</br>
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class HodorExecutor {

    private final ReentrantLock lock = new ReentrantLock();

    private final AtomicBoolean serialExecutable = new AtomicBoolean(false);

    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    private final AtomicInteger rejectCount = new AtomicInteger(0);

    private final CircleQueue<HodorRunnable> serialQueue;

    private ThreadPoolExecutor executor;

    public HodorExecutor(int queueSize, final ThreadPoolExecutor executor) {
        this.serialQueue = new CircleQueue<>(queueSize <= 0 ? 128 : queueSize);
        this.executor = executor;
        setRejectExecutionHandler(executor);
    }

    public HodorExecutor(ThreadPoolExecutor executor) {
        this.serialQueue = new CircleQueue<>(128);
        this.executor = executor;
        setRejectExecutionHandler(executor);
    }

    public void setRejectEnqueuePolicy(final RejectedEnqueueHandler<HodorRunnable> handler) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            this.serialQueue.setRejectedEnqueueHandler(handler);
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
        return serialQueue;
    }

    /**
     * 任务串行执行<br/>
     * <p>
     * 按照任务的提交顺序执行
     *
     * @param runnable 待执行任务
     */
    public void serialExecute(final HodorRunnable runnable) {
        lock.lock();
        try {
            // 从第一个任务触发，后续自动执行
            offer(runnable);
            if (serialExecutable.compareAndSet(false, true)) {
                notifyNextTaskExecute();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 并行执行</br>
     *
     * @param runnable 待执行任务
     */
    public void parallelExecute(final HodorRunnable runnable) {
        lock.lock();
        try {
            executor.execute(runnable);
        } finally {
            lock.unlock();
        }
    }

    private void offer(HodorRunnable runnable) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (isShutdown()) {
                throw new IllegalStateException("Hodor executor has shutdown.");
            }

            boolean offered = serialQueue.offer(runnable);
            if (!offered) {
                log.warn("Queue offer false. Please check the job entry policy...");
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 设置任务执行拒绝策略，新增拒绝次数的记录
     *
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
        serialExecutable.compareAndSet(true, false);
    }

    /**
     * 通知下一个任务开始执行
     */
    private void notifyNextTaskExecute() {
        if (serialQueue.isEmpty()) {
            // 对于空队列再添加元素时需要重新开始消费，否则将会导致队列任务不消费
            reset();
            return;
        }

        Optional.ofNullable(serialQueue.poll())
            .ifPresent(runnable -> executor.execute(() -> {
                try {
                    runnable.run();
                } catch (Throwable unexpected) {
                    log.error("Catch unexpected exception {}.", unexpected.getMessage(), unexpected);
                } finally {
                    notifyNextTaskExecute();
                }
            }));

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
            .executorName(((HodorThreadFactory) executor.getThreadFactory()).getName())
            .circleQueueSize(serialQueue.size())
            .circleQueueCapacity(serialQueue.getCapacity())
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
