package org.dromara.hodor.actuator.bigdata.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * PriorityTaskQueue
 *
 * @author tangzhongyuan
 * @create 2019-03-14 9:45
 **/
public class PriorityTaskQueue {

    private final AtomicInteger seqInteger = new AtomicInteger(0);
    private final BlockingQueue<ITask> queue;
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

    public int addTask(ITask task) {
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
