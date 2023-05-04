package org.dromara.hodor.admin.core;


public enum MsgCodeEnum {
    SUCCESSFUL(0, "success"),
    PART_FAILURE(1, "part failure"),
    INVALID_JOB(2, "invalid job"),
    RESPONSE_ERROR(500, "server error"),
    JSON_PARSE_ERROR(1000, "json parsing failed"),
    INVALID_SIGN(1002, "the signature is invalid"),
    REQUEST_TIMEOUT(1003, "request timed out"),
    INVALID_APPKEY(1004, "appKey is invalid"),
    EMPTY_PARAM(1005, "parameter is empty"),
    JOB_PARSE_ERROR(1006, "job parsing failed"),
    EMPTY_JOB(1007, "jobs is empty"),
    EMPTY_CRON(1008, "cron is empty"),
    INVALID_CRON(1009, "Invalid cron"),
    MISSING_PARAM(1010, "missing the necessary parameters"),
    FEWER_FIELDS(1011, " groupName ,jobName,jobUser can not be empty"),
    BATCH_INSERT_ERROR(1012, "The number of bulk insert exceeds the limit（1000）"),
    NOT_BIND_SESSION(1013, "The job not bind session"),
    OTHER_ERROR(10000, "other error");

    MsgCodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    int code;
    String desc;

    public int getCode() {
        return code;
    }

    public MsgCodeEnum setCode(int code) {
        this.code = code;
        return this;
    }

    public String getDesc() {
        return desc;
    }

    public MsgCodeEnum setDesc(String desc) {
        this.desc = desc;
        return this;
    }

}

