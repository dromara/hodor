package org.dromara.hodor.server.service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.event.Event;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.common.utils.GsonUtils;
import org.dromara.hodor.common.utils.HostUtils;
import org.dromara.hodor.common.utils.ThreadUtils;
import org.dromara.hodor.model.actuator.ActuatorInfo;
import org.dromara.hodor.model.scheduler.HodorMetadata;
import org.dromara.hodor.register.api.ConnectionStateChangeListener;
import org.dromara.hodor.register.api.DataChangeListener;
import org.dromara.hodor.register.api.RegistryCenter;
import org.dromara.hodor.register.api.RegistryConfig;
import org.dromara.hodor.register.api.node.ActuatorNode;
import org.dromara.hodor.register.api.node.SchedulerNode;
import org.dromara.hodor.server.common.HodorLifecycle;
import org.dromara.hodor.server.config.HodorServerProperties;
import org.dromara.hodor.server.listener.JobEventDispatchListener;
import org.dromara.hodor.server.listener.RegistryConnectionStateListener;
import org.springframework.stereotype.Service;

/**
 * register service
 *
 * @author tomgs
 * @version 2020/6/29 1.0
 */
@Slf4j
@Service
public class RegistryService implements HodorLifecycle {

    private final RegistryCenter registryCenter;

    private final HodorServerProperties properties;

    private final GsonUtils gsonUtils;

    private final String serverEndpoint;

    public RegistryService(final HodorServerProperties properties) {
        this.properties = properties;
        this.registryCenter = ExtensionLoader.getExtensionLoader(RegistryCenter.class).getDefaultJoin();
        this.gsonUtils = GsonUtils.getInstance();
        this.serverEndpoint = HostUtils.getLocalIp() + ":" + properties.getNetServerPort();
    }

    @Override
    public void start() {
        RegistryConfig config = RegistryConfig.builder().servers(properties.getRegistryServers()).namespace(properties.getRegistryNamespace()).build();
        registryCenter.init(config);
        this.registryConnectionStateListener(new RegistryConnectionStateListener(this));
        initNode();
    }

    @Override
    public void stop() {
        registryCenter.close();
    }

    public RegistryCenter getRegistryCenter() {
        return registryCenter;
    }

    public void initNode() {
        // init path
        // init data
        createServerNode(SchedulerNode.getServerNodePath(getServerEndpoint()), getServerEndpoint());
    }

    public void waitServerStarted() {
        Integer currRunningNodeCount = this.getRunningNodeCount();
        while (currRunningNodeCount < this.getLeastNodeCount()) {
            log.warn("waiting for the node to join the cluster ...");
            ThreadUtils.sleep(TimeUnit.MILLISECONDS, 1000);
            currRunningNodeCount = this.getRunningNodeCount();
        }
    }

    public Integer getRunningNodeCount() {
        return getRunningNodes().size();
    }

    public List<String> getRunningNodes() {
        return registryCenter.getChildren(SchedulerNode.NODES_PATH);
    }

    public void createServerNode(String serverNodePath, String serverId) {
        registryCenter.createEphemeral(serverNodePath, serverId);
    }

    public HodorMetadata getMetadata() {
        String metadataStr = registryCenter.get(SchedulerNode.METADATA_PATH);
        return gsonUtils.fromJson(metadataStr, HodorMetadata.class);
    }

    public void createMetadata(HodorMetadata metadata) {
        registryCenter.createPersistent(SchedulerNode.METADATA_PATH, gsonUtils.toJson(metadata));
    }

    public void registryConnectionStateListener(ConnectionStateChangeListener connectionStateChangeListener) {
        registryCenter.addConnectionStateListener(connectionStateChangeListener);
    }

    public void registryMetadataListener(DataChangeListener listener) {
        registryListener(SchedulerNode.METADATA_PATH, listener);
    }

    public void registrySchedulerNodeListener(DataChangeListener listener) {
        registryListener(SchedulerNode.NODES_PATH, listener);
    }

    public void registryElectLeaderListener(DataChangeListener listener) {
        registryListener(SchedulerNode.MASTER_ACTIVE_PATH, listener);
    }

    public void registryActuatorNodeListener(DataChangeListener listener) {
        registryListener(ActuatorNode.ACTUATOR_NODES_PATH, listener);
        registryListener(ActuatorNode.ACTUATOR_GROUPS_PATH, listener);
    }

    public void registryListener(String path, DataChangeListener listener) {
        registryCenter.addDataCacheListener(path, listener);
    }

    public String getServerEndpoint() {
        return serverEndpoint;
    }

    public Integer getLeastNodeCount() {
        int clusterNodes = properties.getClusterNodes();
        return clusterNodes <= 0 ? Integer.parseInt(System.getProperty("clusters", "1")) : clusterNodes;
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

    public void registryJobEventListener(JobEventDispatchListener jobEventDispatchListener) {
        registryListener(SchedulerNode.JOB_EVENT, jobEventDispatchListener);
    }

    public <T> void createJobEvent(Event<T> event) {
        registryCenter.createPersistentSequential(String.valueOf(event.getEventType()), gsonUtils.toJson(event.getValue()));
    }

}
