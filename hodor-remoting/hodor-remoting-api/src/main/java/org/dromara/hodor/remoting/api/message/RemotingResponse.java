package org.dromara.hodor.remoting.api.message;

/**
 * remoting response
 *
 * @author tomgs
 * @since 2021/3/4
 */
public class RemotingResponse<T> {

    /**
     * @see RemotingStatus
     * */
    private Integer code;

    private String msg;

    private T data;

    public RemotingResponse(Integer code, String msg) {
        this(code, msg, null);
    }

    public RemotingResponse(Integer code, String msg, T data) {
        //this.requestId = requestId;
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> RemotingResponse<T> succeeded(T data) {
        return new RemotingResponse<>(RemotingStatus.SUCCEEDED, "success", data);
    }

    public static <T> RemotingResponse<T>  failed(String msg) {
        return failed(msg, null);
    }

    public static <T> RemotingResponse<T>  failed(String msg, T data) {
        return new RemotingResponse<>(RemotingStatus.FAILED, msg, data);
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public boolean isSuccess() {
        return code == RemotingStatus.SUCCEEDED;
    }

    @Override
    public String toString() {
        return "RemotingResponse{" +
            ", code=" + code +
            ", msg='" + msg + '\'' +
            ", data=" + data +
            '}';
    }

}
