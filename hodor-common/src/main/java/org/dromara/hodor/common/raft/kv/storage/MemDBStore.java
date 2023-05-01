package org.dromara.hodor.common.raft.kv.storage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * MemDBStore
 *
 * @author tomgs
 * @since 1.0
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
    public Table<byte[], byte[]> getTable(String name) throws IOException {
        return null;
    }

    @Override
    public ArrayList<Table<byte[], byte[]>> listTables() throws IOException {
        return null;
    }

    @Override
    public void flushDB() throws IOException {

    }

    @Override
    public void flushLog(boolean sync) throws IOException {

    }

    @Override
    public void compactDB() throws IOException {

    }

    @Override
    public void close() {

    }

}
