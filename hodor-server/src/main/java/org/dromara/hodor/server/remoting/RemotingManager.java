package org.dromara.hodor.server.remoting;

import org.dromara.hodor.common.Host;
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

    public void sendRequest(final Host host, final RemotingRequest<? extends RequestBody> request) {

    }

}
