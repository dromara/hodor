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

import cn.hutool.http.HttpResponse;
import java.util.Collection;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.client.exception.HodorClientException;
import org.dromara.hodor.common.connect.ConnectStringParser;
import org.dromara.hodor.common.connect.TrySender;
import org.dromara.hodor.common.utils.Utils.Https;
import org.dromara.hodor.common.utils.Utils.Jsons;
import org.dromara.hodor.model.job.JobDesc;

/**
 * JobApi
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
public class JobApi {

    private final ConnectStringParser connectStringParser;

    private final String appName;

    private final String appKey;

    public void registerJob(JobDesc job) throws Exception {
        final HttpResponse response = TrySender.send(connectStringParser, (url) -> Https.createPost(url + "/scheduler/createJob")
                .body(Jsons.toJson(job))
                .header("appName", appName)
                .header("appKey", appKey)
                .execute(),
            ex -> new HodorClientException("JobApi registerJob execute failure, " + ex.getMessage()));
        if (!Objects.requireNonNull(response).isOk()) {
            throw new HodorClientException("Register job failure, " + response.body());
        }
    }

    public void registerJobs(Collection<JobDesc> jobs) throws Exception {
        final HttpResponse response = TrySender.send(connectStringParser, (url) -> Https.createPost(url + "/scheduler/batchCreateJob")
            .body(Jsons.toJson(jobs))
            .header("appName", appName)
            .header("appKey", appKey)
            .execute());
        if (!Objects.requireNonNull(response).isOk()) {
            throw new HodorClientException("Register failure, " + response.body());
        }
    }

    public void updateJob(JobDesc job) throws Exception {
        final HttpResponse response = TrySender.send(connectStringParser, (url) -> Https.createPost(url + "/scheduler/updateJob")
                .body(Jsons.toJson(job))
                .header("appName", appName)
                .header("appKey", appKey)
                .execute(),
            ex -> new HodorClientException("JobApi updateJob execute failure, " + ex.getMessage()));
        if (!Objects.requireNonNull(response).isOk()) {
            throw new HodorClientException("Update job failure, " + response.body());
        }
    }

    public void deleteJob(JobDesc job) throws Exception {
        final HttpResponse response = TrySender.send(connectStringParser, (url) -> Https.createPost(url + "/scheduler/deleteJob")
            .body(Jsons.toJson(job))
            .header("appName", appName)
            .header("appKey", appKey)
            .execute());
        if (!Objects.requireNonNull(response).isOk()) {
            throw new HodorClientException("DeleteJob failure, " + response.body());
        }
    }

    public void executeJob(JobDesc job) throws Exception {
        final HttpResponse response = TrySender.send(connectStringParser, (url) -> Https.createPost(url + "/scheduler/executeJob")
            .body(Jsons.toJson(job))
            .header("appName", appName)
            .header("appKey", appKey)
            .execute());
        if (!Objects.requireNonNull(response).isOk()) {
            throw new HodorClientException("Execute job failure, " + response.body());
        }
    }

    public void stopJob(JobDesc job) throws Exception {
        final HttpResponse response = TrySender.send(connectStringParser, (url) -> Https.createPost(url + "/scheduler/stopJob")
            .body(Jsons.toJson(job))
            .header("appName", appName)
            .header("appKey", appKey)
            .execute());
        if (!Objects.requireNonNull(response).isOk()) {
            throw new HodorClientException("Stop job failure, " + response.body());
        }
    }

}
