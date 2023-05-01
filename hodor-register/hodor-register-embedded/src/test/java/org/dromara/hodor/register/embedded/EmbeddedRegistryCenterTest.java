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

package org.dromara.hodor.register.embedded;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.register.api.RegistryConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * EmbeddedRegistryCenterTest
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class EmbeddedRegistryCenterTest {

    private EmbeddedRegistryCenter embeddedRegistryCenter;

    @Before
    public void init() throws Exception {
        RegistryConfig registryConfig = RegistryConfig.builder()
            .servers("127.0.0.1:5624")
            .namespace("test")
            .dataPath("tmp/data")
            .endpoint("127.0.0.1:5624")
            .build();

        embeddedRegistryCenter = new EmbeddedRegistryCenter();
        embeddedRegistryCenter.init(registryConfig);
    }

    @Test
    public void testInit() {
        embeddedRegistryCenter.createPersistent("/test", "test_data");
        final String data = embeddedRegistryCenter.get("/test");
        System.out.println(data);

        embeddedRegistryCenter.createPersistent("/test1/123", "test_data");
        embeddedRegistryCenter.createPersistent("/test1/124", "test_data");
        embeddedRegistryCenter.createPersistent("/test1/125", "test_data");
        final List<String> childrenData = embeddedRegistryCenter.getChildren("/test1/");
        log.info("children data: {}", childrenData);
        Assert.assertArrayEquals(new String[] {"123", "124", "125"}, childrenData.toArray(new String[0]));
    }

    @Test
    public void testGetChildren() {
        final List<String> childrenData = embeddedRegistryCenter.getChildren("/test1/");
        log.info("children data: {}", childrenData);
    }

}
