package org.dromara.hodor.scheduler.api;

import java.util.List;
import org.dromara.hodor.common.extension.SPI;
import org.dromara.hodor.core.JobInfo;
import org.dromara.hodor.scheduler.api.config.SchedulerConfig;

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

    void addJob(JobInfo jobInfo);

    void addJobList(List<JobInfo> jobInfoList);

    void resumeJob(JobInfo jobInfo);

    void triggerJob(JobInfo jobInfo);

    void pauseJob(JobInfo jobInfo);

    boolean isPaused(JobInfo jobInfo);

    boolean deleteJob(JobInfo jobInfo);

    boolean deleteJobs(List<JobInfo> jobInfoList);

    boolean checkExists(JobInfo jobInfo);

    boolean isStarted();

    boolean isShutdown();

}
