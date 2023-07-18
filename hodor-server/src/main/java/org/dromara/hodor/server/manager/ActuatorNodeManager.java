package org.dromara.hodor.server.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.common.Host;
import org.dromara.hodor.common.concurrent.HodorThreadFactory;
import org.dromara.hodor.common.utils.Pair;
import org.dromara.hodor.common.utils.TimeUtil;
import org.dromara.hodor.common.utils.Utils;
import org.dromara.hodor.model.job.JobKey;
import org.dromara.hodor.model.node.NodeInfo;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * actuator node manager
 *
 * @author tomgs
 * @version 2021/8/1 1.0
 */
@Slf4j
public class ActuatorNodeManager {

    private static volatile ActuatorNodeManager INSTANCE;

    private final Set<String> clusters = Sets.newConcurrentHashSet();

    // clusterName -> endpoint set
    private final Map<String, Set<String>> actuatorClusterEndpoints = Maps.newConcurrentMap();

    // clusterName -> groupName set
    private final Map<String, Set<String>> clusterGroupMap = Maps.newConcurrentMap();

    // groupName -> clusterName set
    private final Map<String, String> groupClusterMap = Maps.newConcurrentMap();

    // endpoint -> actuatorNodeInfo
    private final Map<String, Pair<NodeInfo, Long>> actuatorNodeInfos = Maps.newConcurrentMap();

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
            final Pair<NodeInfo, Long> nodePair = entry.getValue();
            if (heartbeatThresholdExceedCheck(nodePair.getSecond())) {
                String endpoint = entry.getKey();
                removeNode(endpoint);
                return true;
            }
            return false;
        });
    }

    private void removeNode(String endpoint) {
        actuatorNodeInfos.remove(endpoint);
        actuatorClusterEndpoints.values().forEach(endpoints -> endpoints.removeIf(e -> e.equals(endpoint)));
    }

    public Set<String> getActuatorEndpointsByGroupName(final String groupName) {
        final String clusterName = groupClusterMap.get(groupName);
        Utils.Assert.notNull(clusterName, "Group [{}] not found actuator cluster", groupName);
        return actuatorClusterEndpoints.getOrDefault(clusterName, Sets.newConcurrentHashSet());
    }

    public boolean isOffline(String endpoint) {
        final long lastHeartbeat = Optional.ofNullable(actuatorNodeInfos.get(endpoint))
            .orElse(new Pair<>(null, 0L))
            .getSecond();
        return heartbeatThresholdExceedCheck(lastHeartbeat);
    }

    private boolean heartbeatThresholdExceedCheck(Long lastHeartbeat) {
        return TimeUtil.currentTimeMillis() - lastHeartbeat > HEARTBEAT_THRESHOLD;
    }

    public void clearActuatorNodes() {
        actuatorClusterEndpoints.clear();
        clusterGroupMap.clear();
        groupClusterMap.clear();
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

    public Set<String> getGroupByClusterName(String clusterName) {
        return clusterGroupMap.getOrDefault(clusterName, new HashSet<>());
    }

    public String getClusterNameByGroup(String groupName) {
        return groupClusterMap.get(groupName);
    }

    public void addClusterGroupEntry(String clusterName, String groupName) {
        Set<String> groupSet = clusterGroupMap.computeIfAbsent(clusterName, k -> Sets.newHashSet());
        groupSet.add(groupName);
        groupClusterMap.put(groupName, clusterName);
    }

    public void removeClusterGroupEntry(String clusterName, String groupName) {
        Set<String> groupSet = clusterGroupMap.get(clusterName);
        if (groupSet != null) {
            groupSet.remove(groupName);
        }
        groupClusterMap.remove(groupName);
    }

    public void addActuatorClusterEndpoint(String clusterName, String nodeEndpoint) {
        clusters.add(clusterName);
        final Set<String> endpoints = actuatorClusterEndpoints.computeIfAbsent(clusterName, k -> Sets.newHashSet());
        endpoints.add(nodeEndpoint);
    }

    public void addActuatorClusterNodeInfo(String nodeEndpoint, NodeInfo nodeInfo, long lastHeartbeat) {
        actuatorNodeInfos.put(nodeEndpoint, new Pair<>(nodeInfo, lastHeartbeat));
    }

    public void removeActuatorClusterEndpoint(String clusterName, String nodeEndpoint) {
        if (actuatorClusterEndpoints.containsKey(clusterName)) {
            Set<String> endpoints = actuatorClusterEndpoints.get(clusterName);
            endpoints.removeIf(e -> e.equals(nodeEndpoint));
            if (Utils.Collections.isEmpty(endpoints)) {
                actuatorClusterEndpoints.remove(clusterName);
            }
        }
    }

    public void removeActuatorClusterNodeInfo(String nodeEndpoint) {
        actuatorNodeInfos.remove(nodeEndpoint);
    }

    public Pair<NodeInfo, Long> getActuatorNodeInfo(String endpoint) {
        return actuatorNodeInfos.get(endpoint);
    }

    public Set<String> getActuatorClusterEndpoints(String clusterName) {
        return actuatorClusterEndpoints.get(clusterName);
    }

    public Set<String> allClusters() {
        return clusters;
    }

}
