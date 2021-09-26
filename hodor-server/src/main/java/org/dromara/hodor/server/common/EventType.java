package org.dromara.hodor.server.common;

/**
 * event type
 *
 * @author tomgs
 */
public interface EventType {

    /**
     * 任务初始化分发
     */
    String JOB_INIT_DISTRIBUTE = "jobInitDistribute";

    /**
     * 任务创建事件
     */
    String JOB_CREATE_DISTRIBUTE = "jobCreateDistribute";

    /**
     * 任务更新事件
     */
    String JOB_UPDATE_DISTRIBUTE = "jobUpdateDistribute";

    /**
     * 任务删除事件
     */
    String JOB_DELETE_DISTRIBUTE = "jobDeleteDistribute";

    /**
     * scheduler 更新事件
     */
    String SCHEDULER_UPDATE = "schedulerUpdate";

    /**
     * scheduler 删除事件
     */
    String SCHEDULER_DELETE = "schedulerDelete";

}
