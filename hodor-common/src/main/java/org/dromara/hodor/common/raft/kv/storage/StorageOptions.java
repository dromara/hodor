package org.dromara.hodor.common.raft.kv.storage;

import java.io.File;
import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * StoreOptions
 *
 * @author tomgs
 * @since 1.0
 */
@Data
@Builder
public class StorageOptions {

    private StorageType storageType;

    private File storagePath;

    private List<String> storageTables;

    // The raft log used fsync by default, and the correctness of
    // state-machine data with rheakv depends on the raft log + snapshot,
    // so we do not need to fsync.
    private boolean sync = false;

    // For the same reason(See the comment of ‘sync’ field), we also
    // don't need WAL, which can improve performance.
    //
    // If `sync` is true, `disableWAL` must be set false
    private boolean disableWAL = true;

    private DBProfile dbProfile = DBProfile.DISK;

}
