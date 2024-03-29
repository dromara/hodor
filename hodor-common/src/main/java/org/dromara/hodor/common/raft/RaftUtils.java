/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.hodor.common.raft;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.apache.ratis.client.RaftClient;
import org.apache.ratis.client.RaftClientConfigKeys;
import org.apache.ratis.conf.RaftProperties;
import org.apache.ratis.protocol.RaftPeerId;
import org.apache.ratis.retry.ExponentialBackoffRetry;
import org.apache.ratis.util.TimeDuration;

/**
 * Helper class for raft operations.
 */
public final class RaftUtils {

    private RaftUtils() {
        // prevent instantiation
    }

    public static void assertRaftGroupAddress(String address) {
        final String[] addresses = Optional.ofNullable(address)
            .map(s -> s.split(","))
            .orElse(null);
        if (addresses == null || addresses.length == 0) {
            throw new IllegalArgumentException("Failed to get address: " + address);
        }
    }

    /**
     * Gets the raft peer id.
     *
     * @param address the address of the server
     * @return the raft peer id
     */
    public static RaftPeerId getPeerId(InetSocketAddress address) {
        return getPeerId(address.getHostString(), address.getPort());
    }

    /**
     * Gets the raft peer id.
     *
     * @param host the hostname of the server
     * @param port the port of the server
     * @return the raft peer id
     */
    public static RaftPeerId getPeerId(String host, int port) {
        return RaftPeerId.getRaftPeerId(host + "_" + port);
    }

    /**
     * Create a raft client to communicate to ratis server.
     *
     * @param hodorRaftGroup the raft group
     * @return return a raft client
     */
    public static RaftClient createClient(HodorRaftGroup hodorRaftGroup) {
        final RaftProperties properties = hodorRaftGroup.getRaftProperties();
        RaftClientConfigKeys.Rpc.setRequestTimeout(properties,
            TimeDuration.valueOf(15, TimeUnit.SECONDS));
        ExponentialBackoffRetry retryPolicy = ExponentialBackoffRetry.newBuilder()
            .setBaseSleepTime(TimeDuration.valueOf(1000, TimeUnit.MILLISECONDS))
            .setMaxAttempts(10)
            .setMaxSleepTime(
                TimeDuration.valueOf(100_000, TimeUnit.MILLISECONDS))
            .build();
        return RaftClient.newBuilder()
            .setRaftGroup(hodorRaftGroup.getRaftGroup())
            .setProperties(properties)
            .setRetryPolicy(retryPolicy)
            .build();
    }

}
