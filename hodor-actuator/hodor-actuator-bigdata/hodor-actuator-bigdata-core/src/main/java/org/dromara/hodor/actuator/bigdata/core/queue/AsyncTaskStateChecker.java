package org.dromara.hodor.actuator.bigdata.core.queue;

/**
 * 异步任务执行状态检查
 *
 * @author tomgs
 * @since 1.0
 **/
public class AsyncTaskStateChecker {

    private static volatile AsyncTaskStateChecker instance = null;
    
    private static PriorityTaskQueue queue;

    private AsyncTaskStateChecker() {
        queue = new PriorityTaskQueue(1);
        queue.start();
    }

    public static AsyncTaskStateChecker getInstance() {
        if (instance == null) {
            synchronized (AsyncTaskStateChecker.class) {
                if (instance == null) {
                    instance = new AsyncTaskStateChecker();
                }
            }
        }
        return instance;
    }

    public int addTask(AsyncTask task) {
        return queue.addTask(task);
    }

    public int queueSize() {
        return queue.size();
    }

}
