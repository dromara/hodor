package org.dromara.hodor.common;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
 * id generator
 *
 * @author tomgs
 * @since 1.0
 */
public enum IdGenerator {
    INSTANCE;

    private final Snowflake snowflake = IdUtil.getSnowflake();

    public static IdGenerator defaultGenerator() {
        return INSTANCE;
    }

    public long nextId() {
        //return SnowflakeIdWorker.nextUniqId();
        return snowflake.nextId();
    }

}
