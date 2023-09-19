package org.dromara.hodor.remoting.api.message.response;

import lombok.Getter;
import lombok.Setter;
import org.dromara.hodor.remoting.api.message.ResponseBody;

/**
 *  job execute log response
 *
 * @author tomgs
 * @version 2021/3/3 1.0
 */
@Getter
@Setter
public class JobExecuteLogResponse implements ResponseBody {

    private static final long serialVersionUID = -8444222502655435905L;

    private Long requestId;

    private String data;

    private Integer offset;

    private Integer length;

    @Override
    public String toString() {
        return "JobExecuteLogResponse{" +
            "requestId=" + getRequestId() + '\'' +
            "offset=" + offset + '\'' +
            "length=" + length + '\'' +
            // ", data length='" + data.length() + '\'' +
            '}';
    }

}
