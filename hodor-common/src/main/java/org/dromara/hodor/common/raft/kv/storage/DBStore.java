package org.dromara.hodor.common.raft.kv.storage;

/**
 * DBStore
 *
 * @author tomgs
 * @since 2022/3/24
 */
public interface DBStore {

    void init();

    byte[] get(byte[] key);

    void put(byte[] key, byte[] value);

    void delete(byte[] key);

    void close();

}
