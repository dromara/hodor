package org.dromara.hodor.model.enums;

/**
 * default job type
 *
 * @author tomgs
 * @since 1.0
 */
public enum JobType {

    /**
     * 普通任务
     */
    COMMON_JOB(0),

    /**
     * 工作流任务
     */
    WORKFLOW_JOB(1),

    /**
     * 分片任务
     */
    SHARDING_JOB(2),

    /**
     * 广播任务
     */
    BROADCAST_JOB(3),

    /**
     * map_reduce任务
     */
    MAPREDUCE_JOB(3);

    private final int code;

    JobType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
