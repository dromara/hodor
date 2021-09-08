package org.dromara.hodor.server.executor.handler;

import org.dromara.hodor.remoting.api.message.RemotingResponse;
import org.dromara.hodor.remoting.api.message.response.JobExecuteResponse;

/**
 * ResponseHandler
 *
 * @author tomgs
 * @since 2021/9/8
 */
public interface ResponseHandler {

    void fireJobResponseHandler(RemotingResponse<JobExecuteResponse> remotingResponse);

}
