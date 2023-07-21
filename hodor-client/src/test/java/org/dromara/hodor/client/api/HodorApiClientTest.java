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
import com.google.common.reflect.TypeToken;
import java.lang.reflect.Type;
import org.dromara.hodor.client.HodorApiClient;
import org.dromara.hodor.client.HodorClientConfig;
import org.dromara.hodor.common.utils.GsonUtils;
import org.dromara.hodor.common.utils.Utils;
import org.dromara.hodor.model.common.HodorResult;
import org.dromara.hodor.model.scheduler.HodorMetadata;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * HodorApiClientTest
 *
 * @author tomgs
 * @since 1.0
 */
public class HodorApiClientTest {

    @Test
    public void testJobApi() throws Exception {
        HodorClientConfig config = new HodorClientConfig();
        config.setRegistryAddress("http://localhost:8080/hodor");
        config.setAppName("appNameTest");
        config.setAppKey("appKeyTest");
        final HodorApiClient hodorApiClient = new HodorApiClient(config);
        final JobApi api = hodorApiClient.createApi(JobApi.class);
        Assertions.assertNotNull(api);
        //api.executeJob(JobKey.of("test#job"));
    }

    @Test
    public void testConvertJson() {
        String json = "{\"success\":true,\"msg\":\"success\",\"data\":{\"intervalOffsets\":[1644815308118929008,9223372036854775807],\"copySets\":[{\"id\":0,\"dataInterval\":{\"startInterval\":1644815308118929008,\"endInterval\":9223372036854775807},\"leader\":\"169.254.0.9:8081\",\"servers\":[\"169.254.0.9:8081\"]}]}}";

        Type typeToken = new TypeToken<HodorResult<HodorMetadata>>() {
        }.getType();
        final HodorResult<HodorMetadata> bean = GsonUtils.getGson().fromJson(json, typeToken);
        System.out.println(bean.toString());
        Assertions.assertNotNull(bean);

        final HodorResult<HodorMetadata> bean1 = Utils.Jsons.toBean(json, new TypeReference<HodorResult<HodorMetadata>>() {
        }.getType());
        System.out.println(bean1.toString());
        Assertions.assertNotNull(bean1);
    }

}
