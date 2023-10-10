package org.dromara.hodor.model.job;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.hodor.model.enums.JobType;
import org.dromara.hodor.model.enums.Priority;
import org.dromara.hodor.model.enums.ScheduleStrategy;
import org.dromara.hodor.model.enums.TimeType;

/**
 * job info describe
 *
 * @author tomgs
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDesc {

    private Long id;

    private Long hashId;

    private String jobCategory;

    private String groupName;

    private String jobName;

    /**
     * 任务类型：普通任务、工作流任务、分片任务
     */
    private JobType jobType;

    /**
     * 任务命令类型：java、shell、python等，根据执行器支持执行的任务
     */
    private String jobCommandType;

    /**
     * 任务执行的命令，Java任务即执行的方法
     */
    private String jobCommand;

    /**
     * 调度策略
     */
    private ScheduleStrategy scheduleStrategy;

    /**
     * 调度策略表达式
     */
    private String scheduleExp;

    private TimeType timeType;

    private String timeExp;

    private String timeZone;

    private String jobParameters;

    private String extensibleParameters;

    private Priority priority;

    private Boolean failover;

    private Boolean misfire;

    private Boolean fireNow;

    private Integer timeout;

    private Date startTime;

    private Date endTime;

    private Integer retryCount;

    private Integer version;

}
