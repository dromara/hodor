/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import lombok.Builder;
import org.apache.ratis.conf.Parameters;
import org.apache.ratis.conf.RaftProperties;
import org.apache.ratis.protocol.RaftGroup;
import org.apache.ratis.protocol.RaftGroupId;

/**
 * HodorRaftGroupId
 *
 * @author tomgs
 * @since 1.0
 */
@Builder
public class HodorRaftGroup {

    private final String raftGroupName;

    private final String addresses;

    private RaftProperties raftProperties;

    private Parameters parameters;

    public RaftGroupId getRaftGroupId() {
        return getRaftGroup().getGroupId();
    }

    public RaftGroup getRaftGroup() {
        final String[] segmentAddress = addresses.split(",");
        return RaftGroupManager.getInstance()
            .createRaftGroup(raftGroupName, segmentAddress);
    }

    public RaftProperties getRaftProperties() {
        return raftProperties == null ? new RaftProperties() : raftProperties;
    }

    public Parameters getParameters() {
        return parameters == null ? new Parameters() : parameters;
    }
}
