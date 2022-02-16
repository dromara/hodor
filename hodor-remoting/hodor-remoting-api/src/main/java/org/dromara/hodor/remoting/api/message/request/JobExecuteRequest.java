package org.dromara.hodor.remoting.api.message.request;

import lombok.*;

/**
 *  request body
 *
 * @author tomgs
 * @version 2020/9/13 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class JobExecuteRequest extends AbstractRequestBody {

    private static final long serialVersionUID = -3703044901986185064L;

    private String jobName;

    private String groupName;

    private String jobPath;

    private String jobCommand;

    private String jobCommandType;

    private String jobParameters;

    private String extensibleParameters;

    private Integer timeout;

    private Integer shardId;

    private String shardName;

    private Integer retryCount;

    private Integer version;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Long requestId;

        private String jobName;

        private String groupName;

        private String jobPath;

        private String jobCommand;

        private String jobCommandType;

        private String jobParameters;

        private String extensibleParameters;

        private Integer timeout;

        private Integer shardId;

        private String shardName;

        // default 0 times
        private Integer retryCount = 0;

        // default -1
        private Integer version = -1;

        public Builder requestId(Long requestId) {
            this.requestId = requestId;
            return this;
        }

        public Builder jobName(String jobName) {
            this.jobName = jobName;
            return this;
        }

        public Builder groupName(String groupName) {
            this.groupName = groupName;
            return this;
        }

        public Builder jobPath(String jobPath) {
            this.jobPath = jobPath;
            return this;
        }

        public Builder jobCommand(String jobCommand) {
            this.jobCommand = jobCommand;
            return this;
        }

        public Builder jobCommandType(String jobCommandType) {
            this.jobCommandType = jobCommandType;
            return this;
        }

        public Builder jobParameters(String jobParameters) {
            this.jobParameters = jobParameters;
            return this;
        }

        public Builder extensibleParameters(String extensibleParameters) {
            this.extensibleParameters = extensibleParameters;
            return this;
        }

        public Builder timeout(Integer timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder shardId(Integer shardId) {
            this.shardId = shardId;
            return this;
        }

        public Builder shardName(String shardName) {
            this.shardName = shardName;
            return this;
        }

        public Builder retryCount(Integer retryCount) {
            this.retryCount = retryCount;
            return this;
        }

        public Builder version(Integer version) {
            this.version = version;
            return this;
        }

        public JobExecuteRequest build() {
            JobExecuteRequest request = new JobExecuteRequest();
            request.setRequestId(requestId);
            request.setGroupName(groupName);
            request.setJobName(jobName);
            request.setJobCommand(jobCommand);
            request.setJobPath(jobPath);
            request.setJobCommandType(jobCommandType);
            request.setJobParameters(jobParameters);
            request.setExtensibleParameters(extensibleParameters);
            request.setShardId(shardId);
            request.setShardName(shardName);
            request.setTimeout(timeout);
            request.setRetryCount(retryCount);
            request.setVersion(version);
            return request;
        }

    }

}

