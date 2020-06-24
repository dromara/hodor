package org.dromara.hodor.scheduler.api;

import org.dromara.hodor.scheduler.api.exception.HodorSchedulerException;

/**
 * @author tangzy
 * @since 1.0
 */
public class JobExecutorTypeManager {
    public static final JobExecutorTypeManager INSTANCE = new JobExecutorTypeManager();

    private JobExecutorTypeManager() {

    }

    private enum JobExecutorClassEnum {

        COMMON_JOB_EXECUTOR(0, "org.dromara.hodor.scheduler.api.executor.CommonJobExecutor"),
        TIME_JOB_EXECUTOR(1, "org.dromara.hodor.scheduler.api.executor.TimeJobExecutor"),
        WORKFLOW_JOB_EXECUTOR(2, "org.dromara.hodor.scheduler.api.executor.WorkFlowJobExecutor");

        private final int code;

        private final String name;

        JobExecutorClassEnum(int code, String className) {
            this.code = code;
            this.name = className;
        }

        public int getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public static JobExecutorClassEnum get(int code) {
            for (JobExecutorClassEnum jobClassEnum : JobExecutorClassEnum.values()) {
                if (jobClassEnum.code == code) {
                    return jobClassEnum;
                }
            }
            return JobExecutorClassEnum.COMMON_JOB_EXECUTOR;
        }
    }

    public JobExecutor getJobExecutor(int jobType) {
        JobExecutorClassEnum jobClassEnum = JobExecutorClassEnum.get(jobType);
        try {
            return (JobExecutor) Class.forName(jobClassEnum.getName()).newInstance();
        } catch (ReflectiveOperationException e) {
            throw new HodorSchedulerException("jobExecutor class '%s' can't initialize.", jobClassEnum.getName());
        }
    }

}
