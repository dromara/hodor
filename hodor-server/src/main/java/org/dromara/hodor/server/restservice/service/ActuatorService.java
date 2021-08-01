package org.dromara.hodor.server.restservice.service;

import org.dromara.hodor.model.actuator.ActuatorInfo;
import org.dromara.hodor.model.common.HodorResult;
import org.dromara.hodor.server.restservice.HodorRestService;
import org.dromara.hodor.server.restservice.RestMethod;
import org.dromara.hodor.server.service.RegisterService;

/**
 * actuator service
 *
 * @author tomgs
 * @since 2021/2/5
 */
@HodorRestService(value = "actuator", desc = "actuator rest service")
@SuppressWarnings("unused")
public class ActuatorService {

    private final RegisterService registerService;

    public ActuatorService(final RegisterService registerService) {
        this.registerService = registerService;
    }

    @RestMethod("heartbeat")
    public HodorResult<String> heartbeat(ActuatorInfo actuatorInfo) {
        //registerService.refreshAvailableWorkNodes();
        return HodorResult.success("success");
    }

    @RestMethod("offline")
    public HodorResult<String> offline(ActuatorInfo actuatorInfo) {
        System.out.println(actuatorInfo);
        return HodorResult.success("success");
    }

}
