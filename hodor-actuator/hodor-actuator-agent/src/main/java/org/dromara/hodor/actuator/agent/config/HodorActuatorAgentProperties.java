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

package org.dromara.hodor.actuator.agent.config;

import java.util.Map;
import org.dromara.hodor.actuator.api.config.HodorProperties;
import org.dromara.hodor.storage.api.StorageConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * HodorActuatorAgentProperties
 *
 * @author tomgs
 * @since 1.0
 */
@ConfigurationProperties(prefix = "hodor")
public class HodorActuatorAgentProperties {

    /**
     * common properties
     */
    private HodorProperties commons;

    private Map<String, Object> agentConfig;

    private Map<String, StorageConfig> storageConfig;

    public HodorProperties getCommonProperties() {
        return this.commons;
    }

    public HodorProperties getCommons() {
        return commons;
    }

    public void setCommons(HodorProperties commons) {
        this.commons = commons;
    }

    public Map<String, Object> getAgentConfig() {
        return agentConfig;
    }

    public void setAgentConfig(Map<String, Object> agentConfig) {
        this.agentConfig = agentConfig;
    }

    public Map<String, StorageConfig> getStorageConfig() {
        return storageConfig;
    }

    public void setStorageConfig(Map<String, StorageConfig> storageConfig) {
        this.storageConfig = storageConfig;
    }
}
