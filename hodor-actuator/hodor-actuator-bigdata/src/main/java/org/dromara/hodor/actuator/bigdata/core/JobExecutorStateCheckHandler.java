package org.dromara.hodor.actuator.bigdata.core;

import org.dromara.hodor.actuator.bigdata.queue.ITask;
import org.dromara.hodor.actuator.bigdata.queue.PriorityTaskQueue;

/**
 *
 *  任务执行状态检查
 *
 * @author tangzhongyuan
 * @create 2019-03-15 12:08
 **/
public class JobExecutorStateCheckHandler {

    private static volatile JobExecutorStateCheckHandler instance = null;
    private static PriorityTaskQueue queue;

    private JobExecutorStateCheckHandler() {
        queue = new PriorityTaskQueue(1);
        queue.start();
    }

    public static synchronized JobExecutorStateCheckHandler getInstance() {
        if (instance == null) {
            instance = new JobExecutorStateCheckHandler();
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
