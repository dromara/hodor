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

package org.dromara.hodor.server.executor.dag;

import java.io.File;
import java.net.URL;
import org.dromara.hodor.core.dag.FlowData;
import org.dromara.hodor.core.dag.FlowDataLoader;
import org.dromara.hodor.model.job.JobKey;
import org.dromara.hodor.server.HodorServerBaseTest;
import org.dromara.hodor.server.executor.FlowJobExecutorManager;
import org.junit.Test;

/**
 * FlowJobTest
 *
 * @author tomgs
 * @since 2021/9/18
 */
public class FlowJobTest extends HodorServerBaseTest {

    @Test
    public void testAddFlowData() throws Exception {
        FlowJobExecutorManager manager = FlowJobExecutorManager.getInstance();
        File file = loadFlowFileFromResource();
        FlowDataLoader loader = new FlowDataLoader();
        FlowData flowData = loader.load(file);
        manager.putFlowData(JobKey.of("test", "flow_name"), flowData);
    }

    private File loadFlowFileFromResource() {
        //final ClassLoader loader = getClass().getClassLoader();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL resource = loader.getResource("hello_world_flow.yaml");
        assert resource != null;
        return new File(resource.getFile());
    }

}
