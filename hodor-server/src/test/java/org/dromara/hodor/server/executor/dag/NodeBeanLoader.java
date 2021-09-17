/*
 * Copyright 2017 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package org.dromara.hodor.server.executor.dag;

import com.google.common.io.Files;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Set;
import org.dromara.hodor.core.dag.FlowData;
import org.yaml.snakeyaml.Yaml;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Loads NodeBean from YAML files.
 */
public class NodeBeanLoader {

    public FlowData load(final File flowFile) throws Exception {
        //checkArgument(flowFile != null && flowFile.exists());
        //checkArgument(flowFile.getName().endsWith(Constants.FLOW_FILE_SUFFIX));

        final FlowData flowData = new Yaml().loadAs(new FileInputStream(flowFile), FlowData.class);
        if (flowData == null) {
            throw new RuntimeException(
                "Failed to load flow file " + flowFile.getName() + ". Node bean is null .");
        }
        if (flowData.getJobName() == null) {
            flowData.setJobName(getFlowName(flowFile));
        }
        flowData.setType("flow");
        for (final FlowData node : flowData.getNodes()) {
            if (node.getGroupName() == null) {
                node.setGroupName(flowData.getGroupName());
            }
        }
        return flowData;
    }

    public boolean validate(final FlowData flowData) {
        final Set<String> nodeNames = new HashSet<>();
        for (final FlowData n : flowData.getNodes()) {
            if (!nodeNames.add(n.getJobName())) {
                // Duplicate jobs
                return false;
            }
        }

        for (final FlowData n : flowData.getNodes()) {
            if (n.getDependsOn() != null && !nodeNames.containsAll(n.getDependsOn())) {
                // Undefined reference to dependent job
                return false;
            }
        }

        if (nodeNames.contains("ROOT")) {
            // ROOT is reserved as a special value in runtimeProperties
            return false;
        }

        return true;
    }

    public String getFlowName(final File flowFile) {
        checkArgument(flowFile != null && flowFile.exists());
        checkArgument(flowFile.getName().endsWith(".flow") || flowFile.getName().endsWith(".yaml"));

        return Files.getNameWithoutExtension(flowFile.getName());
    }
}
