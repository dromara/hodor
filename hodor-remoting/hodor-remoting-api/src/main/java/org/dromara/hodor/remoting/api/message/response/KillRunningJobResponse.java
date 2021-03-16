package org.dromara.hodor.remoting.api.message.response;

/**
 *  kill running job response
 *
 * @author tomgs
 * @version 2021/3/3 1.0 
 */
public class KillRunningJobResponse extends AbstractResponseBody {

    private static final long serialVersionUID = 6568410342583057026L;

    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
