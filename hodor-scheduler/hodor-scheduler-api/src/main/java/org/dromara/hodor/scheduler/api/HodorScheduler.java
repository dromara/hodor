package org.dromara.hodor.scheduler.api;

import java.util.List;
import org.dromara.hodor.common.extension.SPI;
import org.dromara.hodor.core.JobDesc;
import org.dromara.hodor.scheduler.api.common.SchedulerConfig;

/**
 * hodor scheduler basic interface
 *
 * @author tomgs
 * @since 2020/6/24 1.0
 */
@SPI("scheduler")
public interface HodorScheduler {

    String getSchedulerName();

    void clear();

    void start();

    void shutdown();

    void config(SchedulerConfig config);

    void startDelayed(int seconds);

    void addJob(JobDesc jobInfo);

    void addJobList(List<JobDesc> jobInfoList);

    void resumeJob(JobDesc jobInfo);

    void triggerJob(JobDesc jobInfo);

    void pauseJob(JobDesc jobInfo);

    boolean isPaused(JobDesc jobInfo);

    boolean deleteJob(JobDesc jobInfo);

    boolean deleteJobs(List<JobDesc> jobInfoList);

    boolean checkExists(JobDesc jobInfo);

    boolean isStarted();

    boolean isShutdown();

}
