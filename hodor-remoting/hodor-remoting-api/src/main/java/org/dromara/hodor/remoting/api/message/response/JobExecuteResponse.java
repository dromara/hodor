package org.dromara.hodor.remoting.api.message.response;

/**
 * scheduled response
 *
 * @author tomgs
 * @since 2021/2/26
 */
public class JobExecuteResponse extends AbstractResponseBody {

    private static final long serialVersionUID = 8889407473710885893L;

    private Integer status;

    private String startTime;

    private String completeTime;

    private Integer processTime;

    private Integer shardId;

    private String shardName;

    private String result;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(String completeTime) {
        this.completeTime = completeTime;
    }

    public Integer getProcessTime() {
        return processTime;
    }

    public void setProcessTime(Integer processTime) {
        this.processTime = processTime;
    }

    public Integer getShardId() {
        return shardId;
    }

    public void setShardId(Integer shardId) {
        this.shardId = shardId;
    }

    public String getShardName() {
        return shardName;
    }

    public void setShardName(String shardName) {
        this.shardName = shardName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "JobExecuteResponse{" +
            "status=" + status +
            ", startTime='" + startTime + '\'' +
            ", completeTime='" + completeTime + '\'' +
            ", processTime=" + processTime +
            ", shardId=" + shardId +
            ", shardName='" + shardName + '\'' +
            ", result='" + result + '\'' +
            '}';
    }
}
