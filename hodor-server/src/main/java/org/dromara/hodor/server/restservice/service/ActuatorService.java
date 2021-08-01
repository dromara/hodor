package org.dromara.hodor.server.restservice.service;

import org.dromara.hodor.model.actuator.ActuatorInfo;
import org.dromara.hodor.model.common.HodorResult;
import org.dromara.hodor.server.manager.ActuatorManager;
import org.dromara.hodor.server.restservice.HodorRestService;
import org.dromara.hodor.server.restservice.RestMethod;

/**
 * actuator service
 *
 * @author tomgs
 * @since 2021/2/5
 */
@HodorRestService(value = "actuator", desc = "actuator rest service")
@SuppressWarnings("unused")
public class ActuatorService {

    private final ActuatorManager actuatorManager;

    public ActuatorService() {
        this.actuatorManager = ActuatorManager.getInstance();
    }

    @RestMethod("heartbeat")
    public HodorResult<String> heartbeat(ActuatorInfo actuatorInfo) {
        actuatorInfo.setLastHeartbeat(System.currentTimeMillis());
        actuatorManager.addActuator(actuatorInfo);
        return HodorResult.success("success");
    }

    @RestMethod("offline")
    public HodorResult<String> offline(ActuatorInfo actuatorInfo) {
        actuatorManager.removeActuator(actuatorInfo);
        return HodorResult.success("success");
    }

}
