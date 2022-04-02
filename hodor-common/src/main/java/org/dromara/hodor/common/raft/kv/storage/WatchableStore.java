package org.dromara.hodor.common.raft.kv.storage;

/**
 * WatchableStore
 *
 * @author tomgs
 * @since 2022/3/28
 */
public class WatchableStore implements DBStore {

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
