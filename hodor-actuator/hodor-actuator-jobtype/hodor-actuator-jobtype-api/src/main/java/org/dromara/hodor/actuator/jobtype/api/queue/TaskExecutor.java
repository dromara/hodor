package org.dromara.hodor.actuator.jobtype.api.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * task 执行器
 *
 * @author tomgs
 * @since 1.0
 **/
public class TaskExecutor extends Thread {

    private final BlockingQueue<AsyncTask> taskQueue;

    private volatile boolean isRunning = true;

    TaskExecutor(BlockingQueue<AsyncTask> taskQueue) {
        this.taskQueue = taskQueue;
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                AsyncTask task = taskQueue.take();
                consumer(task);
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                if (isRunning) {
                    continue;
                }
                interrupt();
                break;
            }
        }
    }

    void shutdown() {
        isRunning = false;
        interrupt();
    }

    private void consumer(AsyncTask task) {
        if (task != null) {
            AsyncTask currentTask = task.run();
            if (currentTask != null) {
                taskQueue.offer(currentTask);
            }
        }
    }
}
