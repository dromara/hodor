package org.dromara.hodor.client.executor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.dromara.hodor.common.executor.HodorExecutor;
import org.dromara.hodor.common.executor.HodorExecutorFactory;
import org.dromara.hodor.common.executor.HodorRunnable;

/**
 * executor manager
 *
 * @author tomgs
 * @since 1.0
 */
public class ExecutorManager {

    private static volatile ExecutorManager INSTANCE;

    private final HodorExecutor hodorExecutor;

    private final HodorExecutor commonExecutor;

    private final Map<Long, Thread> runningThread = new ConcurrentHashMap<>();

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
        hodorExecutor.parallelExecute(runnable);
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

}
