package org.dromara.hodor.server.api;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.dromara.hodor.common.utils.Utils.Jsons;
import org.dromara.hodor.model.actuator.ActuatorInfo;
import org.dromara.hodor.model.actuator.BindingInfo;
import org.dromara.hodor.model.common.HodorResult;
import org.dromara.hodor.model.node.NodeInfo;
import org.dromara.hodor.server.manager.ActuatorNodeManager;
import org.dromara.hodor.server.restservice.HodorRestService;
import org.dromara.hodor.server.restservice.RestMethod;
import org.dromara.hodor.server.service.RegistryService;

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
    public HodorResult<List<String>> listBinding() {

        return HodorResult.success("success");
    }

    @RestMethod("actuatorInfos")
    public HodorResult<List<ActuatorInfo>> actuatorInfos() {
        List<ActuatorInfo> actuatorInfos = new ArrayList<>();
        List<String> clusterNames = registryService.getActuatorClusters();
        for (String clusterName : clusterNames) {
            //List<NodeInfo> nodeInfos = actuatorNodeManager.getActuatorNodesByCluster(clusterName);
            //actuatorNodeManager.getActuatorNode(clusterEndpoint);
           /* Optional.ofNullable(clusterInfo)
                .ifPresent(e -> {
                    final ActuatorInfo actuatorInfo = Jsons.toBean(e, ActuatorInfo.class);
                    actuatorInfos.add(actuatorInfo);
                });*/
        }
        return HodorResult.success("success", actuatorInfos);
    }

    private void checkActuatorInfo(ActuatorInfo actuatorInfo) {
        Preconditions.checkNotNull(actuatorInfo.getName(), "actuator name must be not null.");
        Preconditions.checkNotNull(actuatorInfo.getNodeInfo(), "actuator node info must be not null.");
        Preconditions.checkNotNull(actuatorInfo.getGroupNames(), "actuator group names must be not null.");
    }

}
