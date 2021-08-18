package org.dromara.hodor.register.api;

import org.dromara.hodor.common.Host;

/**
 * register manager
 *
 * @author tomgs
 * @since 2020/9/23
 */
public final class RegistryManager {

    private static final RegistryManager INSTANCE = new RegistryManager();

    private RegistryManager() {

    }

    public static RegistryManager getInstance() {
        return INSTANCE;
    }

    public Host selectSuitableHost(final String groupName, final String jobName) {
        return null;
    }

}
