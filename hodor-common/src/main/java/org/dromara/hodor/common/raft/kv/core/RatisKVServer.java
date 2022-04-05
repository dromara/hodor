package org.dromara.hodor.common.raft.kv.core;

import org.apache.ratis.conf.RaftProperties;
import org.apache.ratis.grpc.GrpcConfigKeys;
import org.apache.ratis.protocol.RaftGroupId;
import org.apache.ratis.protocol.RaftPeer;
import org.apache.ratis.server.RaftServer;
import org.apache.ratis.server.RaftServerConfigKeys;
import org.apache.ratis.thirdparty.com.google.protobuf.ByteString;
import org.apache.ratis.util.NetUtils;
import org.dromara.hodor.common.HodorLifecycle;
import org.dromara.hodor.common.raft.RaftGroupManager;
import org.dromara.hodor.common.raft.kv.storage.StorageEngine;
import org.dromara.hodor.common.raft.kv.storage.StorageOptions;
import org.dromara.hodor.common.raft.kv.storage.StorageType;
import org.dromara.hodor.common.storage.cache.CacheSourceConfig;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

/**
 * RatisKVServer
 *
 * @author tomgs
 * @since 2022/3/22
 */
public class RatisKVServer implements HodorLifecycle {

    public static final RaftGroupId RATIS_KV_GROUP_ID = RaftGroupId.valueOf(ByteString.copyFromUtf8("RatisKVGroup0000"));

    private final RaftServer server;

    private StorageEngine storageEngine;

    public RatisKVServer(final CacheSourceConfig sourceConfig) throws IOException {
        // create peers
        final String[] addresses = Optional.ofNullable(sourceConfig.getServerAddresses())
                .map(s -> s.split(","))
                .orElse(null);
        if (addresses == null || addresses.length == 0) {
            throw new IllegalArgumentException("Failed to get " + sourceConfig.getServerAddresses() + " from " + sourceConfig);
        }

        final RaftPeer currentPeer = RaftPeer.newBuilder()
                .setId(sourceConfig.getEndpoint().replace(":", "_"))
                .setAddress(sourceConfig.getEndpoint())
                .build();

        final File storageDir = new File(sourceConfig.getDataPath() + "/" + currentPeer.getId());
        final File dbDir = new File(sourceConfig.getDataPath()  + "/" + currentPeer.getId());

        // init storeEngine
        StorageOptions storageOptions = StorageOptions.builder()
                //.clusterName(sourceConfig.getClusterName())
                .storageType(StorageType.RocksDB)
                .storagePath(dbDir)
                .build();
        storageEngine = new StorageEngine(storageOptions);
        storageEngine.init();

        //create a property object
        RaftProperties properties = new RaftProperties();
        //set the storage directory (different for each peer) in RaftProperty object
        RaftServerConfigKeys.setStorageDir(properties, Collections.singletonList(storageDir));
        //set the port which server listen to in RaftProperty object
        final int port = NetUtils.createSocketAddr(sourceConfig.getEndpoint()).getPort();
        GrpcConfigKeys.Server.setPort(properties, port);
        //create the counter state machine which hold the counter value
        RatisKVServerStateMachine serverStateMachine = new RatisKVServerStateMachine(storageEngine);
        //create and start the Raft server
        this.server = RaftServer.newBuilder()
                .setGroup(RaftGroupManager.getInstance().createRaftGroup("RatisKVGroup0000", addresses))
                .setProperties(properties)
                .setServerId(currentPeer.getId())
                .setStateMachine(serverStateMachine)
                .build();
    }

    @Override
    public void start() throws IOException {
        server.start();
    }

    @Override
    public void stop() throws IOException {
        storageEngine.close();
        server.close();
    }

}
