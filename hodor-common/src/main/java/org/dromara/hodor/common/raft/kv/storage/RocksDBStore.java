package org.dromara.hodor.common.raft.kv.storage;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.dromara.hodor.common.raft.kv.exception.StorageDBException;
import org.dromara.hodor.common.raft.kv.protocol.KVEntry;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

/**
 * RocksDBStore
 *
 * @author tomgs
 * @since 2022/3/24
 */
@Slf4j
public class RocksDBStore implements DBStore {

    static {
        RocksDB.loadLibrary();
    }

    private final StorageOptions storageOptions;

    private RocksDB rocksDB;

    public RocksDBStore(final StorageOptions storageOptions) {
        this.storageOptions = storageOptions;
    }

    @Override
    public void init() {
        final File dbPath = storageOptions.getStoragePath();
        final Options options = new Options();
        options.setCreateIfMissing(true);
        try {
            if (!dbPath.exists()) {
                try {
                    FileUtils.forceMkdir(dbPath);
                } catch (IOException e) {
                    throw new StorageDBException(e.getMessage(), e);
                }
            }
            rocksDB = RocksDB.open(options, dbPath.getPath());
        } catch (RocksDBException e) {
            throw new StorageDBException("Init DB exception: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] get(byte[] key) {
        try {
            return rocksDB.get(key);
        } catch (RocksDBException e) {
            throw new StorageDBException("GET exception: " + e.getMessage(), e);
        }
    }

    @Override
    public void put(byte[] key, byte[] value) {
        try {
            rocksDB.put(key, value);
        } catch (RocksDBException e) {
            throw new StorageDBException("PUT exception: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(byte[] key) {
        try {
            rocksDB.delete(key);
        } catch (RocksDBException e) {
            throw new StorageDBException("PUT exception: " + e.getMessage(), e);
        }
    }

    @Override
    public Boolean containsKey(byte[] key) {
        boolean exists = false;
        if (rocksDB.keyMayExist(ByteBuffer.wrap(key))) {
            try {
                exists = rocksDB.get(key) != null;
            } catch (RocksDBException e) {
                throw new StorageDBException("ContainsKey exception: " + e.getMessage(), e);
            }
        }
        return exists;
    }

    @Override
    public List<KVEntry> scan(byte[] startKey, byte[] endKey) {
        return null;
    }

    @Override
    public void close() {
        rocksDB.close();
    }

}
