/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.hodor.common.raft.kv.storage;

import com.codahale.metrics.Timer;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.concurrent.LockUtil;
import org.dromara.hodor.common.raft.kv.exception.StorageDBException;
import org.dromara.hodor.common.raft.kv.protocol.KVEntry;
import org.dromara.hodor.common.utils.BytesUtil;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.Holder;
import org.rocksdb.ReadOptions;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.rocksdb.WriteOptions;

/**
 * RocksDB Table
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class RocksDBTable implements Table<byte[], byte[]> {

    private final RocksDB db;

    private final ColumnFamilyHandle handle;

    private final WriteOptions writeOptions;

    private final ReadWriteLock readWriteLock;

    RocksDBTable(final RocksDB db, final ColumnFamilyHandle handle,
                 final WriteOptions writeOptions) {
        Objects.requireNonNull(db, "RocksDB instance must be not null.");
        Objects.requireNonNull(handle, "ColumnFamilyHandle instance must be not null.");
        Objects.requireNonNull(writeOptions, "Write option must be not null.");
        this.db = db;
        this.handle = handle;
        this.writeOptions = writeOptions;
        this.readWriteLock = new ReentrantReadWriteLock();
    }

    @Override
    public void put(byte[] key, byte[] value) throws IOException {
        final Timer.Context timeCtx = Table.getTimeContext("PUT");
        LockUtil.lockMethod(readWriteLock.writeLock(), (k, v) -> {
            try {
                db.put(handle, writeOptions, k, v);
                return null;
            } catch (RocksDBException e) {
                throw new StorageDBException("PUT exception: " + e.getMessage(), e);
            } finally {
                timeCtx.stop();
            }
        }, key, value);
    }

    @Override
    public boolean isEmpty() throws IOException {
        try (TableIterator<byte[], ByteArrayKeyValue> keyIter = iterator()) {
            keyIter.seekToFirst();
            return !keyIter.hasNext();
        }
    }

    @Override
    public byte[] get(byte[] key) throws IOException {
        final Timer.Context timeCtx = Table.getTimeContext("GET");
        return LockUtil.lockMethod(readWriteLock.readLock(), k -> {
            try {
                return db.get(handle, k);
            } catch (RocksDBException e) {
                throw new StorageDBException("GET exception: " + e.getMessage(), e);
            } finally {
                timeCtx.stop();
            }
        }, key);
    }

    @Override
    public void delete(byte[] key) throws IOException {
        final Timer.Context timeCtx = Table.getTimeContext("GET");
        LockUtil.lockMethod(readWriteLock.readLock(), k -> {
            try {
                db.delete(handle, key);
                return null;
            } catch (RocksDBException e) {
                throw new StorageDBException("DELETE exception: " + e.getMessage(), e);
            } finally {
                timeCtx.stop();
            }
        }, key);
    }

    @Override
    public TableIterator<byte[], ByteArrayKeyValue> iterator() {
        ReadOptions readOptions = new ReadOptions();
        readOptions.setFillCache(false);
        return new RDBStoreIterator(db.newIterator(handle, readOptions), this);
    }

    @Override
    public String getName() throws IOException {
        try {
            return BytesUtil.readUtf8(this.handle.getName());
        } catch (RocksDBException e) {
            throw new StorageDBException("Get table name exception: " + e.getMessage(), e);
        }
    }

    @Override
    public long getEstimatedKeyCount() {
        try {
            return db.getLongProperty(handle, "rocksdb.estimate-num-keys");
        } catch (RocksDBException e) {
            throw new StorageDBException("Get estimated key count exception: " + e.getMessage(), e);
        }
    }

    @Override
    public List<KVEntry> scan(byte[] startKey, byte[] endKey, boolean returnValue) {
        final Timer.Context timeCtx = Table.getTimeContext("SCAN");
        final List<KVEntry> entries = Lists.newArrayList();
        final Lock readLock = this.readWriteLock.readLock();
        readLock.lock();
        try (final RocksIterator it = this.db.newIterator(handle)) {
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
    public Boolean containsKey(byte[] key) {
        final Timer.Context timeCtx = Table.getTimeContext("CONTAINS_KEY");
        final Lock readLock = this.readWriteLock.readLock();
        readLock.lock();
        try {
            boolean exists = false;
            Holder<byte[]> valueHolder = new Holder<>();
            if (db.keyMayExist(handle, key, valueHolder)) {
                exists = ((valueHolder.getValue() != null) || (db.get(handle, key) != null));
            }
            return exists;
        } catch (final Exception e) {
            throw new StorageDBException("ContainsKey exception: " + e.getMessage(), e);
        } finally {
            readLock.unlock();
            timeCtx.stop();
        }
    }

}
