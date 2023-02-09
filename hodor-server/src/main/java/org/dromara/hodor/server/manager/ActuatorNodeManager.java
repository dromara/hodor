package org.dromara.hodor.server.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.Host;
import org.dromara.hodor.common.concurrent.HodorThreadFactory;
import org.dromara.hodor.common.utils.TimeUtil;
import org.dromara.hodor.model.actuator.ActuatorInfo;
import org.dromara.hodor.model.job.JobKey;
import org.dromara.hodor.model.node.NodeInfo;

/**
 * actuator node manager
 *
 * @author tomgs
 * @version 2021/8/1 1.0
 */
@Slf4j
public class ActuatorNodeManager {

    private static volatile ActuatorNodeManager INSTANCE;

    // groupName -> endpoint set
    private final Map<String, Set<String>> actuatorEndpoints = Maps.newConcurrentMap();

    // clusterName -> groupName set
    private final Map<String, Set<String>> clusterGroupMap = Maps.newConcurrentMap();

    // endpoint -> actuatorNodeInfo
    private final Map<String, ActuatorInfo> actuatorNodeInfos = Maps.newConcurrentMap();

    private static final int HEARTBEAT_THRESHOLD = 30_000;

    private ScheduledExecutorService cleanSchedule;

    private ActuatorNodeManager() {

    }

    public static ActuatorNodeManager getInstance() {
        if (INSTANCE == null) {
            synchronized (ActuatorNodeManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ActuatorNodeManager();
                }
            }
        }
        return INSTANCE;
    }

    public void startOfflineActuatorClean() {
        this.cleanSchedule = Executors.newSingleThreadScheduledExecutor(HodorThreadFactory.create("offline-actuator-cleaner", false));
        this.cleanSchedule.scheduleWithFixedDelay(this::offlineActuatorClean, 30, 60, TimeUnit.SECONDS);
    }

    public void offlineActuatorClean() {
        log.info("offline actuator clean ...");
        actuatorNodeInfos.entrySet().removeIf(entry -> {
            ActuatorInfo actuatorInfo = entry.getValue();
            if (heartbeatThresholdExceedCheck(actuatorInfo.getLastHeartbeat())) {
                String endpoint = entry.getKey();
                removeNode(endpoint);
                return true;
            }
            return false;
        });
    }

    private void removeNode(String endpoint) {
        actuatorNodeInfos.remove(endpoint);
        actuatorEndpoints.values().forEach(endpoints -> endpoints.removeIf(e -> e.equals(endpoint)));
    }

    public void addActuatorEndpoint(String groupName, String nodeEndpoint) {
        Set<String> endpoints = actuatorEndpoints.computeIfAbsent(groupName, e -> Sets.newConcurrentHashSet());
        endpoints.add(nodeEndpoint);
    }

    public void removeActuatorGroup(String groupName, String nodeEndpoint) {
        Optional.ofNullable(actuatorEndpoints.get(groupName)).ifPresent(endpoints -> endpoints.removeIf(endpoint -> endpoint.equals(nodeEndpoint)));
    }

    public Set<String> getActuatorEndpointsByGroupName(final String groupName) {
        return actuatorEndpoints.getOrDefault(groupName, Sets.newConcurrentHashSet());
    }

    public boolean isOffline(String endpoint) {
        final long lastHeartbeat = Optional.ofNullable(actuatorNodeInfos.get(endpoint))
            .orElse(ActuatorInfo.builder().lastHeartbeat(0).build())
            .getLastHeartbeat();
        return heartbeatThresholdExceedCheck(lastHeartbeat);
    }

    private boolean heartbeatThresholdExceedCheck(Long lastHeartbeat) {
        return TimeUtil.currentTimeMillis() - lastHeartbeat > HEARTBEAT_THRESHOLD;
    }

    public void clearActuatorNodes() {
        actuatorEndpoints.clear();
        actuatorNodeInfos.clear();
    }

    public void stopOfflineActuatorClean() {
        if (cleanSchedule != null) {
            cleanSchedule.shutdown();
        }
    }

    public List<Host> getAvailableHosts(JobKey jobKey) {
        List<String> allWorkNodes = Lists.newArrayList(getActuatorEndpointsByGroupName(jobKey.getGroupName()));
        return allWorkNodes.stream()
            .filter(endpoint -> !isOffline(endpoint))
            .map(Host::of)
            .collect(Collectors.toList());
    }

    public void addActuatorNode(String nodeEndpoint, NodeInfo nodeInfo) {
        ActuatorInfo actuatorInfo = actuatorNodeInfos.computeIfAbsent(nodeEndpoint, k -> ActuatorInfo.builder()
            .nodeEndpoint(nodeEndpoint)
            .groupNames(Sets.newHashSet())
            .build());
        actuatorInfo.setNodeInfo(nodeInfo);
    }

    public NodeInfo getActuatorNode(String nodeEndpoint) {
        ActuatorInfo actuatorNodeInfo = Optional.ofNullable(actuatorNodeInfos.get(nodeEndpoint))
            .orElse(ActuatorInfo.builder()
                .nodeInfo(new NodeInfo())
                .build());
        return actuatorNodeInfo.getNodeInfo();
    }

    public void removeActuatorNode(String nodeEndpoint) {
        actuatorNodeInfos.remove(nodeEndpoint);
    }

    public void addActuatorNodeInfo(String groupName, String nodeEndpoint, long lastHeartbeat) {
        ActuatorInfo actuatorInfo = actuatorNodeInfos.computeIfAbsent(nodeEndpoint, k -> ActuatorInfo.builder()
            .nodeEndpoint(nodeEndpoint)
            .groupNames(Sets.newHashSet(groupName))
            .lastHeartbeat(lastHeartbeat)
            .build());
        actuatorInfo.getGroupNames().add(groupName);
        actuatorInfo.setLastHeartbeat(lastHeartbeat);
    }

    public Set<String> getGroupByClusterName(String clusterName) {
        return clusterGroupMap.getOrDefault(clusterName, new HashSet<>());
    }

    public void addClusterGroupEntry(String clusterName, String groupName) {
        Set<String> groupSet = clusterGroupMap.computeIfAbsent(clusterName, Sets::newHashSet);
        groupSet.add(groupName);
    }

    public void removeClusterGroupEntry(String clusterName, String groupName) {
        Set<String> groupSet = clusterGroupMap.computeIfAbsent(clusterName, Sets::newHashSet);
        groupSet.remove(groupName);
    }
}
