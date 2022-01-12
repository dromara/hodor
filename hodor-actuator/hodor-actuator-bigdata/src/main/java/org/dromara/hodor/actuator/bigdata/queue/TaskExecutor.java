package org.dromara.hodor.actuator.bigdata.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * task 执行器
 *
 * @author tangzhongyuan
 * @create 2019-03-14 9:50
 **/
public class TaskExecutor extends Thread {

    private final BlockingQueue<ITask> taskQueue;
    private volatile boolean isRuning = true;

    TaskExecutor(BlockingQueue<ITask> taskQueue) {
        this.taskQueue = taskQueue;
    }

    @Override
    public void run() {
        while (isRuning) {
            try {
                ITask task = taskQueue.take();
                consumer(task);

                //延迟3s再去消费
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();

                if (isRuning) {
                    continue;
                }
                interrupt();
                break;
            }
        }
    }

    void shutdown() {
        isRuning = false;
        interrupt();
    }

    private void consumer(ITask task) {
        if (task != null) {
            ITask currentTask = task.run();
            if (currentTask != null) {
                taskQueue.offer(currentTask);
            }
        }
    }
}
