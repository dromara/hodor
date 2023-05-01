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

import java.io.File;
import java.net.URL;
import javax.annotation.Resource;
import org.dromara.hodor.core.BaseTest;
import org.dromara.hodor.core.dag.FlowData;
import org.dromara.hodor.core.dag.FlowDataLoader;
import org.junit.Test;

/**
 * FlowJobInfoServiceTest
 *
 * @author tomgs
 * @since 1.0
 */
public class FlowJobInfoServiceTest extends BaseTest {

    @Resource
    private FlowJobInfoService flowJobInfoService;

    @Test
    public void testAddFlowJob() throws Exception {
        File file = loadFlowFileFromResource("hello_world_flow.yaml");
        FlowDataLoader loader = new FlowDataLoader();
        FlowData flowData = loader.load(file);

        flowJobInfoService.addFlowJob(flowData);
    }

    @Test
    public void testAddDemoFlowJob() throws Exception {
        File file = loadFlowFileFromResource("hodor_client_demo_flow.yaml");
        FlowDataLoader loader = new FlowDataLoader();
        FlowData flowData = loader.load(file);

        flowJobInfoService.addFlowJob(flowData);
    }

    private File loadFlowFileFromResource(String flowName) {
        final ClassLoader loader = getClass().getClassLoader();
        //ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL resource = loader.getResource(flowName);
        assert resource != null;
        return new File(resource.getFile());
    }

}
