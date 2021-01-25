package org.dromara.hodor.common.executor;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.queue.CircleQueue;
import org.dromara.hodor.common.queue.RejectedEnqueueHandler;

/**
 * HodorExecutor</br>
 *
 * @author tomgs
 * @since 2021/1/22
 */
@Slf4j
public class HodorExecutor {

    private final ReentrantLock lock = new ReentrantLock();

    private AtomicBoolean executable = new AtomicBoolean(false);

    private AtomicBoolean shutdown = new AtomicBoolean(false);

    private CircleQueue<HodorRunnable> circleQueue;

    private ThreadPoolExecutor executor;

    public HodorExecutor() {

    }

    public HodorExecutor(final CircleQueue<HodorRunnable> circleQueue, final ThreadPoolExecutor executor) {
        this.circleQueue = circleQueue;
        this.executor = executor;
    }

    public void setCircleQueue(CircleQueue<HodorRunnable> circleQueue) {
        lock.lock();
        try {
            this.circleQueue = circleQueue;
        } finally {
            lock.unlock();
        }
    }

    public void setExecutor(ThreadPoolExecutor executor) {
        lock.lock();
        try {
            this.executor = executor;
        } finally {
            lock.unlock();
        }
    }

    public void setRejectEnqueuePolicy(RejectedEnqueueHandler<HodorRunnable> handler) {
        lock.lock();
        try {
            this.circleQueue.setRejectedEnqueueHandler(handler);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 任务串行执行<br/>
     *
     * 按照任务的提交顺序执行
     *
     * @param runnable 待执行任务
     */
    public void serialExecute(HodorRunnable runnable) {
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
    public void parallelExecute(HodorRunnable runnable) {
        executor.execute(runnable);
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

        HodorRunnable runnable = Objects.requireNonNull(circleQueue.poll());
        executor.execute(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                runnable.exceptionCaught(e);
            } catch (Throwable unexpected) {
                log.error("Catch unexpected exception {}.", unexpected.getMessage(), unexpected);
            } finally {
                notifyNextTaskExecute();
            }
        });
    }

    public boolean isShutdown() {
        return shutdown.get() && executor.isShutdown();
    }

    public void shutdown() {
        shutdown.compareAndSet(false, true);
        executor.shutdown();
    }

    public ExecutorInfo getExecutorInfo() {
        return ExecutorInfo.builder().circleQueueCount(circleQueue.size())
            .activeTaskCount(executor.getActiveCount())
            .waitTaskCount(executor.getQueue().size())
            .taskCount(executor.getTaskCount())
            .completeTaskCount(executor.getCompletedTaskCount())
            .largestPoolSize(executor.getLargestPoolSize())
            .currentThreadSize(executor.getPoolSize())
            .coreThreadSize(executor.getCorePoolSize())
            .maximumPoolSize(executor.getMaximumPoolSize())
            .build();
    }

}
