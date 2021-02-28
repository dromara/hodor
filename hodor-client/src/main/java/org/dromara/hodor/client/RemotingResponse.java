package org.dromara.hodor.client;

import org.dromara.hodor.remoting.api.message.ResponseBody;

/**
 * @author tomgs
 * @since 2021/2/26
 */
public class RemotingResponse implements ResponseBody {

    private static final long serialVersionUID = 8889407473710885893L;

    private int code;

    private String msg;

    private Object data;

    public RemotingResponse(int code, String msg) {
        this(code, msg, null);
    }

    public RemotingResponse(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public Long getRequestId() {
        return 0L;
    }

    @Override
    public String toString() {
        return "HeartbeatResponse{" +
            "code=" + code +
            ", msg='" + msg + '\'' +
            ", data=" + data +
            '}';
    }

}
