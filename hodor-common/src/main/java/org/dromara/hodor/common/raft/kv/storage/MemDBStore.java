package org.dromara.hodor.common.raft.kv.storage;

/**
 * MemDBStore
 *
 * @author tomgs
 * @since 2022/3/24
 */
public class MemDBStore implements DBStore {

    private final StorageOptions storageOptions;

    public MemDBStore(final StorageOptions storageOptions) {
        this.storageOptions = storageOptions;
    }

    @Override
    public void init() {

    }

    @Override
    public byte[] get(byte[] key) {
        return new byte[0];
    }

    @Override
    public void put(byte[] key, byte[] value) {

    }

    @Override
    public void delete(byte[] key) {

    }

    @Override
    public void close() {

    }

}
