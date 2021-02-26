package org.dromara.hodor.client.action;

import org.dromara.hodor.client.core.RequestContext;
import org.dromara.hodor.common.executor.HodorRunnable;

/**
 * abstract action
 *
 * @author tomgs
 * @since 2021/2/26
 */
public abstract class AbstractAction extends HodorRunnable {

    private final RequestContext context;

    public AbstractAction(final RequestContext context) {
        this.context = context;
    }

    public RequestContext getRequestContext() {
        return context;
    }

}
