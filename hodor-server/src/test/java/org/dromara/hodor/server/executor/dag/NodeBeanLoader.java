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
import org.dromara.hodor.core.dag.NodeBean;
import org.yaml.snakeyaml.Yaml;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Loads NodeBean from YAML files.
 */
public class NodeBeanLoader {

    public NodeBean load(final File flowFile) throws Exception {
        //checkArgument(flowFile != null && flowFile.exists());
        //checkArgument(flowFile.getName().endsWith(Constants.FLOW_FILE_SUFFIX));

        final NodeBean nodeBean = new Yaml().loadAs(new FileInputStream(flowFile), NodeBean.class);
        if (nodeBean == null) {
            throw new RuntimeException(
                "Failed to load flow file " + flowFile.getName() + ". Node bean is null .");
        }
        if (nodeBean.getJobName() == null) {
            nodeBean.setJobName(getFlowName(flowFile));
        }
        nodeBean.setType("flow");
        for (final NodeBean node : nodeBean.getNodes()) {
            if (node.getGroupName() == null) {
                node.setGroupName(nodeBean.getGroupName());
            }
        }
        return nodeBean;
    }

    public boolean validate(final NodeBean nodeBean) {
        final Set<String> nodeNames = new HashSet<>();
        for (final NodeBean n : nodeBean.getNodes()) {
            if (!nodeNames.add(n.getJobName())) {
                // Duplicate jobs
                return false;
            }
        }

        for (final NodeBean n : nodeBean.getNodes()) {
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
