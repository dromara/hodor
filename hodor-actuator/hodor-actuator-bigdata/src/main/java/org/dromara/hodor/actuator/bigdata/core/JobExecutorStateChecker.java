package org.dromara.hodor.actuator.bigdata.core;

import org.dromara.hodor.actuator.bigdata.queue.ITask;
import org.dromara.hodor.actuator.bigdata.queue.PriorityTaskQueue;

/**
 *
 *  任务执行状态检查
 *
 * @author tomgs
 * @since 1.0
 **/
public class JobExecutorStateChecker {

    private static volatile JobExecutorStateChecker instance = null;
    private static PriorityTaskQueue queue;

    private JobExecutorStateChecker() {
        queue = new PriorityTaskQueue(1);
        queue.start();
    }

    public static JobExecutorStateChecker getInstance() {
        if (instance == null) {
            synchronized (JobExecutorStateChecker.class) {
                if (instance == null) {
                    instance = new JobExecutorStateChecker();
                }
            }
        }
        return instance;
    }

    public int addTask(ITask task) {
        return queue.addTask(task);
    }

    public int queueSize() {
        return queue.size();
    }

}
