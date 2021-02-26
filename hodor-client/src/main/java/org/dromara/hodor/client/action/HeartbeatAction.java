package org.dromara.hodor.client.action;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.client.core.HeartbeatResponse;
import org.dromara.hodor.client.core.RequestContext;

/**
 * @author tomgs
 * @since 2021/2/26
 */
@Slf4j
public class HeartbeatAction extends AbstractAction {

    public HeartbeatAction(final RequestContext context) {
        super(context);
    }

    @Override
    public void execute() throws Exception {
        RequestContext requestContext = getRequestContext();
        HeartbeatResponse response = new HeartbeatResponse(0, "success");
        //TODO: 心跳请求携带一些重复的消息
        requestContext.channel().send(response);
    }

    @Override
    public void exceptionCaught(Exception e) {
        log.error("heartbeat request handler exception, {}", e.getMessage(), e);
    }

}
