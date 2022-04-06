package org.dromara.hodor.common.raft.kv.storage;

/**
 * StorageType
 *
 * @author tomgs
 * @since 2022/3/24
 */
public enum StorageType {
    RocksDB("rocksdb"),
    Memory("memory");

    private final String name;

    StorageType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
