package org.dromara.hodor.admin.dto.job;

import lombok.Data;

import java.util.Date;

@Data
public class JobExecution {
    /**
     * 正在运行标志
     */
    private boolean running;

    /**
     * 已经完成标志
     */
    private boolean completed;

    /**
     * 已故障转移标志
     */
    private boolean failover;

    /**
     * 最近一次任务运行开始时间
     */
    private Date lastBeginTime;

    /**
     * 最近一次任务运行结束时间
     */
    private Date lastCompleteTime;

    /**
     * 最近一次调度开始时间
     */
    private Date lastScheduleBeginTime;

    /**
     * 最近一次调度结束时间
     */
    private Date lastScheduleCompleteTime;

    /**
     * 下次任务运行开始时间
     */
    private Date nextFireTime;

}
