package org.dromara.hodor.common.raft.kv.storage;

/**
 * StorageEngine
 *
 * @author tomgs
 * @version 2022/3/23 1.0
 */
public class StorageEngine {

    private final StorageOptions storageOptions;

    private DBStore dbStore;

    public StorageEngine(final StorageOptions storageOptions) {
        this.storageOptions = storageOptions;
    }

    public DBStore getRawDBStore() {
        return dbStore;
    }

    public void init() {
        final StorageType storageType = storageOptions.getStorageType();
        if (storageType == StorageType.Memory) {
            dbStore = new MemDBStore(storageOptions);
        }
        if (storageType == StorageType.RocksDB) {
            dbStore = new RocksDBStore(storageOptions);
        }
        dbStore.init();
    }

    public void close() {
        dbStore.close();
    }

}
