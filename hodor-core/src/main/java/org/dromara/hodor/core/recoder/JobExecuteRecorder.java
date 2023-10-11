package org.dromara.hodor.core.recoder;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.TextStringBuilder;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.core.entity.JobExecDetail;
import org.dromara.hodor.model.enums.JobExecuteStatus;
import org.dromara.hodor.model.job.JobKey;

/**
 * JobExecuteRecorder
 *
 * @author tomgs
 * @since 1.0
 */
public interface JobExecuteRecorder {

    String OP_INSERT = "I";

    String OP_UPDATE = "U";

    /**
     * 记录任务执行明细
     *
     * @param op     操作
     * @param detail 任务明细
     */
    void recordJobExecDetail(String op, JobExecDetail detail);

    /**
     * 将字符数据转换为对象
     *
     * @param detailString 明细字符数据
     * @return 明细对象
     */
    default JobExecDetail toRawJobExecDetail(String detailString) {
        List<String> strings = StringUtils.splitToList(detailString, "|");
        JobExecDetail jobExecDetail = new JobExecDetail();
        final String requestId = strings.get(1);
        final String instanceId = strings.get(2);
        final String groupName = strings.get(3);
        final String jobName = strings.get(4);
        final String shardingCount = strings.get(5);
        final String shardingId = strings.get(6);
        final String shardingParams = strings.get(7);
        final String schedulerEndpoint = strings.get(8);
        final String actuatorEndpoint = strings.get(9);
        final String scheduleStart = strings.get(10);
        final String scheduleEnd = strings.get(11);
        final String executeStart = strings.get(12);
        final String executeEnd = strings.get(13);
        final String executeStatus = strings.get(14);
        final String elapsedTime = strings.get(15);
        final String isTimeout = strings.get(16);
        final String comments = strings.get(17);
        final String jobExecData = strings.get(18);

        jobExecDetail.setId(Long.parseLong(requestId));
        StringUtils.ofBlankable(instanceId).ifPresent(e -> jobExecDetail.setInstanceId(Long.parseLong(e)));
        StringUtils.ofBlankable(groupName).ifPresent(jobExecDetail::setGroupName);
        StringUtils.ofBlankable(jobName).ifPresent(jobExecDetail::setJobName);
        StringUtils.ofBlankable(groupName).ifPresent(jobExecDetail::setGroupName);
        StringUtils.ofBlankable(shardingCount).ifPresent(e -> jobExecDetail.setShardingCount(Integer.parseInt(e)));
        StringUtils.ofBlankable(shardingId).ifPresent(e -> jobExecDetail.setShardingId(Integer.parseInt(e)));
        StringUtils.ofBlankable(shardingParams).ifPresent(jobExecDetail::setShardingParams);
        StringUtils.ofBlankable(schedulerEndpoint).ifPresent(jobExecDetail::setSchedulerEndpoint);
        StringUtils.ofBlankable(actuatorEndpoint).ifPresent(jobExecDetail::setActuatorEndpoint);
        StringUtils.ofBlankable(scheduleStart).ifPresent(e -> jobExecDetail.setScheduleStart(DateUtil.parse(e, DatePattern.NORM_DATETIME_PATTERN)));
        StringUtils.ofBlankable(scheduleEnd).ifPresent(e -> jobExecDetail.setScheduleEnd(DateUtil.parse(e, DatePattern.NORM_DATETIME_PATTERN)));
        StringUtils.ofBlankable(executeStart).ifPresent(e -> jobExecDetail.setExecuteStart(DateUtil.parse(e, DatePattern.NORM_DATETIME_PATTERN)));
        StringUtils.ofBlankable(executeEnd).ifPresent(e -> jobExecDetail.setExecuteEnd(DateUtil.parse(e, DatePattern.NORM_DATETIME_PATTERN)));
        StringUtils.ofBlankable(executeStatus).ifPresent(e -> jobExecDetail.setExecuteStatus(JobExecuteStatus.ofName(e)));
        StringUtils.ofBlankable(elapsedTime).ifPresent(e -> jobExecDetail.setElapsedTime(Long.parseLong(e)));
        StringUtils.ofBlankable(isTimeout).ifPresent(e -> jobExecDetail.setIsTimeout(Boolean.parseBoolean(e)));
        StringUtils.ofBlankable(comments).ifPresent(jobExecDetail::setComments);
        StringUtils.ofBlankable(jobExecData).ifPresent(e -> jobExecDetail.setJobExecData(e.getBytes(StandardCharsets.UTF_8)));
        return jobExecDetail;
    }

    /**
     * requestId|groupName|jobName|...\n
     *
     * @param detail JobExecDetail
     * @return detail string
     */
    default String toDetailString(String op, JobExecDetail detail) {
        TextStringBuilder sb = StringUtils.getStringBuilder();
        sb.setNullText("");
        sb.append(op).append("|");
        sb.append(detail.getId()).append("|");
        sb.append(detail.getInstanceId()).append("|");
        sb.append(detail.getGroupName()).append("|");
        sb.append(detail.getJobName()).append("|");
        sb.append(detail.getShardingCount()).append("|");
        sb.append(detail.getShardingId()).append("|");
        sb.append(detail.getShardingParams()).append("|");
        sb.append(detail.getSchedulerEndpoint()).append("|");
        sb.append(detail.getActuatorEndpoint()).append("|");
        sb.append(detail.getScheduleStart()).append("|");
        sb.append(detail.getScheduleEnd()).append("|");
        sb.append(detail.getExecuteStart()).append("|");
        sb.append(detail.getExecuteEnd()).append("|");
        sb.append(detail.getExecuteStatus()).append("|");
        sb.append(detail.getElapsedTime()).append("|");
        sb.append(detail.getIsTimeout()).append("|");
        sb.append(StringEscapeUtils.ESCAPE_JAVA.translate(detail.getComments())).append("|");
        sb.append(new String(Optional.ofNullable(detail.getJobExecData()).orElse(new byte[0]), StandardCharsets.UTF_8));
        return sb.toString();
    }

    JobExecDetail getJobExecDetail(JobKey jobKey);

    void removeJobExecDetail(JobKey jobKey);

    void addJobExecDetail(JobExecDetail jobExecDetail);

    void startReporterJobExecDetail();

    void stopReporterJobExecDetail();
}
