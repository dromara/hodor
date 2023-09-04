package org.dromara.hodor.actuator.api;

import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.actuator.api.config.HodorProperties;
import org.dromara.hodor.common.connect.ConnectStringParser;
import org.dromara.hodor.common.connect.TrySender;
import org.dromara.hodor.common.utils.GsonUtils;
import org.dromara.hodor.common.utils.Utils.Assert;
import org.dromara.hodor.common.utils.Utils.Collections;
import org.dromara.hodor.common.utils.Utils.Https;
import org.dromara.hodor.model.actuator.ActuatorInfo;
import org.dromara.hodor.model.actuator.JobTypeInfo;
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
        Assert.notBlank(properties.getAppName(), "App name must be not null");
        Assert.notBlank(properties.getAppKey(), "App key must be not null");
        Assert.notBlank(properties.getRegistryAddress(), "Registry address must be not null");

        this.connectStringParser = new ConnectStringParser(properties.getRegistryAddress());
        this.appKey = properties.getAppKey();
        this.appName = properties.getAppName();
    }

    public void registerJobs(Collection<JobDesc> jobs) throws Exception {
        if (Collections.isEmpty(jobs)) {
            return;
        }
        String result = TrySender.send(connectStringParser, (url) -> Https.createPost( url + "/scheduler/batchCreateJob")
            .body(gsonUtils.toJson(jobs))
            .header("appName", appName)
            .header("appKey", appKey)
            .execute()
            .body());
        log.info("Register jobs result: {}", result);
    }

    public void sendHeartbeat(ActuatorInfo actuatorInfo) throws Exception {
        String result = TrySender.send(connectStringParser, (url) -> Https.createPost(url + "/actuator/heartbeat")
            .body(gsonUtils.toJson(actuatorInfo))
            .header("appName", appName)
            .header("appKey", appKey)
            .execute()
            .body());
        log.debug("Send heartbeat result: {}", result);
    }

    public void sendOfflineMsg(ActuatorInfo actuatorInfo) throws Exception {
        String result = TrySender.send(connectStringParser, (url) -> Https.createPost(url + "/actuator/offline")
            .body(gsonUtils.toJson(actuatorInfo))
            .header("appName", appName)
            .header("appKey", appKey)
            .execute()
            .body());
        log.info("Send Offline result: {}", result);
    }

    public void registerJobTypeName(JobTypeInfo jobTypeInfo) throws Exception {
        if (jobTypeInfo == null || Collections.isEmpty(jobTypeInfo.getJobTypeNames())) {
            return;
        }
        String result = TrySender.send(connectStringParser, (url) -> Https.createPost( url + "/scheduler/jobTypeNames")
            .body(gsonUtils.toJson(jobTypeInfo))
            .header("appName", appName)
            .header("appKey", appKey)
            .execute()
            .body());
        log.info("Register jobTypeNames result: {}", result);
    }
}
