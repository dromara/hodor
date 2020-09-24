package org.dromara.hodor.scheduler.api;

import org.dromara.hodor.core.Host;
import org.dromara.hodor.remoting.api.message.RemotingRequest;
import org.dromara.hodor.remoting.api.message.RequestBody;

/**
 * remoting manager
 *
 * @author tomgs
 * @since 2020/9/23
 */
public final class RemotingManager {

    private static final RemotingManager INSTANCE = new RemotingManager();

    private RemotingManager() {
    }

    public static RemotingManager getInstance() {
        return INSTANCE;
    }

    public void sendRequest(Host host, RemotingRequest<? extends RequestBody> request) {

    }

}
