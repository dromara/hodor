package org.dromara.hodor.register.api;

import lombok.Getter;

/**
 * 数据变化事件
 *
 * @author tomgs
 * @since 1.0
 */
public class DataChangeEvent {

    @Getter
    private final DataChangeEvent.Type type;

    @Getter
    private final String path;

    @Getter
    private final byte[] data;

    public DataChangeEvent(String typeName, String path, byte[] data) {
        this.type = Type.valueOf(typeName);
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
