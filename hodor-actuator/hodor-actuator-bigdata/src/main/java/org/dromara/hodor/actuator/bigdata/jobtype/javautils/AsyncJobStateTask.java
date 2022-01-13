package org.dromara.hodor.actuator.bigdata.jobtype.javautils;

import java.io.IOException;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.dromara.hodor.actuator.bigdata.jobtype.HadoopJobUtils;
import org.dromara.hodor.actuator.bigdata.queue.AbstractTask;
import org.dromara.hodor.actuator.bigdata.queue.ITask;
import org.dromara.hodor.model.enums.JobExecuteStatus;

import static org.apache.hadoop.yarn.api.records.YarnApplicationState.FAILED;
import static org.apache.hadoop.yarn.api.records.YarnApplicationState.FINISHED;
import static org.apache.hadoop.yarn.api.records.YarnApplicationState.KILLED;

/**
 * AsyncJobStateTask
 *
 * @author tomgs
 * @since 2021.11.23
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Slf4j
public class AsyncJobStateTask extends AbstractTask {

    private String appId;

    private String requestId;

    @Override
    public ITask runTask() {
        ApplicationReport report = null;
        try {
            report = HadoopJobUtils.fetchJobStateOnCluster(getAppId());
        } catch (YarnException | IOException e) {
            log.error("AsyncJobStateTask ## get report error, errorMsg: {}.", e.getMessage(), e);
        }
        log.info("AsyncJobStateTask ## report: {}", report);

        if (report != null && isFinished(report)) {
            /*RpcResponse response = new RpcResponse();
            response.setRequestId(getRequestId());
            response.setJobCompleteTime(DateUtil.local2Utc(new Date()));
            response.setException(report.getFinalApplicationStatus().name());
            response.setCode(String.valueOf(changeStatusCode(report)));

            ChannelFuture channelFuture = getChannel().writeAndFlush(response);
            channelFuture.addListener(new ResendFutureListener(response, getChannel()));

            //将任务从执行中map移出到完成map
            ClientLinkedService.removeChannelWithRequestId(getRequestId());
            ExecutorManager.INSTANCE.removeRunningJob(getRequestId());
            ExecutorManager.INSTANCE.putFinishedJob(getRequestId(), getContext());*/



            return null;
        }

        return this;
    }

    private boolean isFinished(ApplicationReport report) {
        return report.getYarnApplicationState() == FINISHED
            || report.getYarnApplicationState() == FAILED
            || report.getYarnApplicationState() == KILLED;
    }

    private JobExecuteStatus changeStatusCode(ApplicationReport report) {
        YarnApplicationState yarnApplicationState = report.getYarnApplicationState();
        JobExecuteStatus status = JobExecuteStatus.FAILED;
        switch (yarnApplicationState) {
            case FINISHED:
                status = JobExecuteStatus.SUCCEEDED;
                break;
            case KILLED:
                status = JobExecuteStatus.KILLED;
                break;
        }
        return status;
    }
}


