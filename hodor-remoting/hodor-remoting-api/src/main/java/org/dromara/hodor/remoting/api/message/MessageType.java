package org.dromara.hodor.remoting.api.message;

import org.dromara.hodor.remoting.api.exception.RemotingException;
import org.dromara.hodor.remoting.api.message.request.HeartbeatRequest;
import org.dromara.hodor.remoting.api.message.request.JobExecuteLogRequest;
import org.dromara.hodor.remoting.api.message.request.JobExecuteRequest;
import org.dromara.hodor.remoting.api.message.request.JobExecuteStatusRequest;
import org.dromara.hodor.remoting.api.message.request.KillRunningJobRequest;

/**
 * message type
 *
 * @author tomgs
 * @since 2020/9/24
 */
public enum MessageType {

    HEARTBEAT_REQUEST((byte) 0, HeartbeatRequest.class),

    JOB_EXEC_REQUEST((byte) 1, JobExecuteRequest.class),

    FETCH_JOB_STATUS_REQUEST((byte) 2, JobExecuteStatusRequest.class),

    FETCH_JOB_LOG_REQUEST((byte) 3, JobExecuteLogRequest.class),

    KILL_JOB_REQUEST((byte) 4, KillRunningJobRequest.class);

    private final byte type;

    private final Class<? extends RequestBody> messageClass;

    MessageType(final byte type, final Class<? extends RequestBody> messageClass) {
        this.type = type;
        this.messageClass = messageClass;
    }

    public byte getType() {
        return type;
    }

    public Class<? extends RequestBody> getMessageClass() {
        return messageClass;
    }

    public static MessageType to(byte type) {
        MessageType requestType;
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
                throw new RemotingException("not found message type, " + type);
        }
        return requestType;
    }

}
