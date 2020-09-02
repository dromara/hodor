package org.dromara.hodor.common;

import org.dromara.hodor.common.utils.SnowflakeIdWorker;

/**
 * id generator
 *
 * @author tomgs
 * @since 2020/9/2
 */
public enum IdGenerator {
    INSTANCE;

    public static IdGenerator defaultGenerator() {
        return INSTANCE;
    }

    public long nextId() {
        return SnowflakeIdWorker.nextUniqId();
    }

}
