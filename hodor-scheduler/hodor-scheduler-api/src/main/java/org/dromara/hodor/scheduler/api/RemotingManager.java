package org.dromara.hodor.scheduler.api;

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

}
