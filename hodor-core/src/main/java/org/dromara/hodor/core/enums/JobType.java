package org.dromara.hodor.core.enums;

/**
 * default job type
 *
 * @author tomgs
 * @since 2020/8/26
 */
public enum JobType {

    COMMON_JOB(0),

    TIME_JOB(1),

    WORKFLOW_JOB(2);

    private final int code;

    JobType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
