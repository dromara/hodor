package org.dromara.hodor.actuator.jobtype.api.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * PriorityTaskQueue
 *
 * @author tomgs
 * @since 1.0
 **/
public class PriorityTaskQueue {

    private final AtomicInteger seqInteger = new AtomicInteger(0);
    private final BlockingQueue<AsyncTask> queue;
    private final TaskExecutor[] executors;

    public PriorityTaskQueue(int executorSize) {
        queue = new PriorityBlockingQueue<>();
        executors = new TaskExecutor[executorSize];
    }

    public void start() {
        shutdown();
        for (TaskExecutor executor : executors) {
            executor = new TaskExecutor(queue);
            executor.start();
        }
    }

    public void shutdown() {
        for (TaskExecutor executor : executors) {
            if (executor != null) {
                executor.shutdown();
            }
        }
    }

    public int addTask(AsyncTask task) {
        if (!queue.contains(task)) {
            int i = seqInteger.incrementAndGet();
            task.setSeq(i);
            queue.add(task);
        }
        return queue.size();
    }

    public int size() {
        return queue.size();
    }
}
