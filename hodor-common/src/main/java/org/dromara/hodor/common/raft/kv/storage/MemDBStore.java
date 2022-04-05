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
        throw new UnsupportedOperationException("MemDBStore");
    }

    @Override
    public void put(byte[] key, byte[] value) {
        throw new UnsupportedOperationException("MemDBStore");
    }

    @Override
    public void delete(byte[] key) {
        throw new UnsupportedOperationException("MemDBStore");
    }

    @Override
    public void close() {

    }

}
