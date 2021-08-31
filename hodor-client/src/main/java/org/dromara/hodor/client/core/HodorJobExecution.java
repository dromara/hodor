package org.dromara.hodor.client.core;

import java.util.Date;
import lombok.Builder;
import lombok.Data;
import org.dromara.hodor.common.utils.HostUtils;
import org.dromara.hodor.model.enums.JobExecuteStatus;

/**
 * hodor_job_execution table entity
 *
 * @author tomgs
 * @since 2021/3/18
 */
@Data
@Builder
public class HodorJobExecution {

    private Long requestId;

    private String groupName;

    private String jobName;

    private String parameters;

    private String schedulerTag;

    private String clientHostname;

    private String clientIp;

    private Date startTime;

    private Date completeTime;

    private JobExecuteStatus status;

    private String comments;

    private byte[] result;

    public static HodorJobExecution createRunningJobExecution(Long requestId, String groupName, String jobName, String jobParameters, String schedulerName) {
        return HodorJobExecution.builder()
            .requestId(requestId)
            .groupName(groupName)
            .jobName(jobName)
            .parameters(jobParameters)
            .schedulerTag(schedulerName)
            .clientIp(HostUtils.getLocalIp())
            .clientHostname(HostUtils.getLocalHostName())
            .startTime(new Date())
            .status(JobExecuteStatus.RUNNING)
            .build();
    }

    public static HodorJobExecution createFailureJobExecution(Long requestId, String exceptionStack) {
        return HodorJobExecution.builder()
            .requestId(requestId)
            .status(JobExecuteStatus.FAILED)
            .comments(exceptionStack)
            .completeTime(new Date())
            .build();
    }

    public static HodorJobExecution createSuccessJobExecution(Long requestId, byte[] result) {
        return HodorJobExecution.builder()
            .requestId(requestId)
            .status(JobExecuteStatus.FAILED)
            .comments("success")
            .result(result)
            .completeTime(new Date())
            .build();
    }

    public static HodorJobExecution createKilledJobExecution(Long requestId) {
        return HodorJobExecution.builder()
            .requestId(requestId)
            .status(JobExecuteStatus.KILLED)
            .comments("killed")
            .completeTime(new Date())
            .build();
    }
}
