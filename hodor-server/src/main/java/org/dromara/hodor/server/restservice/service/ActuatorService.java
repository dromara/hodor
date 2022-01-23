package org.dromara.hodor.server.restservice.service;

import com.google.common.base.Preconditions;
import org.dromara.hodor.model.actuator.ActuatorInfo;
import org.dromara.hodor.model.common.HodorResult;
import org.dromara.hodor.server.restservice.HodorRestService;
import org.dromara.hodor.server.restservice.RestMethod;
import org.dromara.hodor.server.service.RegistryService;

/**
 * actuator service
 *
 * @author tomgs
 * @since 2021/2/5
 */
@HodorRestService(value = "actuator", desc = "actuator rest service")
@SuppressWarnings("unused")
public class ActuatorService {

    private final RegistryService registryService;

    public ActuatorService(RegistryService registryService) {
        this.registryService = registryService;
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
    public HodorResult<String> binding(String clusterName, String groupName) {
        Preconditions.checkNotNull(clusterName, "clusterName must be not null.");
        Preconditions.checkNotNull(groupName, "groupName must be not null.");
        registryService.createBindingPath(clusterName, groupName);
        return HodorResult.success("success");
    }

    @RestMethod("unbinding")
    public HodorResult<String> unbinding(String clusterName, String groupName) {
        Preconditions.checkNotNull(clusterName, "clusterName must be not null.");
        Preconditions.checkNotNull(groupName, "groupName must be not null.");
        registryService.removeBindingPath(clusterName, groupName);
        return HodorResult.success("success");
    }

    private void checkActuatorInfo(ActuatorInfo actuatorInfo) {
        Preconditions.checkNotNull(actuatorInfo.getNodeInfo(), "actuator node info must be not null.");
        Preconditions.checkNotNull(actuatorInfo.getGroupNames(), "actuator group names must be not null.");
    }

}
