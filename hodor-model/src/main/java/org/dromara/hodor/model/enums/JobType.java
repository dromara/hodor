package org.dromara.hodor.model.enums;

/**
 * default job type
 *
 * @author tomgs
 * @since 1.0
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
