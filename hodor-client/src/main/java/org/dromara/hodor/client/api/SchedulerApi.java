/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.hodor.client.api;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.http.HttpResponse;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.client.exception.HodorClientException;
import org.dromara.hodor.client.model.SchedulerNodeResult;
import org.dromara.hodor.common.connect.ConnectStringParser;
import org.dromara.hodor.common.connect.TrySender;
import org.dromara.hodor.common.utils.StringUtils;
import org.dromara.hodor.common.utils.Utils.Https;
import org.dromara.hodor.common.utils.Utils.Jsons;
import org.dromara.hodor.model.common.HodorResult;
import org.dromara.hodor.model.scheduler.HodorMetadata;

/**
 * SchedulerApi
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class SchedulerApi {

    private final ConnectStringParser connectStringParser;

    private final String appName;

    private final String appKey;

    public SchedulerApi(ConnectStringParser connectStringParser, String appName, String appKey) {
        this.connectStringParser = connectStringParser;
        this.appName = appName;
        this.appKey = appKey;
    }

    public List<SchedulerNodeResult> getSchedulers() throws Exception {
        final HttpResponse response = TrySender.send(connectStringParser, (url) -> Https.createGet(url + "/scheduler/nodeInfos")
                .header("appName", appName)
                .header("appKey", appKey)
                .execute(),
            ex -> new HodorClientException("SchedulerApi getSchedulers execute failure, " + ex.getMessage()));
        if (!Objects.requireNonNull(response).isOk()) {
            throw new HodorClientException("getSchedulers failure, " + response.body());
        }
        log.debug("Get SchedulerNodeResult : {}", response.body());
        final HodorResult<List<SchedulerNodeResult>> hodorResult = Jsons.toBean(response.body(),
            new TypeReference<HodorResult<List<SchedulerNodeResult>>>() {});
        if (!hodorResult.isSuccess()) {
            throw new HodorClientException(hodorResult.getMsg());
        }
        return hodorResult.getData();
    }

    public HodorMetadata getMetadata(String endpoint) throws Exception {
        ConnectStringParser parser = connectStringParser;
        boolean localed = false;
        if (StringUtils.isNotBlank(endpoint)) {
            parser = new ConnectStringParser("http://" + endpoint + "/" + connectStringParser.getRootName());
            localed = true;
        }
        final String path = "/scheduler/metadata?localed=" + localed;
        final HttpResponse response = TrySender.send(parser, (url) -> Https.createGet(url + path)
                .header("appName", appName)
                .header("appKey", appKey)
                .execute(),
            ex -> new HodorClientException("SchedulerApi getMetadata execute failure, " + ex.getMessage()));
        if (!Objects.requireNonNull(response).isOk()) {
            throw new HodorClientException("getMetadata failure, " + response.body());
        }
        log.debug("Get HodorMetadata : {}", response.body());
        final HodorResult<HodorMetadata> hodorResult = Jsons.toBean(response.body(),
            new TypeReference<HodorResult<HodorMetadata>>() {});
        if (!hodorResult.isSuccess()) {
            throw new HodorClientException(hodorResult.getMsg());
        }
        return hodorResult.getData();
    }

    /**
     * 返回任务类型接口
     */
    public List<String> getJobTypeNames(String clusterName) throws Exception {
        final String path = "/scheduler/getJobTypeNames?clusterName=" + clusterName;
        final HttpResponse response = TrySender.send(connectStringParser, (url) -> Https.createGet(url + path)
                .header("appName", appName)
                .header("appKey", appKey)
                .execute(),
            ex -> new HodorClientException("SchedulerApi getJobTypeNames execute failure, " + ex.getMessage()));
        if (!Objects.requireNonNull(response).isOk()) {
            throw new HodorClientException("getJobTypeNames failure, " + response.body());
        }
        log.debug("Get JobTypeNamesResult : {}", response.body());
        final HodorResult<List<String>> hodorResult = Jsons.toBean(response.body(),
            new TypeReference<HodorResult<List<String>>>() {});
        if (!hodorResult.isSuccess()) {
            throw new HodorClientException(hodorResult.getMsg());
        }
        return hodorResult.getData();
    }

}
