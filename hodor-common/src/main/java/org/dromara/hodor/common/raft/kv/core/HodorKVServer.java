package org.dromara.hodor.common.raft.kv.core;

import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.HodorLifecycle;
import org.dromara.hodor.common.raft.HodorRaftServer;
import org.dromara.hodor.common.raft.kv.storage.StorageEngine;

import java.io.IOException;

/**
 * HodorKVServer
 *
 * @author tomgs
 * @since 2022/3/22
 */
@Slf4j
public class HodorKVServer implements HodorLifecycle {

    private final HodorRaftServer hodorRaftServer;

    private final StorageEngine storageEngine;

    public HodorKVServer(final HodorKVOptions hodorKVOptions) throws IOException {
        Assert.notNull(hodorKVOptions, "hodorRaftServer must be not null.");
        this.hodorRaftServer = new HodorRaftServer(hodorKVOptions.getRaftOptions());
        this.storageEngine = new StorageEngine(hodorKVOptions.getStorageOptions());
    }

    @Override
    public void start() throws Exception {
        hodorRaftServer.start();
    }

    @Override
    public void stop() throws Exception {
        this.hodorRaftServer.stop();
        this.storageEngine.close();
    }

}
