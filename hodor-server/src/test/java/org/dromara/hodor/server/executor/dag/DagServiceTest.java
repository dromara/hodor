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

import cn.hutool.core.lang.TypeReference;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.dromara.hodor.common.dag.Dag;
import org.dromara.hodor.common.utils.SerializeUtils;
import org.dromara.hodor.model.job.JobDesc;
import org.dromara.hodor.model.job.JobKey;
import org.dromara.hodor.server.executor.handler.DagService;
import org.junit.Test;

/**
 * DagServiceTest
 *
 * @author tomgs
 * @since 1.0
 */
public class DagServiceTest {

    private DagService dagService;

    @Test
    public void testGetRunningFlowJob() {
        Dag dagInstance = dagService.getDagInstance(JobKey.of("test", "flow_name"));
        System.out.println(dagInstance);
    }

    @Test
    public void testExecFlowDataParse() {
        TypeReference<Map<String, Map<String, Object>>> flowExecDataTypeReference = new TypeReference<Map<String, Map<String, Object>>>() {};
        String json = "{\"test#n1\":{\"nodeName\":\"n1\",\"groupName\":\"test\",\"rawData\":{\"groupName\":\"test\",\"jobName\":\"n1\",\"jobCommandType\":\"command\",\"jobCommand\":\"echo \\\"This is the first job.\\\"\",\"priority\":\"DEFAULT\",\"jobParameters\":\"123123\",\"timeout\":10,\"retryCount\":3},\"nodeStatus\":\"FAILURE\",\"nodeId\":890256557248581632},\"test#n2\":{\"nodeName\":\"n2\",\"groupName\":\"test\",\"rawData\":{\"groupName\":\"test\",\"jobName\":\"n2\",\"jobCommandType\":\"hadoopJava\",\"jobCommand\":\"echo \\\"This is the second job.\\\"\",\"priority\":\"MEDIUM\",\"jobParameters\":\"123123\",\"timeout\":10,\"retryCount\":3},\"nodeStatus\":\"CANCELED\",\"nodeId\":890256557248581633},\"test#n3\":{\"nodeName\":\"n3\",\"groupName\":\"test\",\"rawData\":{\"groupName\":\"test\",\"jobName\":\"n3\",\"jobCommandType\":\"java\",\"jobCommand\":\"echo \\\"This is the third job.\\\"\",\"priority\":\"HIGHER\",\"jobParameters\":\"123123\",\"timeout\":10,\"retryCount\":3},\"nodeStatus\":\"CANCELED\",\"nodeId\":890256557252775936}}";

        Map<String, Map<String, Object>> runningFlowExecDataMaps = SerializeUtils.deserialize(json.getBytes(StandardCharsets.UTF_8), flowExecDataTypeReference.getType());
        //Map<String, Map<String, Object>> runningFlowExecDataMaps = JSONUtil.toBean(json, flowExecDataTypeReference.getType(), true);
        System.out.println(runningFlowExecDataMaps);
        Map<String, Object> stringObjectMap = runningFlowExecDataMaps.get("test#n1");
        System.out.println(stringObjectMap);
    }

    @Test
    public void test2() {
        String json = "{\"groupName\":\"test\",\"jobName\":\"n1\",\"jobCommandType\":\"command\",\"jobCommand\":\"echo \\\"This is the first job.\\\"\",\"priority\":\"DEFAULT\",\"jobParameters\":\"123123\",\"timeout\":10,\"retryCount\":3}";
        TypeReference<JobDesc> flowExecDataTypeReference = new TypeReference<JobDesc>() {};
        JobDesc jobDesc = SerializeUtils.deserialize(json.getBytes(StandardCharsets.UTF_8), flowExecDataTypeReference.getType());
        System.out.println(jobDesc);
    }

}
