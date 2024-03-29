package org.dromara.hodor.server.service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.HodorLifecycle;
import org.dromara.hodor.common.utils.GsonUtils;
import org.dromara.hodor.common.utils.HostUtils;
import org.dromara.hodor.common.utils.ThreadUtils;
import org.dromara.hodor.model.actuator.ActuatorInfo;
import org.dromara.hodor.model.actuator.JobTypeInfo;
import org.dromara.hodor.model.scheduler.HodorMetadata;
import org.dromara.hodor.register.api.ConnectionStateChangeListener;
import org.dromara.hodor.register.api.DataChangeListener;
import org.dromara.hodor.register.api.RegistryCenter;
import org.dromara.hodor.register.api.RegistryConfig;
import org.dromara.hodor.register.api.node.ActuatorNode;
import org.dromara.hodor.register.api.node.SchedulerNode;
import org.dromara.hodor.server.config.HodorServerProperties;
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

    public RegistryService(final HodorServerProperties properties, final RegistryCenter registryCenter) {
        this.properties = properties;
        this.registryCenter = registryCenter;
        this.gsonUtils = GsonUtils.getInstance();
        this.serverEndpoint = HostUtils.getLocalIp() + ":" + properties.getNetServerPort();
    }

    @Override
    public void start() throws Exception {
        RegistryConfig config = RegistryConfig.builder()
            .servers(properties.getRegistryServers())
            .namespace(properties.getRegistryNamespace())
            .endpoint(properties.getRegistryEndpoint())
            .dataPath(properties.getRegistryDataPath())
            .build();
        registryCenter.init(config);
    }

    @Override
    public void stop() throws Exception {
        registryCenter.close();
    }

    public RegistryCenter getRegistryCenter() {
        return registryCenter;
    }

    public void removeServerNode() {
        registryCenter.remove(SchedulerNode.getServerNodePath(getServerEndpoint()));
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
        registryListener(ActuatorNode.ACTUATOR_CLUSTERS_PATH, listener);
        registryListener(ActuatorNode.ACTUATOR_BINDING_PATH, listener);
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
        final String clusterName = actuatorInfo.getName();
        String endpoint = actuatorInfo.getNodeInfo().getEndpoint();
        // create `cluster -> group` binding path
        actuatorInfo.getGroupNames().forEach(groupName ->
            registryCenter.createPersistent(ActuatorNode.createBindingPath(clusterName, groupName), String.valueOf(actuatorInfo.getLastHeartbeat())));
        // create clusters `clusterName -> endpoint` path
        registryCenter.createPersistent(ActuatorNode.createClusterPath(clusterName, endpoint), gsonUtils.toJson(actuatorInfo.getNodeInfo()));
    }

    public void removeActuator(final ActuatorInfo actuatorInfo) {
        final String clusterName = actuatorInfo.getName();
        String endpoint = actuatorInfo.getNodeInfo().getEndpoint();
        actuatorInfo.getGroupNames().forEach(groupName ->
            registryCenter.remove(ActuatorNode.createBindingPath(clusterName, groupName)));
        registryCenter.remove(ActuatorNode.createClusterPath(clusterName, endpoint));
    }

    public void createBindingPath(String clusterName, String groupName) {
        // create binding
        registryCenter.createPersistent(ActuatorNode.createBindingPath(clusterName, groupName),
                String.valueOf(System.currentTimeMillis()));
    }

    public void removeBindingPath(String clusterName, String groupName) {
        // create binding
        registryCenter.remove(ActuatorNode.createBindingPath(clusterName, groupName));
    }

    public String getServerNodeInfo(String runningNode) {
        return registryCenter.get(SchedulerNode.getServerMetricsNodePath(runningNode));
    }

    public List<String> getActuatorClusters() {
        return registryCenter.getChildren(ActuatorNode.ACTUATOR_CLUSTERS_PATH);
    }

    public String getActuatorClusterInfo(String clusterName) {
        return registryCenter.get(ActuatorNode.getClusterPath(clusterName));
    }

    public void put(String path, String data) {
        registryCenter.createPersistent(path, data);
    }

    /**
     * 在注册中心创建任务类型
     */
    public void createJobTypeNames(JobTypeInfo jobTypeInfo) {
        registryCenter.createPersistent(jobTypeInfo.getClusterName(), gsonUtils.toJson(jobTypeInfo.getJobTypeNames()));
    }

    /**
     * 从注册中心返回任务类型
     * @param clusterName 执行集群名字
     * @return 任务类型集合
     */
    public List<String> getJobTypeNames(String clusterName) {
        String jobTypeNamesStr = registryCenter.get(clusterName);
        return gsonUtils.fromJson(jobTypeNamesStr, List.class);
    }
}
