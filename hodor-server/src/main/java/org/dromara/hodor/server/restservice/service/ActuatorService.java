package org.dromara.hodor.server.restservice.service;

import org.dromara.hodor.model.common.HodorResult;
import org.dromara.hodor.model.node.NodeInfo;
import org.dromara.hodor.server.restservice.HodorRestService;

/**
 * actuator service
 *
 * @author tomgs
 * @since 2021/2/5
 */
@HodorRestService(value = "actuator", desc = "actuator rest service")
@SuppressWarnings("unused")
public class ActuatorService {

    public HodorResult<String> heartbeat(NodeInfo nodeInfo) {
        System.out.println(nodeInfo);
        return HodorResult.success("success");
    }

    public HodorResult<String> offline(NodeInfo nodeInfo) {
        System.out.println(nodeInfo);
        return HodorResult.success("success");
    }

}
