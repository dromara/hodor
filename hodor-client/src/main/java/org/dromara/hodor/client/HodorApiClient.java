package org.dromara.hodor.client;

import cn.hutool.http.HttpUtil;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.client.annotation.HodorProperties;
import org.dromara.hodor.client.config.HeartbeatMsg;
import org.dromara.hodor.client.config.JobDesc;
import org.dromara.hodor.common.utils.GsonUtils;

/**
 * hodor api client
 *
 * @author tomgs
 * @since 2020/12/30
 */
@Slf4j
public class HodorApiClient {

    private final String registryAddress;

    private final GsonUtils gsonUtils = GsonUtils.getInstance();

    public HodorApiClient(final HodorProperties properties) {
        this.registryAddress = properties.getRegistryAddress();
    }

    public void registerJobs(Collection<JobDesc> jobs) {
        String result = HttpUtil.post(registryAddress + "/jobs", gsonUtils.toJson(jobs));
        log.info("Register jobs result: {}", result);
    }

    public void sendHeartbeat(HeartbeatMsg msg) {
        String result = HttpUtil.post(registryAddress + "/heartbeat", gsonUtils.toJson(msg));
        log.info("Send heartbeat result: {}", result);
    }

}
