package org.dromara.hodor.server.service;

import cn.hutool.json.JSONObject;
import com.google.common.collect.Lists;
import java.util.List;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.core.entity.CopySet;
import org.dromara.hodor.core.entity.HodorMetadata;
import org.dromara.hodor.register.api.DataChangeEvent;
import org.dromara.hodor.register.api.DataChangeListener;
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
        initNode();
    }

    @Override
    public void stop() {
        registryCenter.close();
    }

    public RegistryCenter getRegistryCenter() {
        return registryCenter;
    }

    private void initNode() {
        // init path
        registryCenter.makeDirs(ServerNode.METADATA_PATH);
        registryCenter.makeDirs(ServerNode.NODES_PATH);
        registryCenter.makeDirs(ServerNode.COPY_SETS_PATH);
        registryCenter.makeDirs(ServerNode.MASTER_PATH);
        registryCenter.makeDirs(ServerNode.WORK_PATH);

        // init data

        // add listener
        registryCenter.addDataCacheListener(ServerNode.MASTER_PATH, event -> {

        });
    }

    public Integer getRunningNodeCount() {
        return 0;
    }

    public List<String> getRunningNodes() {
        return Lists.newArrayList();
    }

    @Deprecated
    public void createCopySet(CopySet copySet) {
        String serversPath = registryCenter.makePath(ServerNode.COPY_SETS_PATH, String.valueOf(copySet.getId()), "servers");
        for (String server : copySet.getServers()) {
            registryCenter.createEphemeral(serversPath, server);
        }
    }

    public void createMetadata(HodorMetadata metadata) {
        registryCenter.createPersistent(ServerNode.METADATA_PATH, new JSONObject(metadata).toString());
        //for (CopySet copyset : metadata.getCopySets()) {
        //    createCopySet(copyset);
        //}
    }

    public void registryMetadataListener(DataChangeListener listener) {
        registryCenter.addDataCacheListener(ServerNode.METADATA_PATH, listener);
    }

    public void registryServerNodeListener(DataChangeListener listener) {
        registryCenter.addDataCacheListener(ServerNode.NODES_PATH, listener);
    }

}
