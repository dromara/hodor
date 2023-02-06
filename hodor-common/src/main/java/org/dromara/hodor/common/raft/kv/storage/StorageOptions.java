package org.dromara.hodor.common.raft.kv.storage;

import com.google.common.collect.Lists;
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

    @Builder.Default
    private List<String> storageTables = Lists.newArrayList("default");

    @Builder.Default
    private DBProfile storageProfile = DBProfile.DISK;

    // The raft log used fsync by default, and the correctness of
    // state-machine data with rheakv depends on the raft log + snapshot,
    // so we do not need to fsync.
    @Builder.Default
    private boolean sync = false;

    // For the same reason(See the comment of ‘sync’ field), we also
    // don't need WAL, which can improve performance.
    //
    // If `sync` is true, `disableWAL` must be set false
    @Builder.Default
    private boolean disableWAL = true;

}
