package com.dromara.hodor.server.service;

import com.dromara.hodor.server.component.LifecycleComponent;
import com.dromara.hodor.server.config.HodorServerProperties;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.register.api.RegistryCenter;
import org.dromara.hodor.register.api.RegistryConfig;

/**
 *  register service
 *
 * @author tomgs
 * @version 2020/6/29 1.0 
 */
public class RegisterService implements LifecycleComponent {

    private RegistryCenter registryCenter;
    private HodorServerProperties properties;

    public RegisterService(final HodorServerProperties properties) {
        this.properties = properties;
        this.registryCenter = ExtensionLoader.getExtensionLoader(RegistryCenter.class).getDefaultJoin();
    }

    @Override
    public void start() {
        RegistryConfig config = RegistryConfig.builder().servers(properties.getRegistryServers()).namespace(properties.getRegistryNamespace()).build();
        registryCenter.init(config);
    }

    @Override
    public void stop() {
        registryCenter.close();
    }

    public RegistryCenter getRegistryCenter() {
        return registryCenter;
    }

}
