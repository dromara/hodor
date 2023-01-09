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

package org.dromara.hodor.server.manager;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.register.api.RegistryConfig;
import org.dromara.hodor.register.api.node.SchedulerNode;
import org.dromara.hodor.register.embedded.EmbeddedRegistryCenter;
import org.junit.Before;
import org.junit.Test;

/**
 * EmbeddedRegistryServiceTest
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class EmbeddedRegistryServiceTest {

    private EmbeddedRegistryCenter embeddedRegistryCenter;

    @Before
    public void init() throws Exception {
        RegistryConfig registryConfig = RegistryConfig.builder()
            .servers("127.0.0.1:2181")
            .namespace("hodor-test")
            .dataPath("E:\\data\\hodor-scheduler\\register-db")
            .endpoint("127.0.0.1:2181")
            .build();

        embeddedRegistryCenter = new EmbeddedRegistryCenter();
        embeddedRegistryCenter.init(registryConfig);
    }

    @Test
    public void testPut() {
        embeddedRegistryCenter.createEphemeral(SchedulerNode.MASTER_ACTIVE_PATH, "127.0.0.1:8081");
        embeddedRegistryCenter.createEphemeral(SchedulerNode.getServerNodePath("127.0.0.1:8081"), "127.0.0.1:8081");
        embeddedRegistryCenter.createEphemeral(SchedulerNode.getServerNodePath("127.0.0.1:8082"), "127.0.0.1:8082");
    }

    @Test
    public void testGet() {
        final String masterActivePathResult = embeddedRegistryCenter.get(SchedulerNode.MASTER_ACTIVE_PATH);
        System.out.println(masterActivePathResult);
        final String nodePathResult = embeddedRegistryCenter.get(SchedulerNode.NODES_PATH);
        System.out.println(nodePathResult);
    }

    @Test
    public void testGetChildren() {
        final List<String> childrenData = embeddedRegistryCenter.getChildren(SchedulerNode.NODES_PATH);
        log.info("children data: {}", childrenData);
    }

    @Test
    public void testRemoveKey() {
        embeddedRegistryCenter.remove(SchedulerNode.METADATA_PATH);
        embeddedRegistryCenter.remove(SchedulerNode.MASTER_ACTIVE_PATH);
    }

}
