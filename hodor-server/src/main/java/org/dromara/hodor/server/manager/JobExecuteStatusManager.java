package org.dromara.hodor.server.manager;

import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;

/**
 * job status manager
 *
 * @author tomgs
 * @since 2021/8/10
 */
public enum JobExecuteStatusManager {
    INSTANCE;

    public static JobExecuteStatusManager getInstance() {
        return INSTANCE;
    }

    public boolean isRunning(String jobKey) {
        return false;
    }

    public void addRunningJob(HodorJobExecutionContext context) {

    }

    public void addFailureJob(HodorJobExecutionContext context) {
        removeRunningJob(context);
    }

    public void removeRunningJob(HodorJobExecutionContext context) {

    }

    public void addFinishJob(Long requestId) {

    }

}
