package org.dromara.hodor.remoting.api.message.response;

/**
 *  job execute status response
 *
 * @author tomgs
 * @version 2021/3/3 1.0 
 */
public class JobExecuteStatusResponse extends AbstractResponseBody {

    private static final long serialVersionUID = 8892404430523254103L;

    private Integer status;

    private String startTime;

    private String completeTime;

    private String comments;

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

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "JobExecuteStatusResponse{" +
            "status=" + status +
            ", startTime='" + startTime + '\'' +
            ", completeTime='" + completeTime + '\'' +
            ", comments='" + comments + '\'' +
            ", result='" + result + '\'' +
            '}';
    }

}
