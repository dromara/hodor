package org.dromara.hodor.core.entity;

import java.util.Date;
import lombok.Data;
import org.dromara.hodor.core.enums.JobStatus;
import org.dromara.hodor.core.enums.Priority;

/**
 * hodor job info
 *
 * @author tomgs
 * @version 2020/8/2 1.0
 */
@Data
public class HodorJobInfo {

    private Integer id;

    private Integer hashId;

    private String groupName;

    private String jobName;

    private String jobCategory;

    private String jobType;

    private String jobPath;

    private String jobCommand;

    private Priority priority;

    private JobStatus jobStatus;

    private Boolean isDependence;

    private String cronExpression;

    private Integer shardingCount;

    private String jobParameters;

    private String extensibleParameters;

    private Boolean failover;

    private Boolean misfire;

    private Boolean fireNow;

    private Boolean isOnce;

    private Boolean isBroadcast;

    private String slaveIp;

    private Integer timeout;

    private Date activeTime;

    private Date endTime;

    private Date nextExecuteTime;

    private Date prevExecuteTime;

    private Date createTime;

    private String jobDataPath;

    private String jobDesc;

}
