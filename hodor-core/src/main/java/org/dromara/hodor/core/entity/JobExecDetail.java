package org.dromara.hodor.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;
import org.dromara.hodor.model.enums.JobExecuteStatus;

/**
 * job executor detail info
 *
 * @author tomgs
 * @since 1.0
 */
@Data
@TableName("hodor_job_exec_detail")
public class JobExecDetail {

    private Long id;

    private Long instanceId;

    private String groupName;

    private String jobName;

    private Integer shardingCount;

    private Integer shardingId;

    private String shardingParams;

    private String schedulerEndpoint;

    private String actuatorEndpoint;

    private Date scheduleStart;

    private Date scheduleEnd;

    private Date executeStart;

    private Date executeEnd;

    private JobExecuteStatus executeStatus;

    private Long elapsedTime;

    private Boolean isTimeout;

    private String comments;

    private byte[] jobExecData;

    private String execCommand;

}
