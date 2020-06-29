package com.dromara.hodor.server.service;

import com.dromara.hodor.server.component.LifecycleComponent;
import org.dromara.hodor.register.api.RegistryCenter;

/**
 * hodor service
 *
 * @author tomgs
 * @since 2020/6/29
 */
public class HodorService implements LifecycleComponent {

    private final RegistryCenter registryCenter;

    public HodorService(RegisterService registerService) {
        this.registryCenter = registerService.getRegistryCenter();
    }

    @Override
    public void start() {
        //select leader
        //job assign
    }

    @Override
    public void stop() {

    }

}
