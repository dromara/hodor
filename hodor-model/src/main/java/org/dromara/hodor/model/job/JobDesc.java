package org.dromara.hodor.model.job;

import java.util.Date;
import lombok.Data;
import org.dromara.hodor.model.enums.CommandType;
import org.dromara.hodor.model.enums.JobType;
import org.dromara.hodor.model.enums.Priority;

@Data
public class JobDesc {

  private Long id;

  private Long hashId;

  private String jobCategory;

  private String groupName;

  private String jobName;

  private JobType jobType;

  private String jobPath;

  private CommandType jobCommandType;

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

}
