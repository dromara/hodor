package org.dromara.hodor.common.raft.kv.core;

/**
 * CacheServer
 *
 * @author tomgs
 * @since 2022/3/22
 */
public interface CacheServer {

    void start() throws Exception;

    void close() throws Exception;

}
