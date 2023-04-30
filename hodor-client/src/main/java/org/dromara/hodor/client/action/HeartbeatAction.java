package org.dromara.hodor.client.action;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.remoting.api.message.RequestContext;
import org.dromara.hodor.remoting.api.message.request.HeartbeatRequest;
import org.dromara.hodor.remoting.api.message.response.HeartbeatResponse;

/**
 * heartbeat action
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class HeartbeatAction extends AbstractAction<HeartbeatRequest, HeartbeatResponse> {

    public HeartbeatAction(final RequestContext context) {
        super(context);
    }

    @Override
    public HeartbeatResponse executeRequest(HeartbeatRequest request) {
        return null;
    }

}
