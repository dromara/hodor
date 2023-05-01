package org.dromara.hodor.common.raft.kv.storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.dromara.hodor.common.raft.kv.exception.StorageDBException;
import org.dromara.hodor.common.utils.BytesUtil;
import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.ColumnFamilyOptions;
import org.rocksdb.DBOptions;
import org.rocksdb.FlushOptions;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.WriteOptions;

import static org.dromara.hodor.common.raft.kv.exception.ExceptionHelper.toIOException;

/**
 * RocksDBStore
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class RocksDBStore implements DBStore {

    static {
        RocksDB.loadLibrary();
    }

    private final DBOptions dbOptions;

    private final WriteOptions writeOptions;

    private final Map<String, ColumnFamilyHandle> handleTable;

    private final ColumnFamilyOptions cfOptions;

    private final List<ColumnFamilyDescriptor> cfDescriptors;

    private final List<ColumnFamilyHandle> columnFamilyHandles;

    private final File dbPath;

    private RocksDB rocksDB;

    public RocksDBStore(final StorageOptions storageOptions) {
        this.handleTable = new HashMap<>();
        this.cfDescriptors = new ArrayList<>();
        this.columnFamilyHandles = new ArrayList<>();
        this.writeOptions = createWriteOptions(storageOptions);
        this.dbOptions = createDBOptions(storageOptions);
        this.cfOptions = createColumnFamilyOptions(storageOptions);
        this.dbPath = storageOptions.getStoragePath();
    }

    private ColumnFamilyOptions createColumnFamilyOptions(StorageOptions storageOptions) {
        return storageOptions.getStorageProfile().getColumnFamilyOptions();
    }

    private static WriteOptions createWriteOptions(StorageOptions opts) {
        WriteOptions writeOptions = new WriteOptions();
        writeOptions.setSync(opts.isSync());
        // If `sync` is true, `disableWAL` must be set false.
        writeOptions.setDisableWAL(!opts.isSync() && opts.isDisableWAL());
        return writeOptions;
    }

    // Creates the rocksDB options, the user must take care
    // to close it after closing db.
    private static DBOptions createDBOptions(StorageOptions opts) {
        return opts.getStorageProfile().getDBOptions();
    }

    @Override
    public void init() {
        try {
            createDbPath(this.dbPath);
            // default column family
            this.cfDescriptors.add(DBColumnFamily.Default.getColumnFamilyDescriptor(cfOptions));
            this.cfDescriptors.add(DBColumnFamily.HodorRaft.getColumnFamilyDescriptor(cfOptions));
            this.cfDescriptors.add(DBColumnFamily.HodorWatch.getColumnFamilyDescriptor(cfOptions));
            this.cfDescriptors.add(DBColumnFamily.HodorWrite.getColumnFamilyDescriptor(cfOptions));
            this.cfDescriptors.add(DBColumnFamily.HodorLock.getColumnFamilyDescriptor(cfOptions));
            this.cfDescriptors.add(DBColumnFamily.HodorSeq.getColumnFamilyDescriptor(cfOptions));
            // open db
            this.rocksDB = RocksDB.open(this.dbOptions, dbPath.getPath(), this.cfDescriptors, this.columnFamilyHandles);

            for (ColumnFamilyHandle columnFamilyHandle : columnFamilyHandles) {
                handleTable.put(BytesUtil.readUtf8(columnFamilyHandle.getName()), columnFamilyHandle);
            }
        } catch (RocksDBException e) {
            throw new StorageDBException("Init DB exception: " + e.getMessage(), e);
        }

        if (log.isDebugEnabled()) {
            log.debug("RocksDB successfully opened.");
            log.debug("[Option] dbPath = {}", dbPath.getAbsolutePath());
            log.debug("[Option] createIfMissing = {}", dbOptions.createIfMissing());
            log.debug("[Option] maxOpenFiles= {}", dbOptions.maxOpenFiles());
        }
    }

    @Override
    public Table<byte[], byte[]> getTable(String name) throws IOException {
        ColumnFamilyHandle handle = handleTable.get(name);
        if (handle == null) {
            throw new IOException("No such table in this DB. TableName : " + name);
        }
        return new RocksDBTable(this.rocksDB, handle, this.writeOptions);
    }

    @Override
    public ArrayList<Table<byte[], byte[]>> listTables() {
        ArrayList<Table<byte[], byte[]>> returnList = new ArrayList<>();
        for (ColumnFamilyHandle handle : handleTable.values()) {
            returnList.add(new RocksDBTable(rocksDB, handle, writeOptions));
        }
        return returnList;
    }

    @Override
    public void flushDB() throws IOException {
        try (FlushOptions flushOptions = new FlushOptions()) {
            flushOptions.setWaitForFlush(true);
            rocksDB.flush(flushOptions);
        } catch (RocksDBException e) {
            throw toIOException("Unable to Flush RocksDB data", e);
        }
    }

    @Override
    public void flushLog(boolean sync) throws IOException {
        if (rocksDB != null) {
            try {
                // for RocksDB it is sufficient to flush the WAL as entire db can
                // be reconstructed using it.
                rocksDB.flushWal(sync);
            } catch (RocksDBException e) {
                throw toIOException("Failed to flush db", e);
            }
        }
    }

    @Override
    public void compactDB() throws IOException {
        if (rocksDB != null) {
            try {
                rocksDB.compactRange();
            } catch (RocksDBException e) {
                throw toIOException("Failed to compact db", e);
            }
        }
    }

    private static void createDbPath(File dbPath) {
        if (!dbPath.exists()) {
            try {
                FileUtils.forceMkdir(dbPath);
            } catch (IOException e) {
                throw new StorageDBException(e.getMessage(), e);
            }
        }
    }

    @Override
    public void close() {
        for (final ColumnFamilyHandle handle : handleTable.values()) {
            handle.close();
        }

        cfOptions.close();

        if (rocksDB != null) {
            rocksDB.close();
            rocksDB = null;
        }

        if (dbOptions != null) {
            dbOptions.close();
        }

        if (writeOptions != null) {
            writeOptions.close();
        }
    }

}
