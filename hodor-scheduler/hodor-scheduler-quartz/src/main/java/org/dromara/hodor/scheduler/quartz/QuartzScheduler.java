package org.dromara.hodor.scheduler.quartz;

import org.dromara.hodor.common.extension.Join;
import org.dromara.hodor.core.entity.JobInfo;
import org.dromara.hodor.scheduler.api.HodorScheduler;
import org.dromara.hodor.scheduler.api.JobExecutor;
import org.dromara.hodor.scheduler.api.JobExecutorTypeManager;
import org.dromara.hodor.scheduler.api.config.SchedulerConfig;
import org.dromara.hodor.scheduler.api.exception.HodorSchedulerException;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

/**
 *  implements scheduler by quartz
 *
 * @author tomgs
 * @since 2020/6/24 1.0
 */
@Join
public class QuartzScheduler implements HodorScheduler {

    private String schedulerName;
    private Scheduler scheduler;
    private StdSchedulerFactory factory;
    private ReentrantLock lock;

    public QuartzScheduler() {
        factory = new StdSchedulerFactory();
        lock = new ReentrantLock();
    }

    public QuartzScheduler(SchedulerConfig config) {
        this();
        config(config);
    }

    @Override
    public void config(SchedulerConfig config) {
        try {
            this.factory.initialize(getBaseProperties(config));
            this.scheduler = factory.getScheduler();
            this.schedulerName = config.getSchedulerName();
        } catch (SchedulerException e) {
            throw new HodorSchedulerException(e);
        }
    }

    private Properties getBaseProperties(SchedulerConfig config) {
        Properties result = new Properties();
        result.put("org.quartz.threadPool.class", org.quartz.simpl.SimpleThreadPool.class.getName());
        result.put("org.quartz.threadPool.threadCount", String.valueOf(config.getThreadCount()));
        result.put("org.quartz.scheduler.instanceName", config.getSchedulerName());
        result.put("org.quartz.jobStore.misfireThreshold", String.valueOf(config.getMisfireThreshold()));
        return result;
    }

    @Override
    public String getSchedulerName() {
        return schedulerName;
    }

    @Override
    public void start() {
        lock.lock();
        try {
            if (!scheduler.isStarted()) {
                scheduler.start();
            }
        } catch (SchedulerException e) {
            throw new HodorSchedulerException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void shutdown() {
        try {
            scheduler.shutdown();
        } catch (SchedulerException e) {
            throw new HodorSchedulerException(e);
        }
    }

    @Override
    public void startDelayed(int seconds) {
        try {
            scheduler.startDelayed(seconds);
        } catch (SchedulerException e) {
            throw new HodorSchedulerException(e);
        }
    }

    @Override
    public void addJob(JobInfo jobInfo) {
        JobDetail jobDetail = JobBuilder.newJob(HodorJob.class)
                .withIdentity(jobInfo.getJobName(), jobInfo.getGroupName())
                .requestRecovery(true)
                .build();
        JobExecutor jobExecutor = JobExecutorTypeManager.INSTANCE.getJobExecutor(jobInfo.getType());
        jobDetail.getJobDataMap().put("jobExecutor", jobExecutor);
        jobDetail.getJobDataMap().put("jobInfo", jobInfo);

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobInfo.getJobName(), jobInfo.getGroupName())
                .withSchedule(CronScheduleBuilder.cronSchedule(jobInfo.getCron()).withMisfireHandlingInstructionDoNothing())
                .withPriority(jobInfo.getPriority().getValue())
                .forJob(jobDetail)
                .build();

        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new HodorSchedulerException(e);
        }
    }

    @Override
    public void addJobList(List<JobInfo> jobInfoList) {
        jobInfoList.forEach(this::addJob);
    }

    @Override
    public boolean deleteJob(JobInfo jobInfo) {
        try {
            if (scheduler.checkExists(JobKey.jobKey(jobInfo.getJobName(), jobInfo.getGroupName()))) {
                return scheduler.deleteJob(JobKey.jobKey(jobInfo.getJobName(), jobInfo.getGroupName()));
            }
            return false;
        } catch (SchedulerException e) {
            throw new HodorSchedulerException(e);
        }
    }

    @Override
    public boolean deleteJobs(List<JobInfo> jobInfoList) {
        boolean result = true;
        for (JobInfo jobInfo : jobInfoList) {
            result = deleteJob(jobInfo);
        }
        return result;
    }

    @Override
    public boolean checkExists(JobInfo jobInfo) {
        try {
            return scheduler.checkExists(JobKey.jobKey(jobInfo.getJobName(), jobInfo.getGroupName()));
        } catch (SchedulerException e) {
            throw new HodorSchedulerException(e);
        }
    }

    @Override
    public void pauseJob(JobInfo jobInfo) {
        try {
            scheduler.pauseJob(JobKey.jobKey(jobInfo.getJobName(), jobInfo.getGroupName()));
        } catch (SchedulerException e) {
            throw new HodorSchedulerException(e);
        }
    }

    @Override
    public boolean isPaused(JobInfo jobInfo) {
        try {
            return scheduler.isShutdown() && Trigger.TriggerState.PAUSED == scheduler.getTriggerState(new TriggerKey(jobInfo.getJobName(), jobInfo.getGroupName()));
        } catch (SchedulerException e) {
            throw new HodorSchedulerException(e);
        }
    }

    @Override
    public void clear() {
        try {
            scheduler.clear();
        } catch (SchedulerException e) {
            throw new HodorSchedulerException(e);
        }
    }

    @Override
    public void resumeJob(JobInfo jobInfo) {
        try {
            scheduler.resumeJob(JobKey.jobKey(jobInfo.getJobName(), jobInfo.getGroupName()));
        } catch (SchedulerException e) {
            throw new HodorSchedulerException(e);
        }
    }

    @Override
    public void triggerJob(JobInfo jobInfo) {
        try {
            scheduler.triggerJob(JobKey.jobKey(jobInfo.getJobName(), jobInfo.getGroupName()));
        } catch (SchedulerException e) {
            throw new HodorSchedulerException(e);
        }
    }

    @Override
    public boolean isShutdown() {
        try {
            return scheduler.isShutdown();
        } catch (SchedulerException e) {
            throw new HodorSchedulerException(e);
        }
    }

    @Override
    public boolean isStarted() {
        try {
            return scheduler.isStarted();
        } catch (SchedulerException e) {
            throw new HodorSchedulerException(e);
        }
    }

}
