package org.dromara.hodor.register.embedded.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.dromara.hodor.register.api.DataChangeListener;

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
