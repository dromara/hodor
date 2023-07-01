package org.dromara.hodor.register.embedded.client;

import org.apache.ratis.client.ClientFactory;
import org.apache.ratis.conf.RaftProperties;
import org.apache.ratis.protocol.ClientId;
import org.apache.ratis.rpc.RpcFactory;

/**
 * WatchClientFactory
 *
 * @author tomgs
 * @version 1.0
 */
public interface WatchClientFactory extends RpcFactory {

    static WatchClientFactory cast(RpcFactory rpcFactory) {
        if (rpcFactory instanceof WatchClientFactory) {
            return (WatchClientFactory)rpcFactory;
        }
        throw new ClassCastException("Cannot cast " + rpcFactory.getClass()
                + " to " + WatchClientFactory.class
                + "; rpc type is " + rpcFactory.getRpcType());
    }

    /** Create a {@link WatchClientRpc}. */
    WatchClientRpc newWatchClientRpc(ClientId clientId, RaftProperties properties);

}
