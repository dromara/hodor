package org.dromara.hodor.common.raft;

import org.apache.ratis.client.RaftClient;
import org.apache.ratis.conf.Parameters;
import org.apache.ratis.conf.RaftProperties;
import org.apache.ratis.grpc.GrpcFactory;
import org.apache.ratis.protocol.ClientId;
import org.apache.ratis.protocol.RaftGroup;
import org.apache.ratis.retry.RetryPolicies;
import org.apache.ratis.util.TimeDuration;

/**
 * HodorRaftClient
 *
 * @author tomgs
 * @version 2022/3/20 1.0
 */
public class HodorRaftClient {

    /**
     * build the RaftClient instance which is used to communicate to
     * Counter cluster
     *
     * @param raftGroup raft group
     * @return the created client of Counter cluster
     */
    public static RaftClient buildClient(RaftGroup raftGroup) {
        RaftProperties raftProperties = new RaftProperties();
        RaftClient.Builder builder = RaftClient.newBuilder()
            .setProperties(raftProperties)
            .setRaftGroup(raftGroup)
            .setRetryPolicy(RetryPolicies.retryUpToMaximumCountWithFixedSleep(3, TimeDuration.ONE_SECOND))
            .setClientRpc(new GrpcFactory(new Parameters())
                .newRaftClientRpc(ClientId.randomId(), raftProperties));
        return builder.build();
    }

}
