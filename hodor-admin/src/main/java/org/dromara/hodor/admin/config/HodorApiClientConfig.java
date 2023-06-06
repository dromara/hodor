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

package org.dromara.hodor.admin.config;

import org.dromara.hodor.client.HodorApiClient;
import org.dromara.hodor.client.HodorClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * HodorApiClientConfig
 *
 * @author tomgs
 * @since 1.0
 */
@Configuration
public class HodorApiClientConfig {

    @Value("${hodor.client.registryAddress}")
    private String registryAddress;

    @Value("${hodor.client.appName}")
    private String appName;

    @Value("${hodor.client.appKey}")
    private String appKey;

    @Bean
    public HodorApiClient hodorApiClient() {
        HodorClientConfig config = new HodorClientConfig();
        config.setRegistryAddress(registryAddress);
        config.setAppName(appName);
        config.setAppKey(appKey);
        return new HodorApiClient(config);
    }
}
