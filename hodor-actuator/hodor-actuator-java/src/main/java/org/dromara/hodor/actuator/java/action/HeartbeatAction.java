package org.dromara.hodor.actuator.java.action;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.client.core.RequestContext;
import org.dromara.hodor.remoting.api.message.request.HeartbeatRequest;
import org.dromara.hodor.remoting.api.message.response.HeartbeatResponse;

/**
 * heartbeat action
 *
 * @author tomgs
 * @since 2021/2/26
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