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

package org.dromara.hodor.server.executor.handler;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.core.Constants;
import org.dromara.hodor.remoting.api.message.RemotingResponse;
import org.dromara.hodor.remoting.api.message.response.JobExecuteResponse;
import org.dromara.hodor.scheduler.api.HodorJobExecutionContext;
import org.dromara.hodor.server.executor.MapReduceJobExecutorManager;

/**
 * HodorMapReduceJobRequestHandler
 *
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class HodorMapReduceJobRequestHandler extends HodorJobRequestHandler {

    @Override
    public void resultHandle(Map<String, Object> attachment, RemotingResponse<JobExecuteResponse> remotingResponse) {
        final String stage = (String) attachment.get(Constants.JobConstants.JOB_STAGE_KEY);
        MapReduceJobExecutorManager.getInstance().fireJobResponseHandler(stage, remotingResponse);
        super.resultHandle(attachment, remotingResponse);
    }

    @Override
    public void exceptionHandle(HodorJobExecutionContext context, Throwable t) {
        log.error("job {} request [id:{}] execute exception, msg: {}.", context.getRequestId(), context.getJobKey(), t.getMessage(), t);
        RemotingResponse<JobExecuteResponse> errorResponse = getErrorResponse(context, t);
        MapReduceJobExecutorManager.getInstance().fireJobResponseHandler(context.getStage(), errorResponse);
        super.exceptionHandle(context, t);
    }
}