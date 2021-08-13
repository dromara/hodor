package org.dromara.hodor.server.executor.job;

import org.dromara.hodor.core.entity.JobExecDetail;
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
        return null;
    }

    /**
     * requestId|groupName|jobName|...\n
     * @param detail JobExecDetail
     * @return detail string
     */
    default String toDetailString(String op, JobExecDetail detail) {
        StringBuilder sb = new StringBuilder(64);
        sb.append(op).append("|");
        sb.append(detail.getId()).append("|");
        sb.append(detail.getGroupName()).append("|");
        sb.append(detail.getJobName()).append("|");
        sb.append("\n");
        return sb.toString();
    }

    JobExecDetail getJobExecDetail(JobKey jobKey);

    void removeRunningJob(JobKey jobKey);

    void addSchedulerRunningJob(JobExecDetail jobExecDetail);
}
