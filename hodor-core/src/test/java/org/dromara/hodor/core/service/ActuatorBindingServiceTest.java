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

package org.dromara.hodor.core.service;

import javax.annotation.Resource;
import org.dromara.hodor.core.BaseTest;
import org.junit.Test;

/**
 * ActuatorBindingServiceTest
 *
 * @author tomgs
 * @since 1.0
 */
public class ActuatorBindingServiceTest extends BaseTest {

    @Resource
    private ActuatorBindingService actuatorBindingService;

    @Test
    public void testBind() {
        String clusterName = "test";
        String groupName = "test";
        System.out.println(actuatorBindingService.bind(clusterName, groupName));
    }

    @Test
    public void testListBind() {
        System.out.println(actuatorBindingService.listBinding());
    }

    @Test
    public void testUnbind() {
        String clusterName = "test";
        String groupName = "test";
        System.out.println(actuatorBindingService.unbind(clusterName, groupName));
    }

}
