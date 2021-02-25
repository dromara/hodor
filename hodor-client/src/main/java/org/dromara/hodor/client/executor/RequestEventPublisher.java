package org.dromara.hodor.client.executor;

import org.dromara.hodor.client.core.RequestContext;
import org.dromara.hodor.common.event.AbstractEventPublisher;

/**
 *  请求事件分发器
 *
 * @author tomgs
 * @version 2021/2/26 1.0 
 */

public class RequestEventPublisher extends AbstractEventPublisher<RequestContext> {

    @Override
    public void registerListener() {

    }

    public void notifyRequestHandler(RequestContext request) {

    }

}
