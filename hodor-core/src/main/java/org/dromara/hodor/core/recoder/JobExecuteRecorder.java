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
 */
public interface JobExecuteRecorder {

    String OP_INSERT = "I";

    String OP_UPDATE = "U";

    /**
     * 记录任务执行明细
     * @param op 操作
     * @param detail 任务明细
     */
    void recordJobExecDetail(String op, JobExecDetail detail);

    /**
     * 将字符数据转换为对象
     * @param detailString 明细字符数据
     * @return 明细对象
     */
    default JobExecDetail toRawJobExecDetail(String detailString) {
        List<String> strings = StringUtils.splitToList(detailString, "|");
        JobExecDetail jobExecDetail = new JobExecDetail();
        jobExecDetail.setId(Long.parseLong(strings.get(1)));
        if (StringUtils.isNotBlank(strings.get(2))) {
            jobExecDetail.setGroupName(strings.get(2));
        }
        if (StringUtils.isNotBlank(strings.get(3))) {
            jobExecDetail.setJobName(strings.get(3));
        }
        if (StringUtils.isNotBlank(strings.get(4))) {
            jobExecDetail.setSchedulerEndpoint(strings.get(4));
        }
        if (StringUtils.isNotBlank(strings.get(5))) {
            jobExecDetail.setActuatorEndpoint(strings.get(5));
        }
        if (StringUtils.isNotBlank(strings.get(6))) {
            jobExecDetail.setScheduleStart(DateUtil.parse(strings.get(6), DatePattern.NORM_DATETIME_PATTERN));
        }
        if (StringUtils.isNotBlank(strings.get(7))) {
            jobExecDetail.setScheduleEnd(DateUtil.parse(strings.get(7), DatePattern.NORM_DATETIME_PATTERN));
        }
        if (StringUtils.isNotBlank(strings.get(8))) {
            jobExecDetail.setExecuteStart(DateUtil.parse(strings.get(8), DatePattern.NORM_DATETIME_PATTERN));
        }
        if (StringUtils.isNotBlank(strings.get(9))) {
            jobExecDetail.setExecuteEnd(DateUtil.parse(strings.get(9), DatePattern.NORM_DATETIME_PATTERN));
        }
        if (StringUtils.isNotBlank(strings.get(10))) {
            jobExecDetail.setExecuteStatus(JobExecuteStatus.ofName(strings.get(10)));
        }
        if (StringUtils.isNotBlank(strings.get(11))) {
            jobExecDetail.setElapsedTime(Integer.parseInt(strings.get(11)));
        }
        if (StringUtils.isNotBlank(strings.get(12))) {
            jobExecDetail.setIsTimeout(Boolean.parseBoolean(strings.get(12)));
        }
        if (StringUtils.isNotBlank(strings.get(13))) {
            jobExecDetail.setParentRequestId(strings.get(13));
        }
        if (StringUtils.isNotBlank(strings.get(14))) {
            jobExecDetail.setComments(strings.get(14));
        }
        if (StringUtils.isNotBlank(strings.get(15))) {
            jobExecDetail.setJobExeData(strings.get(15).getBytes(StandardCharsets.UTF_8));
        }
        return jobExecDetail;
    }

    /**
     * requestId|groupName|jobName|...\n
     * @param detail JobExecDetail
     * @return detail string
     */
    default String toDetailString(String op, JobExecDetail detail) {
        TextStringBuilder sb = StringUtils.getStringBuilder();
        sb.setNullText("");
        sb.append(op).append("|");
        sb.append(detail.getId()).append("|");
        sb.append(detail.getGroupName()).append("|");
        sb.append(detail.getJobName()).append("|");
        sb.append(detail.getSchedulerEndpoint()).append("|");
        sb.append(detail.getActuatorEndpoint()).append("|");
        sb.append(detail.getScheduleStart()).append("|");
        sb.append(detail.getScheduleEnd()).append("|");
        sb.append(detail.getExecuteStart()).append("|");
        sb.append(detail.getExecuteEnd()).append("|");
        sb.append(detail.getExecuteStatus()).append("|");
        sb.append(detail.getElapsedTime()).append("|");
        sb.append(detail.getIsTimeout()).append("|");
        sb.append(detail.getParentRequestId()).append("|");
        sb.append(StringEscapeUtils.ESCAPE_JAVA.translate(detail.getComments())).append("|");
        sb.append(new String(Optional.ofNullable(detail.getJobExeData()).orElse(new byte[0]), StandardCharsets.UTF_8));
        return sb.toString();
    }

    JobExecDetail getJobExecDetail(JobKey jobKey);

    void removeJobExecDetail(JobKey jobKey);

    void addJobExecDetail(JobExecDetail jobExecDetail);

    void startReporterJobExecDetail();
}
