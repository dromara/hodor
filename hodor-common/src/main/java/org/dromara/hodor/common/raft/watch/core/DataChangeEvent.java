package org.dromara.hodor.common.raft.watch.core;

import lombok.Getter;

/**
 * 数据变化事件
 *
 * @author tomgs
 * @since 2020/6/28
 */
public class DataChangeEvent {

    @Getter
    private final Type type;

    @Getter
    private final String path;

    @Getter
    private final byte[] data;

    public DataChangeEvent(String typeName, String path, byte[] data) {
        this.type = Type.valueOf(typeName);
        this.path = path;
        this.data = data;
    }

    public DataChangeEvent(Type type, String path, byte[] data) {
        this.type = type;
        this.path = path;
        this.data = data;
    }

    public enum Type {
        NODE_ADDED,
        NODE_UPDATED,
        NODE_REMOVED,
        CONNECTION_SUSPENDED,
        CONNECTION_RECONNECTED,
        CONNECTION_LOST,
        INITIALIZED;

        Type() {
        }

    }

}
