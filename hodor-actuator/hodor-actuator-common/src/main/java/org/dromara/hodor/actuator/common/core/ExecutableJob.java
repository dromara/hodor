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

package org.dromara.hodor.actuator.common.core;

import lombok.Builder;
import lombok.Data;
import org.apache.logging.log4j.Logger;
import org.dromara.hodor.actuator.common.JobExecutionContext;
import org.dromara.hodor.actuator.common.JobRunnable;
import org.dromara.hodor.model.enums.JobExecuteStatus;
import org.dromara.hodor.model.job.JobKey;
import org.dromara.hodor.remoting.api.message.RequestContext;

/**
 * ExecutableJob
 *
 * @author tomgs
 * @since 2022/1/10
 */
@Data
@Builder
public class ExecutableJob {

    private Long requestId;

    private JobKey jobKey;

    private String jobCommandType;

    private JobRunnable jobRunnable;

    private RequestContext requestContext;

    private JobExecutionContext executionContext;

    private JobExecuteStatus executeStatus;

    private Thread currentThread;

    private String dataPath;

    private Logger jobLogger;

}
