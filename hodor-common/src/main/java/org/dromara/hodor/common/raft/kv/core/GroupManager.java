package org.dromara.hodor.common.raft.kv.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.apache.ratis.protocol.RaftGroup;
import org.apache.ratis.protocol.RaftGroupId;
import org.apache.ratis.protocol.RaftPeer;
import org.apache.ratis.thirdparty.com.google.protobuf.ByteString;

/**
 * GroupManager
 *
 * @author tomgs
 * @since 2022/3/24
 */
public class GroupManager {

    private static final GroupManager INSTANCE = new GroupManager();

    private static final UUID CLUSTER_GROUP_ID = UUID.fromString("02511d47-d67c-49a3-9011-abb3109a44c1");

    // 这个会自动转换为UUID，但是需要确保字符串为16位
    public static final RaftGroupId RATIS_KV_GROUP_ID = RaftGroupId.valueOf(ByteString.copyFromUtf8("RatisKVGroup0000"));

    private RaftGroup raftGroup;

    private GroupManager() {
    }

    public static GroupManager getInstance() {
        return INSTANCE;
    }

    public synchronized RaftGroup getRaftGroup(RaftGroupId groupId, String[] addresses) {
        if (raftGroup != null) {
            return raftGroup;
        }
        final List<RaftPeer> peers = new ArrayList<>(addresses.length);
        for (String address : addresses) {
            peers.add(RaftPeer.newBuilder().setId(address.replace(":", "_")).setAddress(address).build());
        }
        final List<RaftPeer> raftPeers = Collections.unmodifiableList(peers);
        //raftGroup = RaftGroup.valueOf(RaftGroupId.valueOf(CLUSTER_GROUP_ID), raftPeers);
        raftGroup = RaftGroup.valueOf(groupId, raftPeers);
        return raftGroup;
    }

}
