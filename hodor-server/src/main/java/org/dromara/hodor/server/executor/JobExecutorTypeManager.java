package org.dromara.hodor.server.executor;

import org.dromara.hodor.model.enums.JobType;
import org.dromara.hodor.scheduler.api.JobExecutor;

/**
 * job executor type manager
 *
 * @author tangzy
 * @since 1.0
 */
public class JobExecutorTypeManager {

    private static final JobExecutorTypeManager INSTANCE = new JobExecutorTypeManager();

    private JobExecutorTypeManager() {

    }

    public static JobExecutorTypeManager getInstance() {
        return INSTANCE;
    }

    private enum JobExecutorClassEnum {

        COMMON_JOB_EXECUTOR(JobType.COMMON_JOB, new CommonJobExecutor()),
        WORKFLOW_JOB_EXECUTOR(JobType.WORKFLOW_JOB, new FlowJobExecutor()),
        SHARDING_JOB_EXECUTOR(JobType.SHARDING_JOB, new ShardingJobExecutor()),
        BROADCAST_JOB_EXECUTOR(JobType.BROADCAST_JOB, new BroadcastJobExecutor()),
        MAP_REDUCE_JOB_EXECUTOR(JobType.MAP_REDUCE_JOB, new MapReduceJobExecutor());

        private final JobType jobType;

        private final JobExecutor jobExecutor;

        JobExecutorClassEnum(JobType jobType, JobExecutor jobExecutor) {
            this.jobType = jobType;
            this.jobExecutor = jobExecutor;
        }

        public JobType getJobType() {
            return jobType;
        }

        public JobExecutor getJobExecutor() {
            return jobExecutor;
        }

        public static JobExecutorClassEnum get(JobType jobType) {
            // if branch predicate
            if (JobExecutorClassEnum.COMMON_JOB_EXECUTOR.jobType == jobType) {
                return JobExecutorClassEnum.COMMON_JOB_EXECUTOR;
            }
            for (JobExecutorClassEnum jobClassEnum : JobExecutorClassEnum.values()) {
                if (jobClassEnum.jobType == jobType) {
                    return jobClassEnum;
                }
            }
            throw new IllegalArgumentException("Illegal job type : " + jobType.name());
        }
    }

    public JobExecutor getJobExecutor(JobType jobType) {
        JobExecutorClassEnum jobClassEnum = JobExecutorClassEnum.get(jobType);
        return jobClassEnum.getJobExecutor();
    }

}
