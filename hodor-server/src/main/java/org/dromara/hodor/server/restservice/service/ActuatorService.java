package org.dromara.hodor.server.restservice.service;

import org.dromara.hodor.model.common.HodorResult;
import org.dromara.hodor.model.node.NodeInfo;
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

    @RestMethod("heartbeat")
    public HodorResult<String> heartbeat(NodeInfo nodeInfo) {
        System.out.println(nodeInfo);
        return HodorResult.success("success");
    }

    @RestMethod("offline")
    public HodorResult<String> offline(NodeInfo nodeInfo) {
        System.out.println(nodeInfo);
        return HodorResult.success("success");
    }

}
