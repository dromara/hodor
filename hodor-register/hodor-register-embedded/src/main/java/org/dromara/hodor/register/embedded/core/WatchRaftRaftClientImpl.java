package org.dromara.hodor.register.embedded.core;

import java.io.IOException;
import org.apache.ratis.client.RaftClient;
import org.apache.ratis.conf.Parameters;
import org.apache.ratis.conf.RaftProperties;
import org.apache.ratis.protocol.RaftGroup;
import org.apache.ratis.retry.RetryPolicy;
import org.dromara.hodor.register.embedded.client.WatchClientRpc;

/**
 * WatchRaftClient
 *
 * @author tomgs
 * @since 1.0
 */
public class WatchRaftRaftClientImpl implements WatchRaftClient {

    private final RaftClient raftClient;
    private final WatchClientRpc watchClientRpc;
    private final RetryPolicy retryPolicy;
    private final RaftProperties properties;
    private final Parameters parameters;

    public WatchRaftRaftClientImpl(final RaftClient raftClient, final WatchClientRpc watchClientRpc,
                                   final RaftGroup group, final RetryPolicy retryPolicy,
                                   final RaftProperties properties, final Parameters parameters) {
        this.raftClient = raftClient;
        this.watchClientRpc = watchClientRpc;
        this.retryPolicy = retryPolicy;
        this.properties = properties;
        this.parameters = parameters;

        watchClientRpc.addRaftPeers(group.getPeers());
        watchClientRpc.startHandleWatchStreamResponse();
    }

    @Override
    public WatchClientRpc watchClientRpc() {
        return watchClientRpc;
    }

    @Override
    public RaftClient raftClient() {
        return raftClient;
    }

    @Override
    public void close() throws IOException {
        watchClientRpc.close();
        raftClient.close();
    }
}
