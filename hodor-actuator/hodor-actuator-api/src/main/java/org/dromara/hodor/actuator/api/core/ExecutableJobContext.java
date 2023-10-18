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

package org.dromara.hodor.actuator.api.core;

import java.nio.file.Path;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import org.dromara.hodor.actuator.api.ExecutableJob;
import org.dromara.hodor.actuator.api.utils.JobPathUtils;
import org.dromara.hodor.model.enums.JobExecuteStatus;
import org.dromara.hodor.model.job.JobKey;
import org.dromara.hodor.remoting.api.message.RequestContext;
import org.dromara.hodor.remoting.api.message.request.JobExecuteRequest;

/**
 * ExecutableJobContext
 *
 * @author tomgs
 * @since 1.0
 */
@Data
@Builder
public class ExecutableJobContext {

    private Long requestId;

    private JobKey jobKey;

    private String jobCommandType;

    private String jobCommand;

    private Integer version;

    private ExecutableJob jobRunnable;

    private JobExecuteRequest executeRequest;

    private RequestContext requestContext;

    private JobExecuteStatus executeStatus;

    private Thread currentThread;

    private JobLogger jobLogger;

    // 上游数据
    private Object parentJobData;

    //上游任务的结果 Map<requestId, result>
    private Map<Long, Object> parentJobExecuteResults;

    //上游任务的状态 Map<requestId, executeStatus>
    private Map<Long, JobExecuteStatus> parentJobExecuteStatuses;

    public Path getResourcesPath(String dataPath) {
        return JobPathUtils.getResourcesPath(dataPath, jobKey, String.valueOf(executeRequest.getVersion()));
    }

    public Path getExecutionsPath(String dataPath) {
        return JobPathUtils.getExecutionsPath(dataPath, jobKey.getGroupName(), jobKey.getJobName());
    }

    public Path getAbsoluteLogPath() {
        return jobLogger.getLogPath().toAbsolutePath();
    }

}
