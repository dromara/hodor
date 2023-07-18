package org.dromara.hodor.server.api;

import com.google.common.base.Preconditions;
import org.dromara.hodor.common.utils.Pair;
import org.dromara.hodor.model.actuator.ActuatorInfo;
import org.dromara.hodor.model.actuator.BindingInfo;
import org.dromara.hodor.model.common.HodorResult;
import org.dromara.hodor.model.node.NodeInfo;
import org.dromara.hodor.server.manager.ActuatorNodeManager;
import org.dromara.hodor.server.restservice.HodorRestService;
import org.dromara.hodor.server.restservice.RestMethod;
import org.dromara.hodor.server.service.RegistryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * actuator service
 *
 * @author tomgs
 * @since 1.0
 */
@HodorRestService(value = "actuator", desc = "actuator rest service")
@SuppressWarnings("unused")
public class ActuatorResource {

    private final RegistryService registryService;

    private final ActuatorNodeManager actuatorNodeManager;

    public ActuatorResource(final RegistryService registryService) {
        this.registryService = registryService;
        this.actuatorNodeManager = ActuatorNodeManager.getInstance();
    }

    @RestMethod("heartbeat")
    public HodorResult<String> heartbeat(ActuatorInfo actuatorInfo) {
        checkActuatorInfo(actuatorInfo);
        actuatorInfo.setLastHeartbeat(System.currentTimeMillis());
        registryService.createActuator(actuatorInfo);
        return HodorResult.success("success");
    }

    @RestMethod("offline")
    public HodorResult<String> offline(ActuatorInfo actuatorInfo) {
        checkActuatorInfo(actuatorInfo);
        registryService.removeActuator(actuatorInfo);
        return HodorResult.success("success");
    }

    @RestMethod("binding")
    public HodorResult<String> binding(BindingInfo bindingInfo) {
        Preconditions.checkNotNull(bindingInfo, "bindingInfo must be not null");
        final String clusterName = Preconditions.checkNotNull(bindingInfo.getClusterName(), "clusterName must be not null.");
        final String groupName = Preconditions.checkNotNull(bindingInfo.getGroupName(), "groupName must be not null.");
        registryService.createBindingPath(clusterName, groupName);
        return HodorResult.success("success");
    }

    @RestMethod("unbinding")
    public HodorResult<String> unbinding(BindingInfo bindingInfo) {
        Preconditions.checkNotNull(bindingInfo, "bindingInfo must be not null");
        final String clusterName = Preconditions.checkNotNull(bindingInfo.getClusterName(), "clusterName must be not null.");
        final String groupName = Preconditions.checkNotNull(bindingInfo.getGroupName(), "groupName must be not null.");
        registryService.removeBindingPath(clusterName, groupName);
        return HodorResult.success("success");
    }

    @RestMethod("listBinding")
    public HodorResult<List<BindingInfo>> listBinding() {
        List<BindingInfo> result = new ArrayList<>();
        Set<String> clusterNames = actuatorNodeManager.allClusters();
        for (String clusterName : clusterNames) {
            Set<String> groupNames = actuatorNodeManager.getGroupByClusterName(clusterName);
            for (String groupName : groupNames) {
                BindingInfo bindingInfo = new BindingInfo()
                    .setClusterName(clusterName)
                    .setGroupName(groupName);
                result.add(bindingInfo);
            }
        }
        return HodorResult.success("success", result);
    }

    @RestMethod("actuatorInfos")
    public HodorResult<List<ActuatorInfo>> actuatorInfos() {
        List<ActuatorInfo> actuatorInfos = new ArrayList<>();
        List<String> clusterNames = registryService.getActuatorClusters();
        for (String clusterName : clusterNames) {
            Set<String> actuatorClusterEndpoints = actuatorNodeManager.getActuatorClusterEndpoints(clusterName);
            for (String actuatorClusterEndpoint : actuatorClusterEndpoints) {
                final Pair<NodeInfo, Long> actuatorNodeInfo = actuatorNodeManager.getActuatorNodeInfo(actuatorClusterEndpoint);
                ActuatorInfo actuatorInfo = new ActuatorInfo();
                actuatorInfo.setName(clusterName);
                actuatorInfo.setNodeEndpoint(actuatorClusterEndpoint);
                actuatorInfo.setGroupNames(actuatorNodeManager.getGroupByClusterName(clusterName));
                if (actuatorNodeInfo != null) {
                    actuatorInfo.setNodeInfo(actuatorNodeInfo.getFirst());
                    actuatorInfo.setLastHeartbeat(actuatorNodeInfo.getSecond());
                }
                actuatorInfos.add(actuatorInfo);
            }
        }
        return HodorResult.success("success", actuatorInfos);
    }

    @RestMethod("allClusters")
    public HodorResult<List<String>> allClusters() {
        List<String> clusterNames = registryService.getActuatorClusters();
        return HodorResult.success("success", clusterNames);
    }

    private void checkActuatorInfo(ActuatorInfo actuatorInfo) {
        Preconditions.checkNotNull(actuatorInfo.getName(), "actuator name must be not null.");
        Preconditions.checkNotNull(actuatorInfo.getNodeInfo(), "actuator node info must be not null.");
        Preconditions.checkNotNull(actuatorInfo.getGroupNames(), "actuator group names must be not null.");
    }

}
