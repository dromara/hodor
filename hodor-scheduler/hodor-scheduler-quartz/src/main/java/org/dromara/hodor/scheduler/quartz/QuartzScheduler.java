package org.dromara.hodor.scheduler.quartz;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;
import org.dromara.hodor.common.extension.Join;
import org.dromara.hodor.core.JobDesc;
import org.dromara.hodor.scheduler.api.HodorScheduler;
import org.dromara.hodor.scheduler.api.JobExecutor;
import org.dromara.hodor.scheduler.api.JobExecutorTypeManager;
import org.dromara.hodor.scheduler.api.config.SchedulerConfig;
import org.dromara.hodor.scheduler.api.exception.HodorSchedulerException;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

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
    public void addJob(JobDesc jobDesc) {
        JobDetail jobDetail = JobBuilder.newJob(HodorJob.class)
                .withIdentity(jobDesc.getJobName(), jobDesc.getGroupName())
                .requestRecovery(true)
                .build();

        JobExecutor jobExecutor = JobExecutorTypeManager.INSTANCE.getJobExecutor(jobDesc.getJobType());
        jobDetail.getJobDataMap().put("jobExecutor", jobExecutor);
        jobDetail.getJobDataMap().put("jobDesc", jobDesc);

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobDesc.getJobName(), jobDesc.getGroupName())
                .withSchedule(CronScheduleBuilder.cronSchedule(jobDesc.getCronExpression()).withMisfireHandlingInstructionDoNothing())
                .withPriority(jobDesc.getPriority().getValue())
                .forJob(jobDetail)
                .build();

        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new HodorSchedulerException(e);
        }
    }

    @Override
    public void addJobList(List<JobDesc> jobDescList) {
        jobDescList.forEach(this::addJob);
    }

    @Override
    public boolean deleteJob(JobDesc jobDesc) {
        try {
            if (scheduler.checkExists(JobKey.jobKey(jobDesc.getJobName(), jobDesc.getGroupName()))) {
                return scheduler.deleteJob(JobKey.jobKey(jobDesc.getJobName(), jobDesc.getGroupName()));
            }
            return false;
        } catch (SchedulerException e) {
            throw new HodorSchedulerException(e);
        }
    }

    @Override
    public boolean deleteJobs(List<JobDesc> jobDescList) {
        boolean result = true;
        for (JobDesc jobDesc : jobDescList) {
            result = deleteJob(jobDesc);
        }
        return result;
    }

    @Override
    public boolean checkExists(JobDesc jobDesc) {
        try {
            return scheduler.checkExists(JobKey.jobKey(jobDesc.getJobName(), jobDesc.getGroupName()));
        } catch (SchedulerException e) {
            throw new HodorSchedulerException(e);
        }
    }

    @Override
    public void pauseJob(JobDesc jobDesc) {
        try {
            scheduler.pauseJob(JobKey.jobKey(jobDesc.getJobName(), jobDesc.getGroupName()));
        } catch (SchedulerException e) {
            throw new HodorSchedulerException(e);
        }
    }

    @Override
    public boolean isPaused(JobDesc jobDesc) {
        try {
            return scheduler.isShutdown() && Trigger.TriggerState.PAUSED == scheduler.getTriggerState(new TriggerKey(jobDesc.getJobName(), jobDesc.getGroupName()));
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
    public void resumeJob(JobDesc jobDesc) {
        try {
            scheduler.resumeJob(JobKey.jobKey(jobDesc.getJobName(), jobDesc.getGroupName()));
        } catch (SchedulerException e) {
            throw new HodorSchedulerException(e);
        }
    }

    @Override
    public void triggerJob(JobDesc jobDesc) {
        try {
            scheduler.triggerJob(JobKey.jobKey(jobDesc.getJobName(), jobDesc.getGroupName()));
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
