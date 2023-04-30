package org.dromara.hodor.actuator.bigdata.jobtype.javautils;

import cn.hutool.core.date.DateUtil;
import java.io.IOException;
import java.util.Objects;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dromara.hodor.actuator.api.utils.Props;
import org.dromara.hodor.actuator.bigdata.executor.CommonJobProperties;
import org.dromara.hodor.actuator.bigdata.jobtype.HadoopJobUtils;
import org.dromara.hodor.actuator.bigdata.queue.AbstractTask;
import org.dromara.hodor.actuator.bigdata.queue.ITask;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.model.enums.JobExecuteStatus;
import org.dromara.hodor.remoting.api.message.Header;
import org.dromara.hodor.remoting.api.message.RemotingMessage;
import org.dromara.hodor.remoting.api.message.RemotingResponse;
import org.dromara.hodor.remoting.api.message.RequestContext;
import org.dromara.hodor.remoting.api.message.response.JobExecuteResponse;

import static org.apache.hadoop.yarn.api.records.YarnApplicationState.FAILED;
import static org.apache.hadoop.yarn.api.records.YarnApplicationState.FINISHED;
import static org.apache.hadoop.yarn.api.records.YarnApplicationState.KILLED;

/**
 * AsyncJobStateTask
 *
 * @author tomgs
 * @since 1.0
 **/
public class AsyncJobStateTask extends AbstractTask {

    private final Logger logger = LogManager.getLogger(AsyncJobStateTask.class);

    private String appId;

    private Long requestId;

    private Props props;

    @Override
    public ITask runTask() {
        ApplicationReport report = null;
        try {
            report = HadoopJobUtils.fetchJobStateOnCluster(getAppId());
        } catch (YarnException | IOException e) {
            logger.error(StringUtils.format("AsyncJobStateTask ## get report error, errorMsg: {}.", e.getMessage()), e);
        }
        logger.info(StringUtils.format("AsyncJobStateTask ## report: {}", report));

        if (report != null && isFinished(report)) {
            RequestContext context = (RequestContext) props.get(CommonJobProperties.JOB_CONTEXT);
            JobExecuteResponse response = new JobExecuteResponse();
            response.setRequestId(requestId);
            response.setStatus(changeStatusCode(report));
            response.setCompleteTime(DateUtil.formatDateTime(DateUtil.date(report.getFinishTime())));
            response.setProcessTime(report.getFinishTime() - report.getStartTime());

            byte[] body = context.serializer().serialize(RemotingResponse.succeeded(response));
            RemotingMessage remotingMessage = new RemotingMessage();
            remotingMessage.setHeader(Header.builder()
                .magic(context.requestHeader().getMagic())
                .id(context.requestHeader().getId())
                .version(context.requestHeader().getVersion())
                .attachment(context.requestHeader().getAttachment())
                .type(context.requestHeader().getType())
                .length(body.length)
                .build());
            remotingMessage.setBody(body);
            context.channel().send(remotingMessage);
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

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Props getProps() {
        return props;
    }

    public void setProps(Props props) {
        this.props = props;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AsyncJobStateTask that = (AsyncJobStateTask) o;
        return appId.equals(that.appId) && requestId.equals(that.requestId) && props.equals(that.props);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appId, requestId, props);
    }
}


