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

package org.dromara.hodor.actuator.api.utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.dromara.hodor.actuator.api.core.JobLoggerManager;
import org.dromara.hodor.model.job.JobKey;

/**
 * JobPathUtils
 *
 * @author tomgs
 * @since 1.0
 */
public class JobPathUtils {

    private static final String EXECUTIONS = "executions";

    private static final String RESOURCES = "resources";

    public static Path getExecutionsPath(String dataPath, Long requestId) {
        return Paths.get(dataPath, EXECUTIONS, String.valueOf(requestId));
    }

    public static Path getResourcesPath(String dataPath, JobKey jobKey, String version) {
        return Paths.get(dataPath, RESOURCES, jobKey.getKeyName(), version);
    }

    public static Path getJobLogPath(String dataPath, JobKey jobKey, Long requestId) {
        return JobLoggerManager.getInstance()
            .buildJobLoggerFile(dataPath, jobKey.getGroupName(), jobKey.getJobName(), requestId)
            .toPath();
    }

}
