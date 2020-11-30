package org.dromara.hodor.core.enums;

/**
 * request type
 *
 * @author tomgs
 * @since 2020/9/24
 */
public enum RequestType {

    HEARTBEAT_REQUEST((byte) 0),

    JOB_EXEC_REQUEST((byte) 1),

    FETCH_JOB_STATUS_REQUEST((byte) 2),

    FETCH_JOB_LOG_REQUEST((byte) 3),

    KILL_JOB_REQUEST((byte) 4);

    private final byte type;

    RequestType(byte type) {
        this.type = type;
    }

    public byte getCode() {
        return type;
    }

    public static RequestType to(byte type) {
        RequestType requestType = null;
        switch (type) {
            case (byte) 0:
                requestType = HEARTBEAT_REQUEST;
                break;
            case (byte) 1:
                requestType = JOB_EXEC_REQUEST;
                break;
            case (byte) 2:
                requestType = FETCH_JOB_STATUS_REQUEST;
                break;
            case (byte) 3:
                requestType = FETCH_JOB_LOG_REQUEST;
                break;
            case (byte) 4:
                requestType = KILL_JOB_REQUEST;
                break;
            default:
                break;
        }
        return requestType;
    }

}
