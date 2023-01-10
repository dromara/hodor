package org.dromara.hodor.common.raft.kv.storage;

import com.codahale.metrics.Timer;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.ratis.thirdparty.com.google.common.collect.Lists;
import org.dromara.hodor.common.raft.kv.exception.StorageDBException;
import org.dromara.hodor.common.raft.kv.protocol.KVEntry;
import org.dromara.hodor.common.utils.BytesUtil;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;

/**
 * RocksDBStore
 *
 * @author tomgs
 * @since 2022/3/24
 */
@Slf4j
public class RocksDBStore implements DBStore {

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

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
        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(key.length);
        byteBuffer.put(key);
        byteBuffer.flip();
        if (rocksDB.keyMayExist(byteBuffer)) {
            try {
                exists = rocksDB.get(key) != null;
            } catch (RocksDBException e) {
                throw new StorageDBException("ContainsKey exception: " + e.getMessage(), e);
            }
        }
        return exists;
    }

    @Override
    public List<KVEntry> scan(byte[] startKey, byte[] endKey, boolean returnValue) {
        final Timer.Context timeCtx = DBStore.getTimeContext("SCAN");
        final List<KVEntry> entries = Lists.newArrayList();
        final Lock readLock = this.readWriteLock.readLock();
        readLock.lock();
        try (final RocksIterator it = this.rocksDB.newIterator()) {
            if (startKey == null) {
                it.seekToFirst();
            } else {
                it.seek(startKey);
            }
            for (; it.isValid(); it.next()) {
                final byte[] key = it.key();
                if (endKey != null && BytesUtil.getDefaultByteArrayComparator()
                    .compare(key, 0, endKey.length, endKey, 0, endKey.length) > 0) {
                    break;
                }
                entries.add(new KVEntry(key, returnValue ? it.value() : null));
            }
        } catch (final Exception e) {
            throw new StorageDBException("Scan exception: " + e.getMessage(), e);
        } finally {
            readLock.unlock();
            timeCtx.stop();
        }
        return entries;
    }

    @Override
    public void close() {
        rocksDB.close();
    }

}
