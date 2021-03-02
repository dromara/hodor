package org.dromara.hodor.remoting.api.message.response;

import org.dromara.hodor.remoting.api.message.ResponseBody;

/**
 * remoting response
 *
 * @author tomgs
 * @since 2021/2/26
 */
public class RemotingResponse implements ResponseBody {

    private static final long serialVersionUID = 8889407473710885893L;

    private Long requestId;

    private Integer code;

    private String msg;

    private Object data;

    public RemotingResponse(Integer code, String msg) {
        this(0L, code, msg);
    }

    public RemotingResponse(Long requestId, Integer code, String msg) {
        this(requestId, code, msg, null);
    }

    public RemotingResponse(Long requestId, Integer code, String msg, Object data) {
        this.requestId = requestId;
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(Object data) {
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
        return requestId;
    }

    @Override
    public String toString() {
        return "RemotingResponse{" +
            "requestId=" + requestId +
            ", code=" + code +
            ", msg='" + msg + '\'' +
            ", data=" + data +
            '}';
    }

}
