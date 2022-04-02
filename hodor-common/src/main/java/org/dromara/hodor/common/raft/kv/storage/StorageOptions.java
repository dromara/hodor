package org.dromara.hodor.common.raft.kv.storage;

import java.io.File;
import lombok.Builder;
import lombok.Data;

/**
 * StoreOptions
 *
 * @author tomgs
 * @since 2022/3/24
 */
@Data
@Builder
public class StorageOptions {

    private String clusterName;

    private StorageType storageType;

    private File storagePath;

}
