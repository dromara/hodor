package org.dromara.hodor.common.raft;

import cn.hutool.core.lang.Assert;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.ratis.client.RaftClient;
import org.apache.ratis.conf.RaftProperties;
import org.apache.ratis.grpc.GrpcConfigKeys;
import org.apache.ratis.protocol.RaftGroup;
import org.apache.ratis.protocol.RaftGroupId;
import org.apache.ratis.protocol.RaftPeer;
import org.apache.ratis.protocol.exceptions.AlreadyExistsException;
import org.apache.ratis.server.RaftServer;
import org.apache.ratis.server.RaftServerConfigKeys;
import org.apache.ratis.server.impl.ServerImplUtils;
import org.apache.ratis.statemachine.StateMachine;
import org.apache.ratis.util.NetUtils;

/**
 * HodorRaftServer
 *
 * @author tomgs
 * @version 2022/3/20 1.0
 */
@Slf4j
public class HodorRaftServer {

    private final Map<RaftGroupId, StateMachine> registry = new ConcurrentHashMap<>();

    private final RaftOptions raftOptions;

    private final String endpoint;

    private final File storageDir;

    private final RaftProperties raftProperties;

    private RaftServer server;

    private RaftPeer currentPeer;

    public HodorRaftServer(final RaftOptions raftOptions) throws IOException {
        Assert.notNull(raftOptions, "raftOptions must not be null.");
        this.raftOptions = raftOptions;
        this.endpoint = raftOptions.getEndpoint();
        this.storageDir = raftOptions.getStorageDir();
        this.raftProperties = raftOptions.getRaftProperties();
        init();
    }

    private void init() throws IOException {
        //create a property object
        this.currentPeer = RaftGroupManager.getInstance().buildRaftPeer(NetUtils.createSocketAddr(endpoint), 0);
        //set the storage directory (different for each peer) in RaftProperty object
        RaftServerConfigKeys.setStorageDir(raftProperties, Collections.singletonList(new File(storageDir, currentPeer.getId().toString())));
        //set the port which server listen to in RaftProperty object
        int port = NetUtils.createSocketAddr(currentPeer.getAddress()).getPort();
        GrpcConfigKeys.Server.setPort(raftProperties, port);
        initRaftGroup(raftOptions.getStateMachineMap());
        //create and start the Raft server
        this.server = ServerImplUtils.newRaftServer(currentPeer.getId(), null, registry::get,
            null, raftProperties, null);
    }

    public void start() throws IOException {
        this.server.start();
        //WatchManager.getInstance().startWatchEvent("watch-event");
        createRaftGroup(raftOptions.getStateMachineMap());
    }

    public void stop() throws IOException {
        this.server.close();
    }

    public void initRaftGroup(Map<HodorRaftGroup, HodorRaftStateMachine> stateMachineMap) {
        final Map<RaftGroupId, HodorRaftStateMachine> machineMap = stateMachineMap.entrySet()
            .stream()
            .collect(Collectors.toMap(e ->
                    e.getKey().getRaftGroupId(),
                Map.Entry::getValue));
        registry.putAll(machineMap);
    }

    public void createRaftGroup(Map<HodorRaftGroup, HodorRaftStateMachine> stateMachineMap) throws IOException {
        for (Map.Entry<HodorRaftGroup, HodorRaftStateMachine> stateMachineEntry : stateMachineMap.entrySet()) {
            final RaftGroup raftGroup = stateMachineEntry.getKey().getRaftGroup();
            try (final RaftClient raftClient = RaftUtils.createClient(raftGroup)) {
                try {
                    raftClient.getGroupManagementApi(currentPeer.getId()).add(raftGroup);
                } catch (AlreadyExistsException e) {
                    // do not log
                } catch (IOException ioe) {
                    log.warn("Add group failed for {}", currentPeer, ioe);
                }
            }
        }
    }
}
