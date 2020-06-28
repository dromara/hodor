package org.dromara.hodor.register.api;

import lombok.Getter;

/**
 * 数据变化事件
 *
 * @author tomgs
 * @since 2020/6/28
 */
public class DataChangeEvent {

    @Getter
    private DataChangeEvent.Type type;

    @Getter
    private String path;

    @Getter
    private byte[] data;

    public DataChangeEvent(String typeName, String path, byte[] data) {
        this.type = Type.valueOf(typeName);
        this.path = path;
        this.data = data;
    }

    public static enum Type {

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
