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

package org.dromara.hodor.actuator.jobtype.kettle;

import org.apache.logging.log4j.Logger;
import org.dromara.hodor.actuator.api.utils.Props;
import org.dromara.hodor.actuator.jobtype.api.executor.AbstractJob;

/**
 * KettleJob
 *
 * @author tomgs
 * @since 1.0
 */
public class KettleJob extends AbstractJob {

    private final String jobId;

    private final Props sysProps;

    private final Props jobProps;

    private final Logger log;

    public KettleJob(String jobId, Props sysProps, Props jobProps, Logger log) {
        super(jobId, sysProps, jobProps, log);
        this.jobId = jobId;
        this.sysProps = sysProps;
        this.jobProps = jobProps;
        this.log = log;
    }

    @Override
    public void run() throws Exception {
        // 1、设置资源库
        // 2、初始化环境
        // 3、执行job
    }

    @Override
    public void cancel() throws Exception {
        super.cancel();
    }
}
