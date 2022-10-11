package org.dromara.hodor.common;

/**
 * HodorLifecycle
 *
 * @author tomgs
 * @since 2020/6/28
 */
public interface HodorLifecycle {

    default void init() throws Exception {}

    void start() throws Exception;

    void stop() throws Exception;

}
