package org.dromara.hodor.common.raft.kv.core;

import cn.hutool.core.lang.Assert;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.ratis.protocol.RaftPeer;
import org.apache.ratis.util.NetUtils;
import org.dromara.hodor.common.HodorLifecycle;
import org.dromara.hodor.common.raft.HodorRaftGroup;
import org.dromara.hodor.common.raft.HodorRaftServer;
import org.dromara.hodor.common.raft.HodorRaftStateMachine;
import org.dromara.hodor.common.raft.RaftGroupManager;
import org.dromara.hodor.common.raft.RaftOptions;
import org.dromara.hodor.common.raft.kv.storage.StorageEngine;
import org.dromara.hodor.common.raft.kv.storage.StorageOptions;

/**
 * HodorKVServer
 *
 * @author tomgs
 * @since 2022/3/22
 */
@Slf4j
public class HodorKVServer implements HodorLifecycle {

    private final HodorRaftServer hodorRaftServer;

    public HodorKVServer(final HodorKVOptions hodorKVOptions) throws IOException {
        Assert.notNull(hodorKVOptions, "hodorRaftServer must be not null.");
        final StorageOptions storageOptions = hodorKVOptions.getStorageOptions();
        final RaftOptions raftOptions = hodorKVOptions.getRaftOptions();
        final Optional<Map<HodorRaftGroup, HodorRaftStateMachine>> stateMachineMapOptional = Optional.ofNullable(raftOptions.getStateMachineMap());
        final Map<HodorRaftGroup, HodorRaftStateMachine> stateMachineMap = stateMachineMapOptional.orElseGet(HashMap::new);

        final RaftPeer raftPeer = RaftGroupManager.getInstance().buildRaftPeer(NetUtils.createSocketAddr(raftOptions.getEndpoint()), 0);
        storageOptions.setStoragePath(new File(storageOptions.getStoragePath(),
            Paths.get(hodorKVOptions.getClusterName(),
                raftPeer.getId().toString(),
                storageOptions.getStorageType().getName()).toString()));
        StorageEngine storageEngine = new StorageEngine(storageOptions);
        storageEngine.init();

        HodorRaftGroup hodorRaftGroup = HodorRaftGroup.builder()
            .raftGroupName(KVConstant.HODOR_KV_GROUP_NAME)
            .addresses(raftOptions.getServerAddresses())
            .build();
        stateMachineMap.putIfAbsent(hodorRaftGroup, new HodorKVStateMachine(storageEngine));

        raftOptions.setStorageDir(new File(raftOptions.getStorageDir(), hodorKVOptions.getClusterName()));
        raftOptions.setStateMachineMap(stateMachineMap);

        this.hodorRaftServer = new HodorRaftServer(raftOptions);
    }

    @Override
    public void start() throws Exception {
        hodorRaftServer.start();
    }

    @Override
    public void stop() throws Exception {
        hodorRaftServer.stop();
    }

}
