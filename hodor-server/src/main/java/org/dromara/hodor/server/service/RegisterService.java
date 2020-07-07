package org.dromara.hodor.server.service;

import com.google.common.collect.Lists;
import java.util.List;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.register.api.RegistryCenter;
import org.dromara.hodor.register.api.RegistryConfig;
import org.dromara.hodor.register.api.node.ServerNode;
import org.dromara.hodor.server.component.LifecycleComponent;
import org.dromara.hodor.server.config.HodorServerProperties;
import org.springframework.stereotype.Service;

/**
 *  register service
 *
 * @author tomgs
 * @version 2020/6/29 1.0 
 */
@Service
public class RegisterService implements LifecycleComponent {

    private final RegistryCenter registryCenter;
    private final HodorServerProperties properties;

    public RegisterService(final HodorServerProperties properties) {
        this.properties = properties;
        this.registryCenter = ExtensionLoader.getExtensionLoader(RegistryCenter.class).getDefaultJoin();
    }

    @Override
    public void start() {
        RegistryConfig config = RegistryConfig.builder().servers(properties.getRegistryServers()).namespace(properties.getRegistryNamespace()).build();
        registryCenter.init(config);
        initRootNode();
    }

    @Override
    public void stop() {
        registryCenter.close();
    }

    public RegistryCenter getRegistryCenter() {
        return registryCenter;
    }

    private void initRootNode() {
        // init path
        registryCenter.makeDirs(ServerNode.METADATA_PATH);
        registryCenter.makeDirs(ServerNode.NODES_PATH);
        registryCenter.makeDirs(ServerNode.COPY_SETS_PATH);
        registryCenter.makeDirs(ServerNode.MASTER_PATH);
        registryCenter.makeDirs(ServerNode.WORK_PATH);

        // init data

    }

    public Integer getRunningNodeCount() {
        return 0;
    }

    public List<String> getRunningNodes() {
        return Lists.newArrayList();
    }

    public void createCopySet(int id, List<String> copySets) {
        String serversPath = registryCenter.makePath(ServerNode.COPY_SETS_PATH, String.valueOf(id), "servers");
        for (String copySet : copySets) {
            registryCenter.createEphemeral(serversPath, copySet);
        }
    }

}
