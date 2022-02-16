package org.dromara.hodor.model.job;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.hodor.model.enums.JobType;
import org.dromara.hodor.model.enums.Priority;

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

  private JobType jobType;

  private String jobPath;

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

  private Date endTime;

  private Integer retryCount;

  private Integer version;

}
