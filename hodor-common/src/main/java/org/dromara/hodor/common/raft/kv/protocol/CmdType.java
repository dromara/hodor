package org.dromara.hodor.common.raft.kv.protocol;

/**
 * CmdType
 *
 * @author tomgs
 * @since 1.0
 */
public enum CmdType {
    GET,
    PUT,
    DELETE,
    CONTAINS_KEY,
    SCAN
}
