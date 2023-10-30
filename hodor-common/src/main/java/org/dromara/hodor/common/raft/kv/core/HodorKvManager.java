package org.dromara.hodor.common.raft.kv.core;

import org.dromara.hodor.common.raft.kv.storage.DBColumnFamily;
import org.dromara.hodor.common.raft.kv.storage.DBStore;
import org.dromara.hodor.common.raft.kv.storage.StorageEngine;
import org.dromara.hodor.common.raft.kv.storage.Table;
import org.dromara.hodor.common.utils.BytesUtil;

import java.io.IOException;

/**
 *
 * HodorKvManager
 *
 * @author tomgs
 * @version 1.0
 */
public class HodorKvManager implements DBStoreHAManager {

    private final StorageEngine storageEngine;

    private final DBStore dbStore;

    public HodorKvManager(final StorageEngine storageEngine) {
        this.storageEngine = storageEngine;
        this.dbStore = storageEngine.getRawDBStore();
    }

    @Override
    public Table<byte[], byte[]> getTransactionInfoTable() throws IOException {
        return this.dbStore.getTable(DBColumnFamily.HodorRaft.getName());
    }

    @Override
    public void flushDB() throws IOException {
        this.dbStore.flushDB();
    }

}
