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

import org.dromara.hodor.client.HodorApiClient;
import org.dromara.hodor.client.config.HodorClientConfig;
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

}
