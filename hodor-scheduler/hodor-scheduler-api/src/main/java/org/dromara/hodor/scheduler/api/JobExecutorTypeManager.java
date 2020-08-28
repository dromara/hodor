package org.dromara.hodor.scheduler.api;

import org.dromara.hodor.core.enums.JobType;
import org.dromara.hodor.scheduler.api.exception.HodorSchedulerException;
import org.dromara.hodor.scheduler.api.executor.CommonJobExecutor;
import org.dromara.hodor.scheduler.api.executor.TimeJobExecutor;
import org.dromara.hodor.scheduler.api.executor.WorkFlowJobExecutor;

/**
 * @author tangzy
 * @since 1.0
 */
public class JobExecutorTypeManager {

    public static final JobExecutorTypeManager INSTANCE = new JobExecutorTypeManager();

    private JobExecutorTypeManager() {

    }

    private enum JobExecutorClassEnum {

        COMMON_JOB_EXECUTOR(JobType.COMMON_JOB, CommonJobExecutor.class.getName()),
        TIME_JOB_EXECUTOR(JobType.TIME_JOB, TimeJobExecutor.class.getName()),
        WORKFLOW_JOB_EXECUTOR(JobType.WORKFLOW_JOB, WorkFlowJobExecutor.class.getName());

        private final JobType jobType;

        private final String name;

        JobExecutorClassEnum(JobType jobType, String className) {
            this.jobType = jobType;
            this.name = className;
        }

        public JobType getJobType() {
            return jobType;
        }

        public String getName() {
            return name;
        }

        public static JobExecutorClassEnum get(JobType jobType) {
            for (JobExecutorClassEnum jobClassEnum : JobExecutorClassEnum.values()) {
                if (jobClassEnum.jobType == jobType) {
                    return jobClassEnum;
                }
            }
            return JobExecutorClassEnum.COMMON_JOB_EXECUTOR;
        }
    }

    public JobExecutor getJobExecutor(JobType jobType) {
        JobExecutorClassEnum jobClassEnum = JobExecutorClassEnum.get(jobType);
        try {
            return (JobExecutor) Class.forName(jobClassEnum.getName()).newInstance();
        } catch (ReflectiveOperationException e) {
            throw new HodorSchedulerException("jobExecutor class '%s' can't initialize.", jobClassEnum.getName());
        }
    }

}
