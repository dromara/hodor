package org.dromara.hodor.server.restservice.service;

import org.dromara.hodor.model.common.HodorResult;
import org.dromara.hodor.model.node.NodeInfo;
import org.dromara.hodor.server.restservice.HodorRestService;

/**
 * register service
 *
 * @author tomgs
 * @since 2021/2/5
 */
@HodorRestService(value = "worker", desc = "work rest service")
@SuppressWarnings("unused")
public class WorkerService {

    public HodorResult<String> heartbeat(NodeInfo nodeInfo) {
        System.out.println(nodeInfo);
        return HodorResult.success("success");
    }

    public HodorResult<String> offline(NodeInfo nodeInfo) {
        System.out.println(nodeInfo);
        return HodorResult.success("success");
    }

}
