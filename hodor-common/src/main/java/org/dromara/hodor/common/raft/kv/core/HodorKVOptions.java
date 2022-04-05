package org.dromara.hodor.common.raft.kv.core;

import lombok.Builder;
import lombok.Data;
import org.dromara.hodor.common.raft.RaftOptions;
import org.dromara.hodor.common.raft.kv.storage.StorageOptions;

/**
 * @author tomgs
 * @version 2022/4/5 1.0
 */
@Data
@Builder
public class HodorKVOptions {

    private String clusterName;

    private RaftOptions raftOptions;

    private StorageOptions storageOptions;

}
