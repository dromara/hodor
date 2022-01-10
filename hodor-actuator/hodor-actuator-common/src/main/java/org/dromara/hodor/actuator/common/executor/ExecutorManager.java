package org.dromara.hodor.actuator.common.executor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.dromara.hodor.actuator.common.core.ExecutableJob;
import org.dromara.hodor.common.exception.HodorExecutorException;
import org.dromara.hodor.common.executor.HodorExecutor;
import org.dromara.hodor.common.executor.HodorExecutorFactory;
import org.dromara.hodor.common.executor.HodorRunnable;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.model.enums.JobExecuteStatus;

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

    private final Map<Long, ExecutableJob> executableNodeMap = new ConcurrentHashMap<>();

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

    public void addExecutableNode(ExecutableJob executableJob) {
        if (executableNodeMap.containsKey(executableJob.getRequestId())) {
            throw new HodorExecutorException(StringUtils.format("execute job {} exception, job request [{}] has already running.",
                executableJob.getJobKey(), executableJob.getRequestId()));
        }
        executableNodeMap.put(executableJob.getRequestId(), executableJob);
    }

    public ExecutableJob getExecutableJob(Long requestId) {
        return executableNodeMap.get(requestId);
    }

    public void removeExecutableNode(Long requestId) {
        executableNodeMap.remove(requestId);
    }

    public boolean readyExecutableJob(Long requestId) {
        ExecutableJob executableJob = getExecutableJob(requestId);
        if (executableJob != null) {
            return false;
        }
        ExecutableJob readyJob = ExecutableJob.builder()
            .requestId(requestId)
            .executeStatus(JobExecuteStatus.READY)
            .build();
        addExecutableNode(readyJob);
        return true;
    }

}
