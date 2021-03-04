package org.dromara.hodor.remoting.api.message;

/**
 * remoting response
 *
 * @author tomgs
 * @since 2021/3/4
 */
public class RemotingResponse implements ResponseBody {

    private static final long serialVersionUID = -544536043615257748L;

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

    public static RemotingResponse succeeded(Object data) {
        return succeeded(0L, data);
    }

    public static RemotingResponse succeeded(Long requestId, Object data) {
        return new RemotingResponse(requestId, RemotingStatus.SUCCEEDED, "success", data);
    }

    public static RemotingResponse failed(String msg) {
        return failed(0L, msg);
    }

    public static RemotingResponse failed(Long requestId, String msg) {
        return failed(requestId, msg, null);
    }

    public static RemotingResponse failed(Long requestId, String msg, Object data) {
        return new RemotingResponse(requestId, RemotingStatus.FAILED, msg, data);
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

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

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
