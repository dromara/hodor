package org.dromara.hodor.common.raft.watch.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WatchClientManager
 *
 * @author tomgs
 * @since 1.0
 */
public class WatchClientManager {

    private static final Map<String, DataChangeListener> listenerMap = new ConcurrentHashMap<>();

    public void watch() {

    }

    public void unwatch() {

    }

}
