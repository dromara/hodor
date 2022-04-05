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

package org.dromara.hodor.actuator.bigdata.core;

import org.apache.log4j.Logger;
import org.dromara.hodor.actuator.bigdata.executor.Job;
import org.dromara.hodor.actuator.common.ExecutableJob;
import org.dromara.hodor.actuator.common.core.ExecutableJobContext;

/**
 * BigdataExecutableJob
 *
 * @author tomgs
 * @since 2022/1/13
 */
public class BigdataExecutableJob implements ExecutableJob {

    private final Job job;
    private final Logger logger;

    public BigdataExecutableJob(final Job job, final Logger logger) {
        this.job = job;
        this.logger = logger;
    }

    @Override
    public Object execute(ExecutableJobContext executableJobContext) throws Exception {
        logger.info("job execute start ...");
        job.run();
        logger.info("job execute complete ...");
        return null;
    }

    @Override
    public void stop(ExecutableJobContext executableJobContext) throws Exception {
        job.cancel();
    }

}
