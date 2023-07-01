package org.dromara.hodor.register.embedded.core;

import java.io.Closeable;
import java.util.Collection;
import java.util.Objects;
import org.apache.ratis.RaftConfigKeys;
import org.apache.ratis.client.ClientFactory;
import org.apache.ratis.client.RaftClient;
import org.apache.ratis.client.RaftClientRpc;
import org.apache.ratis.client.impl.ClientImplUtils;
import org.apache.ratis.conf.Parameters;
import org.apache.ratis.conf.RaftProperties;
import org.apache.ratis.protocol.ClientId;
import org.apache.ratis.protocol.RaftGroup;
import org.apache.ratis.protocol.RaftPeer;
import org.apache.ratis.protocol.RaftPeerId;
import org.apache.ratis.retry.RetryPolicies;
import org.apache.ratis.retry.RetryPolicy;
import org.apache.ratis.rpc.RpcType;
import org.dromara.hodor.register.embedded.client.WatchClientRpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WatchClient
 *
 * @author tomgs
 * @version 1.0
 */
public interface WatchRaftClient extends Closeable {

    Logger LOG = LoggerFactory.getLogger(WatchRaftClient.class);

    ClientId clientId();

    WatchClientRpc watchClientRpc();

    RaftClient raftClient();

    /** @return a {@link RaftClient.Builder}. */
    static Builder newBuilder() {
        return new Builder();
    }

    /** To build {@link RaftClient} objects. */
    class Builder {
        private ClientId clientId;
        private RaftClientRpc clientRpc;
        private WatchClientRpc watchClientRpc;
        private RaftGroup group;
        private RaftPeerId leaderId;
        private RaftPeer primaryDataStreamServer;
        private RaftProperties properties;
        private Parameters parameters;
        private RetryPolicy retryPolicy = RetryPolicies.retryForeverNoSleep();

        private Builder() {}

        /** @return a {@link RaftClient} object. */
        public WatchRaftClient build() {
            if (clientId == null) {
                clientId = ClientId.randomId();
            }
            if (properties != null) {
                if (clientRpc == null) {
                    final RpcType rpcType = RaftConfigKeys.Rpc.type(properties, LOG::debug);
                    final ClientFactory factory = ClientFactory.cast(rpcType.newFactory(parameters));
                    clientRpc = factory.newRaftClientRpc(clientId, properties);
                }
            }
            Objects.requireNonNull(group, "The 'group' field is not initialized.");
            if (primaryDataStreamServer == null) {
                final Collection<RaftPeer> peers = group.getPeers();
                if (!peers.isEmpty()) {
                    primaryDataStreamServer = peers.iterator().next();
                }
            }
            final RaftClient raftClient = ClientImplUtils.newRaftClient(clientId, group, leaderId, primaryDataStreamServer,
                    Objects.requireNonNull(clientRpc, "The 'clientRpc' field is not initialized."), retryPolicy,
                    properties, parameters);
            return new WatchRaftRaftClientImpl(clientId, raftClient, watchClientRpc, group, retryPolicy,
                    properties, parameters);
        }

        /** Set {@link RaftClient} ID. */
        public Builder setClientId(ClientId clientId) {
            this.clientId = clientId;
            return this;
        }

        /** Set servers. */
        public Builder setRaftGroup(RaftGroup grp) {
            this.group = grp;
            return this;
        }

        /** Set leader ID. */
        public Builder setLeaderId(RaftPeerId leaderId) {
            this.leaderId = leaderId;
            return this;
        }

        /** Set primary server of DataStream. */
        public Builder setPrimaryDataStreamServer(RaftPeer primaryDataStreamServer) {
            this.primaryDataStreamServer = primaryDataStreamServer;
            return this;
        }

        /** Set {@link RaftClientRpc}. */
        public Builder setClientRpc(RaftClientRpc clientRpc) {
            this.clientRpc = clientRpc;
            return this;
        }

        /** Set {@link RaftProperties}. */
        public Builder setProperties(RaftProperties properties) {
            this.properties = properties;
            return this;
        }

        /** Set {@link Parameters}. */
        public Builder setParameters(Parameters parameters) {
            this.parameters = parameters;
            return this;
        }

        /** Set {@link RetryPolicy}. */
        public Builder setRetryPolicy(RetryPolicy retryPolicy) {
            this.retryPolicy = retryPolicy;
            return this;
        }

        public Builder setWatchClientRpc(WatchClientRpc watchClientRpc) {
            this.watchClientRpc = watchClientRpc;
            return this;
        }
    }

}
