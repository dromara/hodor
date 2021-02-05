package org.dromara.hodor.server.executor.handler;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.core.enums.RequestType;
import org.dromara.hodor.remoting.api.HodorChannel;
import org.dromara.hodor.remoting.api.message.RemotingResponse;
import org.dromara.hodor.remoting.api.message.ResponseBody;

/**
 * @author tomgs
 * @since 2020/11/30
 */
@Slf4j
public class JobResponseHandler extends AbstractHodorClientChannelHandler {

    @Override
    protected void received0(HodorChannel channel, RemotingResponse<ResponseBody> message) throws Exception {
        byte type = message.getHeader().getType();
        switch (RequestType.to(type)) {
            case JOB_EXEC_REQUEST:
                log.info("request type");
                break;
            case HEARTBEAT_REQUEST:
                log.info("heartbeat type");
                break;
            case FETCH_JOB_STATUS_REQUEST:
                log.info("fetch job status type");
                break;
            case FETCH_JOB_LOG_REQUEST:
                log.info("fetch job log type");
                break;
            case KILL_JOB_REQUEST:
                log.info("kill job type");
                break;
            default:
                break;
        }
    }

}
