package org.dromara.hodor.common.raft.watch.client;

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

    static ClientFactory cast(RpcFactory rpcFactory) {
        if (rpcFactory instanceof ClientFactory) {
            return (ClientFactory)rpcFactory;
        }
        throw new ClassCastException("Cannot cast " + rpcFactory.getClass()
                + " to " + ClientFactory.class
                + "; rpc type is " + rpcFactory.getRpcType());
    }

    /** Create a {@link WatchClientRpc}. */
    WatchClientRpc newWatchClientRpc(ClientId clientId, RaftProperties properties);

}
