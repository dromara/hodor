package org.dromara.hodor.server.service;

import java.util.List;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.common.utils.GsonUtils;
import org.dromara.hodor.common.utils.HostUtils;
import org.dromara.hodor.model.actuator.ActuatorInfo;
import org.dromara.hodor.model.scheduler.CopySet;
import org.dromara.hodor.model.scheduler.HodorMetadata;
import org.dromara.hodor.register.api.DataChangeListener;
import org.dromara.hodor.register.api.RegistryCenter;
import org.dromara.hodor.register.api.RegistryConfig;
import org.dromara.hodor.register.api.node.ActuatorNode;
import org.dromara.hodor.register.api.node.SchedulerNode;
import org.dromara.hodor.server.component.LifecycleComponent;
import org.dromara.hodor.server.config.HodorServerProperties;
import org.springframework.stereotype.Service;

/**
 * register service
 *
 * @author tomgs
 * @version 2020/6/29 1.0
 */
@Service
public class RegisterService implements LifecycleComponent {

    private final RegistryCenter registryCenter;

    private final HodorServerProperties properties;

    private final GsonUtils gsonUtils;

    private final String serverId;

    public RegisterService(final HodorServerProperties properties) {
        this.properties = properties;
        this.registryCenter = ExtensionLoader.getExtensionLoader(RegistryCenter.class).getDefaultJoin();
        this.gsonUtils = GsonUtils.getInstance();
        this.serverId = HostUtils.getLocalIp() + ":" + properties.getNetServerPort();
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
        //registryCenter.makeDirs(ServerNode.METADATA_PATH);
        //registryCenter.makeDirs(ServerNode.NODES_PATH);
        //registryCenter.makeDirs(ServerNode.COPY_SETS_PATH);
        //registryCenter.makeDirs(ServerNode.MASTER_PATH);
        //registryCenter.makeDirs(ServerNode.WORK_PATH);

        // init data
        createServerNode(SchedulerNode.getServerNodePath(getServerId()), getServerId());

    }

    public Integer getRunningNodeCount() {
        return getRunningNodes().size();
    }

    public List<String> getRunningNodes() {
        return registryCenter.getChildren(SchedulerNode.NODES_PATH);
    }

    @Deprecated
    public void createCopySet(CopySet copySet) {
        String serversPath = registryCenter.makePath(SchedulerNode.COPY_SETS_PATH, String.valueOf(copySet.getId()), "servers");
        for (String server : copySet.getServers()) {
            registryCenter.createEphemeral(serversPath, server);
        }
    }

    public void createServerNode(String serverNodePath, String serverId) {
        registryCenter.createEphemeral(serverNodePath, serverId);
    }

    public void createMetadata(HodorMetadata metadata) {
        registryCenter.createPersistent(SchedulerNode.METADATA_PATH, gsonUtils.toJson(metadata));
    }

    public void registryMetadataListener(DataChangeListener listener) {
        registryListener(SchedulerNode.METADATA_PATH, listener);
    }

    public void registrySchedulerNodeListener(DataChangeListener listener) {
        registryListener(SchedulerNode.NODES_PATH, listener);
    }

    public void registryElectLeaderListener(DataChangeListener listener) {
        registryListener(SchedulerNode.ACTIVE_PATH, listener);
    }

    public void registryActuatorNodeListener(DataChangeListener listener) {
        registryListener(ActuatorNode.ACTUATOR_NODES_PATH, listener);
        registryListener(ActuatorNode.ACTUATOR_GROUPS_PATH, listener);
    }

    public void registryListener(String path, DataChangeListener listener) {
        registryCenter.addDataCacheListener(path, listener);
    }

    public String getServerId() {
        return serverId;
    }

    public Integer getLeastNodeCount() {
        //return properties.getClusterNodes();
        return Integer.parseInt(System.getProperty("clusters", "1"));
    }

    public void createActuator(final ActuatorInfo actuatorInfo) {
        String endpoint = actuatorInfo.getNodeInfo().getEndpoint();
        registryCenter.createPersistent(ActuatorNode.createNodePath(endpoint), gsonUtils.toJson(actuatorInfo.getNodeInfo()));
        actuatorInfo.getGroupNames().forEach(groupName ->
            registryCenter.createPersistent(ActuatorNode.createGroupPath(groupName, endpoint), String.valueOf(actuatorInfo.getLastHeartbeat())));
    }

    public void removeActuator(final ActuatorInfo actuatorInfo) {
        String endpoint = actuatorInfo.getNodeInfo().getEndpoint();
        registryCenter.remove(ActuatorNode.createNodePath(endpoint));
        actuatorInfo.getGroupNames().forEach(groupName ->
            registryCenter.remove(ActuatorNode.createGroupPath(groupName, endpoint)));
    }

}
