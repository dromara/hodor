package org.dromara.hodor.scheduler.api;

import org.dromara.hodor.core.Host;

/**
 * register manager
 *
 * @author tomgs
 * @since 2020/9/23
 */
public final class RegisterManager {

    private static final RegisterManager INSTANCE = new RegisterManager();

    private RegisterManager() {
    }

    public static RegisterManager getInstance() {
        return INSTANCE;
    }


    public Host selectSuitableHost(String jobGroup) {
        return null;
    }

}
