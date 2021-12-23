package org.dromara.hodor.actuator.common.executor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.dromara.hodor.common.executor.HodorExecutor;
import org.dromara.hodor.common.executor.HodorExecutorFactory;
import org.dromara.hodor.common.executor.HodorRunnable;

/**
 * executor manager
 *
 * @author tomgs
 * @since 2021/2/26
 */
public class ExecutorManager {

    private static volatile ExecutorManager INSTANCE;

    private final HodorExecutor hodorExecutor;

    private final HodorExecutor commonExecutor;

    private final Map<Long, Thread> runningThread = new ConcurrentHashMap<>();

    private final Thread defaultThread = new Thread(() -> {}, "default");

    private ExecutorManager() {
        final int threadSize = Runtime.getRuntime().availableProcessors() * 2;
        // request job
        hodorExecutor = HodorExecutorFactory.createDefaultExecutor("job-exec", threadSize, false);
        // heartbeat, fetch log, job status, kill job
        commonExecutor = HodorExecutorFactory.createDefaultExecutor("common-exec", threadSize / 4, true);
    }

    public static ExecutorManager getInstance() {
        if (INSTANCE == null) {
            synchronized (ExecutorManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ExecutorManager();
                }
            }
        }
        return INSTANCE;
    }

    public void execute(final HodorRunnable runnable) {
        hodorExecutor.serialExecute(runnable);
    }

    public void commonExecute(final HodorRunnable runnable) {
        commonExecutor.parallelExecute(runnable);
    }

    public HodorExecutor getHodorExecutor() {
        return hodorExecutor;
    }

    public HodorExecutor getCommonExecutor() {
        return commonExecutor;
    }

    public void addRunningThread(Long requestId, Thread currentThread) {
        runningThread.put(requestId, currentThread);
    }

    public void removeRunningThread(Long requestId) {
        runningThread.remove(requestId);
    }

    public Thread getRunningThread(Long requestId) {
        return runningThread.get(requestId);
    }

    public Thread getDefaultThread() {
        return defaultThread;
    }

    public boolean executeReady(Long requestId) {
        Thread runningThread = getRunningThread(requestId);
        if (runningThread != null) {
            return false;
        }
        addRunningThread(requestId, getDefaultThread());
        return true;
    }

}