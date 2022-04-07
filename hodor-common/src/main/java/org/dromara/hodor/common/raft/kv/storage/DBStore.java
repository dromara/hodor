package org.dromara.hodor.common.raft.kv.storage;

import org.dromara.hodor.common.raft.kv.core.KVOperate;

/**
 * DBStore
 *
 * @author tomgs
 * @since 2022/3/24
 */
public interface DBStore extends KVOperate {

    void init();

}
