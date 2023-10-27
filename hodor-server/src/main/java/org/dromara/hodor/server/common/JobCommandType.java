package org.dromara.hodor.server.common;

import lombok.Getter;

/**
 * job command type
 *
 * @author tomgs
 * @version 1.0
 */
@Getter
public enum JobCommandType {

    /**
     * 任务创建事件
     */
    JOB_CREATE_CMD("jobCreate"),

    /**
     * 任务更新事件
     */
    JOB_UPDATE_CMD("jobUpdate"),

    /**
     * 任务删除事件
     */
    JOB_DELETE_CMD("jobDelete"),

    /**
     * 执行任务
     */
    JOB_EXECUTE_CMD("jobExecute"),

    /**
     * 恢复调度
     */
    JOB_RESCHEDULE_CMD("jobReschedule");

    private final String name;

    JobCommandType(String name) {
        this.name = name;
    }

}
