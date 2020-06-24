package org.dromara.hodor.scheduler.api;

import org.dromara.hodor.scheduler.api.exception.HodorSchedulerException;

/**
 * @author tangzy
 * @since 1.0
 */
public class JobTypeManager {
    public static final JobTypeManager INSTANCE = new JobTypeManager();

    private JobTypeManager() {

    }

    private enum JobClassEnum {
        // TODO: 这个地方怎么处理比较好
        COMMON(0, "com.tomgs.scheduler.quartz.job.InitJob");

        private final int code;

        private final String name;

        JobClassEnum(int code, String className) {
            this.code = code;
            this.name = className;
        }

        public int getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public static JobClassEnum get(int code) {
            for (JobClassEnum jobClassEnum : JobClassEnum.values()) {
                if (jobClassEnum.code == code) {
                    return jobClassEnum;
                }
            }
            return JobClassEnum.COMMON;
        }
    }

    public Class<?> getJobClass(int jobType) {
        JobClassEnum jobClassEnum = JobClassEnum.get(jobType);
        try {
            return Class.forName(jobClassEnum.getName());
        } catch (ClassNotFoundException e) {
            throw new HodorSchedulerException("get job class exception.", e);
        }
    }

}
