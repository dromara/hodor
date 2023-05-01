package org.dromara.hodor.common.raft.kv.storage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * DBStore
 *
 * @author tomgs
 * @since 1.0
 */
public interface DBStore extends AutoCloseable {

    void init() throws Exception;

    /**
     * Gets an existing TableStore.
     *
     * @param name - Name of the TableStore to get
     * @return - TableStore.
     * @throws IOException on Failure
     */
    Table<byte[], byte[]> getTable(String name) throws IOException;

    /**
     * Lists the Known list of Tables in a DB.
     *
     * @return List of Tables, in case of Rocks DB and LevelDB we will return at
     * least one entry called DEFAULT.
     * @throws IOException on Failure
     */
    ArrayList<Table<byte[], byte[]>> listTables() throws IOException;

    /**
     * Flush the DB buffer onto persistent storage.
     * @throws IOException on Failure
     */
    void flushDB() throws IOException;

    /**
     * Flush the outstanding I/O operations of the DB.
     * @param sync if true will sync the outstanding I/Os to the disk.
     */
    void flushLog(boolean sync) throws IOException;

    /**
     * Compact the entire database.
     *
     * @throws IOException on Failure
     */
    void compactDB() throws IOException;

}
