package org.dromara.hodor.model.job;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.hodor.model.enums.JobType;
import org.dromara.hodor.model.enums.Priority;

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
   * 任务类型：普通任务、定时任务、工作流任务
   */
  private JobType jobType;

  private String jobPath;

  /**
   * 任务命令类型：java、shell、python等，根据执行器支持执行的任务
   */
  private String jobCommandType;

  private String jobCommand;

  private Priority priority;

  private Boolean isDependence;

  private String cron;

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

  private Date startTime;

  private Date endTime;

  private Integer retryCount;

  private Integer version;

}
