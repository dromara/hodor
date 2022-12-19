package org.dromara.hodor.common.raft.watch.client;

import org.dromara.hodor.common.proto.DataChangeEvent;

/**
 * WatchCallback
 *
 * @author tomgs
 * @since 1.0
 */
public interface WatchCallback {

    void callback(DataChangeEvent event);

}
