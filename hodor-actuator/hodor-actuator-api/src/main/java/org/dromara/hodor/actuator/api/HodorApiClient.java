package org.dromara.hodor.actuator.api;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpUtil;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.actuator.api.config.HodorProperties;
import org.dromara.hodor.actuator.api.core.ConnectStringParser;
import org.dromara.hodor.actuator.api.core.TrySender;
import org.dromara.hodor.common.utils.GsonUtils;
import org.dromara.hodor.model.actuator.ActuatorInfo;
import org.dromara.hodor.model.job.JobDesc;

/**
 * hodor api client
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class HodorApiClient {

    private final String appName;

    private final String appKey;

    private final ConnectStringParser connectStringParser;

    private final GsonUtils gsonUtils = GsonUtils.getInstance();

    public HodorApiClient(final HodorProperties properties) {
        this.connectStringParser = new ConnectStringParser(properties.getRegistryAddress());
        this.appKey = properties.getAppKey();
        this.appName = properties.getAppName();
    }

    public void registerJobs(Collection<JobDesc> jobs) throws Exception {
        if (CollectionUtil.isEmpty(jobs)) {
            return;
        }
        String result = TrySender.send(connectStringParser, (url) -> HttpUtil.createPost( url + "/scheduler/batchCreateJob")
            .body(gsonUtils.toJson(jobs))
            .header("appName", appName)
            .header("appKey", appKey)
            .execute()
            .body());
        log.info("Register jobs result: {}", result);
    }

    public void sendHeartbeat(ActuatorInfo actuatorInfo) throws Exception {
        String result = TrySender.send(connectStringParser, (url) -> HttpUtil.createPost(url + "/actuator/heartbeat")
            .body(gsonUtils.toJson(actuatorInfo))
            .header("appName", appName)
            .header("appKey", appKey)
            .execute()
            .body());
        log.debug("Send heartbeat result: {}", result);
    }

    public void sendOfflineMsg(ActuatorInfo actuatorInfo) throws Exception {
        String result = TrySender.send(connectStringParser, (url) -> HttpUtil.createPost(url + "/actuator/offline")
            .body(gsonUtils.toJson(actuatorInfo))
            .header("appName", appName)
            .header("appKey", appKey)
            .execute()
            .body());
        log.info("Send Offline result: {}", result);
    }

}
