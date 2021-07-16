package org.dromara.hodor.client;

import cn.hutool.http.HttpUtil;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.client.annotation.HodorProperties;
import org.dromara.hodor.model.node.NodeInfo;
import org.dromara.hodor.model.job.JobDesc;
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

    private final String appName;

    private final String appKey;

    private final GsonUtils gsonUtils = GsonUtils.getInstance();

    public HodorApiClient(final HodorProperties properties) {
        this.registryAddress = properties.getRegistryAddress();
        this.appKey = properties.getAppKey();
        this.appName = properties.getAppName();
    }

    public void registerJobs(Collection<JobDesc> jobs) {
        String result = HttpUtil.createPost(registryAddress + "/scheduler/createJob")
            .body(gsonUtils.toJson(jobs))
            .header("appName", appName)
            .header("appKey", appKey)
            .execute()
            .body();
        log.info("Register jobs result: {}", result);
    }

    public void sendHeartbeat(NodeInfo msg) {
        String result = HttpUtil.createPost(registryAddress + "/worker/heartbeat")
            .body(gsonUtils.toJson(msg))
            .header("appName", appName)
            .header("appKey", appKey)
            .execute()
            .body();
        log.info("Send heartbeat result: {}", result);
    }

    public void sendOfflineMsg(NodeInfo msg) {
        String result = HttpUtil.createPost(registryAddress + "/worker/offline")
            .body(gsonUtils.toJson(msg))
            .header("appName", appName)
            .header("appKey", appKey)
            .execute()
            .body();
        //log.info("Send heartbeat result: {}", result);
    }

}
